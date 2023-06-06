package cn.llm.client.glm;

import cn.llm.client.core.LLMCompletionRequest;
import cn.llm.core.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @author Kepler
 */
@Getter
@Setter
public class GLMCompletionRequest extends LLMCompletionRequest {

    private Integer maxLength;

    private Integer numBeams;

    private Boolean doSample;

    private Float topP;

    private List<Message> messages;

}
