package cn.llm.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.llm.client.core.LLMClientOptions;
import cn.llm.client.core.LLMHttpClient;
import cn.llm.client.erniebot.ERNIEBotCompletionRequest;
import cn.llm.core.Message;
import cn.llm.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author Kepler
 * 文心
 */
@Slf4j
public class ERNIEBot extends LLMHttpClient {

    public ERNIEBot(LLMClientOptions llmOptions) {
        this.llmOptions = llmOptions;
    }

    @Override
    protected void validateResult(JSONObject retJson) {
        if (!retJson.containsKey("result")) {
            String msg = "unknown error";
            if (retJson.containsKey("error_msg")) {
                msg = retJson.getStr("error_msg");
            }
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    protected LLMReply extractReply(JSONObject retJson) {
        LLMReply reply = new LLMReply();
        reply.setData(retJson.getStr("result"));
        reply.setEnd(retJson.getBool("is_end", Boolean.FALSE));
        return reply;
    }

    @Override
    protected Flux<String> doComplete(String prompt, Message... chatHistory) {
        ERNIEBotCompletionRequest request = new ERNIEBotCompletionRequest();
        Map<String, Object> options = this.llmOptions.toMap();
        BeanUtil.fillBeanWithMap(options, request, true);
        request.setMessages(Message.ofDialog(prompt, chatHistory));
        String accessToken = (String) options.remove("accessToken");
        Assert.hasText(accessToken, "accessToken必填");
        String requestBody = JacksonUtil.toStr(request, DEFAULT_MAPPER);
        Assert.notNull(requestBody, "empty request!");
        log.info("llm request body = {}", requestBody);
        String uri = this.llmOptions.getRemoteUrl() + "?access_token=" + accessToken;
        return executeHttpRequest(uri, requestBody, null);
    }
}
