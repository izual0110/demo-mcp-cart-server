package com.example.demomcpcartserver;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    ToolCallbackProvider toolCallbackProvider(CartTools cartTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(cartTools)
                .build();
    }
}
