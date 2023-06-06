package io.github.llm.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author kepler
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class HelloControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void hello(){
        WebTestClient.ResponseSpec exchange = webTestClient.get().uri("/hello")
                .exchange();
        log.info(exchange.expectBody().returnResult().toString());
        exchange.expectStatus()
                .is2xxSuccessful();
    }

}
