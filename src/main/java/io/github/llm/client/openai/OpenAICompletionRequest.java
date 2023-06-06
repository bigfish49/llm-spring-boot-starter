package io.github.llm.client.openai;

import io.github.llm.client.core.LLMCompletionRequest;
import io.github.llm.core.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


/**
 * @author Kepler
 */
@Getter
@Setter
public class OpenAICompletionRequest extends LLMCompletionRequest {

    private String model;

    private List<Message> messages;

    private String prompt;

    private String suffix;

    private Float topP;

    private Integer n;

    private Integer maxTokens;

    private String[] stop;

    private Boolean stream;

    private Integer logprobs;

    private Integer bestOf;

    private Map<String, Integer> logitBias;

    private Boolean echo;

    private Integer presencePenalty;

    private Integer frequencyPenalty;

    private String user;

}
