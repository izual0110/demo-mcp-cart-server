package com.example.demomcpcartserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.github.client-id=test-client-id",
        "spring.security.oauth2.client.registration.github.client-secret=test-client-secret"
})
class DemoMcpCartServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
