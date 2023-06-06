package cn.llm.config;

import cn.llm.client.core.HttpClientOptions;
import cn.llm.client.core.LLMClientOptions;
import cn.llm.client.openai.ApiType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author Kepler
 * 读spring配置文件
 */
@ConfigurationProperties(prefix = "spring.llm")
@Getter
@Setter
public class LLMProperties {

    /**
     * ernieBot相关配置
     */
    private Client client = new Client();

    @Getter
    @Setter
    public static class Client extends HttpClientOptions{

        /**
         * 是否使用全局连接池
         */
        private Boolean useGlobalPool;

        private ERNIEBot ernieBot = new ERNIEBot();

        private GLM glm = new GLM();

        private OpenAI openai = new OpenAI();

    }

    @Getter
    @Setter
    public static class GLM extends LLMClientOptions {

        private Float temperature;

        private Integer maxLength;

        private Integer numBeams;

        private Boolean doSample;

        private Float topP;

    }

    @Getter
    @Setter
    public static class OpenAI extends LLMClientOptions {

        private ApiType apiType = ApiType.AZURE;

        private Float temperature;

        private String model;

        private String deploymentId;

        private String apiKey;

        private String apiVersion;

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

    @Getter
    @Setter
    public static class ERNIEBot extends LLMClientOptions {

        private Float temperature;

        private String accessToken;

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

        private String user;

    }


}
