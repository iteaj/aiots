package com.iteaj.iboot.plugin.ai.autoconfig;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.iboot.plugin.ai.mcp.iot.IotMcpToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan("com.iteaj.iboot.plugin.ai.mcp")
public class McpToolAutoConfiguration {

    @Bean
    public OrderFilterChainDefinition  mcpDefinition() {
        return new OrderFilterChainDefinition().addAnon("/ai/mcp/**", "/ai/sse", "/ai/mcp/msg");
    }

    @Bean
    public ToolCallbackProvider openLibraryTools(IotMcpToolService iotMcpToolService) {
        return MethodToolCallbackProvider.builder().toolObjects(iotMcpToolService).build();
    }
}
