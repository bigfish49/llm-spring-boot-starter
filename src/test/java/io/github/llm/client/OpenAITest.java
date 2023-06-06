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
public class OpenAITest {

    @Autowired
    private OpenAI openAI;

    @Test
    public void sayHi(){
        String reply = openAI.complete("Classify the following news headline into 1 of the following categories: Business, Tech, Politics, Sport, Entertainment\\n\\nHeadline 1: Donna Steffensen Is Cooking Up a New Kind of Perfection. The Internet’s most beloved cooking guru has a buzzy new book and a fresh new perspective\\nCategory: Entertainment\\n\\nHeadline 2: Major Retailer Announces Plans to Close Over 100 Stores\\nCategory:");
        log.info("reply is: \n{}", reply);
    }


}
