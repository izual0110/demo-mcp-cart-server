package com.example.demomcpcartserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class McpE2ETests extends AbstractSpringBootTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authenticatedUserCanInitializeMcpListToolsAndCallTool() throws Exception {
        MvcResult initializeResult = mockMvc.perform(post("/mcp")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject("mcp-user")))
                        .accept(MediaType.TEXT_EVENT_STREAM, MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 1,
                                  "method": "initialize",
                                  "params": {
                                    "protocolVersion": "2025-06-18",
                                    "capabilities": {},
                                    "clientInfo": {
                                      "name": "mcp-e2e-test",
                                      "version": "1.0.0"
                                    }
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String sessionId = initializeResult.getResponse().getHeader("Mcp-Session-Id");
        assertThat(sessionId).isNotBlank();

        assertThat(initializeResult.getResponse().getContentAsString()).contains("\"serverInfo\"", "demo-cart-mcp");

        MvcResult toolsListResult = mockMvc.perform(post("/mcp")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject("mcp-user")))
                        .header("Mcp-Session-Id", sessionId)
                        .accept(MediaType.TEXT_EVENT_STREAM, MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 2,
                                  "method": "tools/list"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(toolsListResult.getResponse().getContentAsString()).contains("addToCart", "removeFromCart", "getCart", "clearCart");

        MvcResult callToolResult = mockMvc.perform(post("/mcp")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject("mcp-user")))
                        .header("Mcp-Session-Id", sessionId)
                        .accept(MediaType.TEXT_EVENT_STREAM, MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 3,
                                  "method": "tools/call",
                                  "params": {
                                    "name": "addToCart",
                                    "arguments": {
                                      "sku": "SKU-1",
                                      "name": "Blue mug",
                                      "quantity": 2
                                    }
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(callToolResult.getResponse().getContentAsString()).contains("SKU-1", "Blue mug", "2");
    }
}
