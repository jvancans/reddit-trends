package com.ui.reddittrends;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "debug")
abstract class AbstractIntegrationTest {
    @Inject
    TestRestTemplate restTemplate;

    @Value("${local.server.port}")
    private int port;

    String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testContextLoads() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10); // wait for consumer to pile up some data
    }
}
