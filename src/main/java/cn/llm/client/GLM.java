package cn.llm.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.llm.client.core.LLMClientOptions;
import cn.llm.client.core.LLMHttpClient;
import cn.llm.client.glm.GLMCompletionRequest;
import cn.llm.core.Message;
import cn.llm.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

/**
 * @author Kepler
 * chatglm-6b
 */
@Slf4j
public class GLM extends LLMHttpClient {

    public GLM(LLMClientOptions llmOptions) {
        this.llmOptions = llmOptions;
    }

    @Override
    protected void validateResult(JSONObject retJson) {
        if (retJson.getInt("code") != 0) {
            throw new IllegalArgumentException(retJson.getStr("message"));
        }
    }

    @Override
    protected LLMReply extractReply(JSONObject retJson) {
        String data = retJson.getStr("data");
        LLMReply reply = new LLMReply();
        reply.setData(data);
        return reply;
    }

    @Override
    protected Flux<String> doComplete(String prompt, Message... chatHistory) {
        GLMCompletionRequest request = new GLMCompletionRequest();
        BeanUtil.fillBeanWithMap(this.llmOptions.toMap(),
                request, true);
        request.setMessages(Message.ofDialog(prompt, chatHistory));
        String requestBody = JacksonUtil.toStr(request, DEFAULT_MAPPER);
        Assert.isTrue(requestBody != null, "empty request!");
        log.info("llm request body = {}", requestBody);
        return executeHttpRequest(this.llmOptions.getRemoteUrl(), requestBody, null);
    }
}
