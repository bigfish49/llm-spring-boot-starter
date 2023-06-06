package cn.llm.client;

import cn.llm.core.Streamable;
import lombok.Data;
import reactor.core.publisher.Flux;

/**
 * @author Kepler
 * 大模型响应, 用于适配Flux
 */
@Data
public class LLMReply implements Streamable {

    private String data;

    private boolean end;

    /**
     * 转为String
     * @param flux 流
     */
    public static String parseString(Flux<LLMReply> flux){
        StringBuilder sb = new StringBuilder();
        for (LLMReply line : flux.toIterable()) {
            sb.append(line.getData());
        }
        return sb.toString();
    }
}
