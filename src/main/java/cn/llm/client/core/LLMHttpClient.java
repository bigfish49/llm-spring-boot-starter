package cn.llm.client.core;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.llm.client.LLMReply;
import cn.llm.core.Message;
import cn.llm.error.ExceptionCode;
import cn.llm.error.LLMException;
import cn.llm.util.JacksonUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * LLMClient通用功能
 */
@Slf4j
@Setter
public abstract class LLMHttpClient implements Closeable {

    public static final ObjectMapper DEFAULT_MAPPER;

    static {
        DEFAULT_MAPPER = JacksonUtil.MAPPER.copy();
        // 空值不输出
        DEFAULT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 驼峰转下划线格式
        DEFAULT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    protected LLMClientOptions llmOptions;

    protected ConnectionProvider connProvider;

    public void init() {
        try {
            if (connProvider == null) {
                connProvider = this.llmOptions.constructConnProvider();
            }
        } catch (Exception e) {
            log.error("initPool error.", e);
        }
    }

    protected WebClient createWebClient() {
        HttpClient client = HttpClient.create(connProvider)
                .responseTimeout(Duration.ofMillis(llmOptions.getResponseTimeout()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, llmOptions.getConnectTimeout())
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE.equals(llmOptions.getSoKeepAlive()))
                .option(EpollChannelOption.TCP_KEEPIDLE, llmOptions.getTcpKeepIdle())
                .option(EpollChannelOption.TCP_KEEPINTVL, llmOptions.getTcpKeepInterval())
                .option(EpollChannelOption.TCP_KEEPCNT, llmOptions.getTcpKeepCnt());


        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }

    protected Flux<String> executeHttpRequest(String uri, String requestBody, Consumer<HttpHeaders> headersConsumer) {
        try {
            WebClient.RequestBodySpec requestBodySpec = createWebClient()
                    .post()
                    .uri(uri);
            if (null != headersConsumer) {
                requestBodySpec.headers(headersConsumer);
            }
            return requestBodySpec
                    .acceptCharset(StandardCharsets.UTF_8)
                    .bodyValue(Objects.requireNonNull(requestBody))
                    .retrieve()
                    .bodyToFlux(String.class)
                    .retryWhen(Retry.backoff(this.llmOptions.getRetryCount(), Duration.ofSeconds(5)));
        } catch (Exception e) {
            log.error("send http error, request={}.", requestBody, e);
            throw new LLMException(ExceptionCode.LLM_SEND_HTTP);
        }
    }

    /**
     * 核心方法, 聊天补全
     *
     * @param chatHistory 聊天记录
     * @return 流式
     */
    public Flux<LLMReply> completeFlux(String prompt, Message... chatHistory) {
        Flux<String> rawResult = this.doComplete(prompt, chatHistory);
        log.info("llm response body = {}", rawResult);
        return rawResult.map(this::handleReply);
    }

    /**
     * 核心方法, 聊天补全
     *
     * @param chatHistory 聊天记录
     * @return LLM回复内容
     */
    public String complete(String prompt, Message... chatHistory) {
        return LLMReply.parseString(completeFlux(prompt, chatHistory));
    }

    /**
     * 结果处理
     */
    private LLMReply handleReply(String line) {
        try {
            JSONObject retJson = JSONUtil.parseObj(line);
            Assert.notNull(retJson, "json in response is null");
            validateResult(retJson);
            return extractReply(retJson);
        } catch (RuntimeException e) {
            log.error("llm result invalid, request={}.", line, e);
            throw new LLMException(ExceptionCode.LLM_RESULT_VALID);
        }
    }

    /**
     * 校验响应json
     */
    protected abstract void validateResult(JSONObject retJson);

    /**
     * 提取大模型回复
     */
    protected abstract LLMReply extractReply(JSONObject retJson);

    protected abstract Flux<String> doComplete(String prompt, Message... chatHistory);

    @Override
    public void close() {
        if (null != connProvider) {
            this.connProvider.dispose();
        }
    }
}
