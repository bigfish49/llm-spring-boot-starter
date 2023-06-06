# llm spring boot starter
llm(large language model) with spring boot! stream feature supported using webflux

## 安装
```shell
mvn clean -U install -Dmaven.test.skip=true
```

## 快速开始
mvn dependency
```xml
<dependency>
   <groupId>cn.llm</groupId>
   <artifactId>llm-spring-boot-starter</artifactId>
   <version>2.7.0-SNAPSHOT</version>   
</dependency>
```
2.7.0也是spring-boot的版本
使用
```java
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

}

```

## 测试
1. 运行 cn.llm.App
2. 访问 http://localhost:8080/llm/stream/display

## 如何贡献
plz contact me

## 讨论
