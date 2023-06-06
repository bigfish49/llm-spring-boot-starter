package cn.llm.controller;

import cn.llm.client.LLMReply;
import cn.llm.client.OpenAI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

/**
 * @author kepler
 */
@Controller
@RequestMapping("/llm")
public class LLMController {

    @Autowired
    private OpenAI llmClient;

    @GetMapping(value="/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<LLMReply> stream(@RequestParam(name = "q") String question){
        return llmClient.completeFlux(question);
    }

    @GetMapping(value="/stream/display")
    public String streamView(){
        return "stream_display";
    }


}
