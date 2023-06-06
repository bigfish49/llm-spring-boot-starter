package cn.llm.client.erniebot;

import cn.llm.client.core.LLMCompletionRequest;
import cn.llm.core.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @author Kepler
 * 2023-05-29 目前超参未开放, 仅需设置messages, stream, user_id
 */
@Getter
@Setter
public class ERNIEBotCompletionRequest extends LLMCompletionRequest {

    private List<Message> messages;

    private Float penaltyScore;

    private Float topP;

    /**
     * ⽣成粒度设置该值可以控制模型⽣成粒度。
     * 默认"paragraph"，可选参数为word, sentence, paragraph
     */
    private String maskType;

    /**
     * 最⼩⽣成⻓度输出结果的最⼩⻓度，避免因模型⽣成END导致⽣成⻓度过
     * 短的情况，与seq_len结合使⽤来设置⽣成⽂本的⻓度范围。
     * 默认2，取值范围：[1,seq_len]
     */
    private Integer minDecLen;

    /**
     * 最⼤⽣成⻓度输出结果的最⼤⻓度，因模型⽣成END或者遇到⽤户指定的
     * stop_token，实际返回结果可能会⼩于这个⻓度，与min_dec_len结合使
     * ⽤来控制⽣成⽂本的⻓度范围。
     * 默认1000，范围[1, 1000]
     */
    private Integer seqLen;

    private Boolean stream;

    private String userId;

}
