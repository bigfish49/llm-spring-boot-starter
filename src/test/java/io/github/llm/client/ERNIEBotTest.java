package io.github.llm.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author kepler
 */
@SpringBootTest
@Slf4j
public class ERNIEBotTest {

    @Autowired
    private ERNIEBot ernieBot;

    @Test
    public void sayHi(){
        String reply = ernieBot.complete("张三年龄是28，性别男。请仅输出json就可以");
        log.info("reply is: \n{}", reply);
    }


}
