package cn.llm.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import cn.llm.client.core.LLMClientOptions;
import cn.llm.client.core.LLMHttpClient;
import cn.llm.client.openai.ApiType;
import cn.llm.client.openai.OpenAICompletionRequest;
import cn.llm.core.Message;
import cn.llm.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author Kepler
 * openai
 * 支持azure及openai方式
 */
@Slf4j
public class OpenAI extends LLMHttpClient {

    public OpenAI(LLMClientOptions llmOptions) {
        this.llmOptions = llmOptions;
    }

    @Override
    protected void validateResult(JSONObject retJson) {
        if (!retJson.containsKey("choices")) {
            String msg = "unknown error";
            if (retJson.containsKey("error")) {
                msg = retJson.getJSONObject("error").getStr("message");
            }
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    protected LLMReply extractReply(JSONObject retJson) {
        String data = retJson.getJSONArray("choices")
                .getJSONObject(0)
                .getStr("text");
        LLMReply reply = new LLMReply();
        reply.setData(data);
        return reply;
    }

    @Override
    protected Flux<String> doComplete(String prompt, Message[] chatHistory) {
        OpenAICompletionRequest request = new OpenAICompletionRequest();
        Map<String, Object> options = this.llmOptions.toMap();
        BeanUtil.fillBeanWithMap(options, request, true);
        ApiType apiType = (ApiType) options.remove("apiType");
        String url;
        if (ApiType.AZURE.equals(apiType)) {
            // TODO add chat history
            request.setPrompt(prompt);
            String deploymentId = (String) options.remove("deploymentId");
            Assert.hasText(deploymentId, "deploymentId必填");
            String apiVersion = (String) options.remove("apiVersion");
            Assert.hasText(apiVersion, "apiVersion必填");
            url = String.format("%s/openai/deployments/%s/completions?api-version=%s",
                    this.llmOptions.getRemoteUrl(), deploymentId, apiVersion);
        } else {
            request.setMessages(Message.ofDialog(prompt, chatHistory));
            String model = (String)options.get("model");
            Assert.hasText(model, "model必填");
            url = String.format("%s/v1/chat/completions", this.llmOptions.getRemoteUrl());
        }
        String requestBody = JacksonUtil.toStr(request, DEFAULT_MAPPER);
        Assert.isTrue(requestBody != null, "empty request!");
        log.info("llm request body = {}", requestBody);
        String apiKey = (String) options.remove("apiKey");

        return executeHttpRequest(url, requestBody, headers->{
            if (ApiType.AZURE.equals(apiType)) {
                headers.add("api-key", apiKey);
            } else {
                headers.add("Authorization", "Bearer "+apiKey);
            }
        });
    }
}
