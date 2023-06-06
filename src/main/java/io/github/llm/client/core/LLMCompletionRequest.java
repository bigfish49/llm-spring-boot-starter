package io.github.llm.client.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Kepler
 * LLM complete api 请求体
 */
@Getter
@Setter
public class LLMCompletionRequest implements Serializable {

    private Float temperature = 0.0f;

}
