package com.iteaj.iboot.plugin.ai.mcp.controller;

import com.iteaj.framework.result.Result;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/mcp")
public class McpToolsController {

    private final McpAsyncServer mcpSyncServer;

    public McpToolsController(McpAsyncServer mcpSyncServer) {
        this.mcpSyncServer = mcpSyncServer;
    }

    /**
     * 获取Mcp工具列表
     * @return 已经注册的工具列表
     */
    @GetMapping("/tools")
    public Result<List<McpSchema.Tool>> tools() {
//        List<McpSchema.Tool> tools = mcpSyncServer.listTools().
        return Result.success(null, "");
    }
}
