package com.example.demomcpcartserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Import(SecurityE2ETests.TestEndpoints.class)
class SecurityE2ETests extends AbstractSpringBootTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestIsRejectedWithUnauthorized() throws Exception {
        mockMvc.perform(get("/test/secured"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void jwtAuthenticatedRequestCanAccessSecuredEndpoint() throws Exception {
        mockMvc.perform(get("/test/secured")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject("jwt-user"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("ok"));
    }

    @TestConfiguration
    static class TestEndpoints {

        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {

        @GetMapping("/test/secured")
        String secured() {
            return "ok";
        }
    }
}
