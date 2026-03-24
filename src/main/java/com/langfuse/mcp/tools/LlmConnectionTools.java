package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.response.LlmConnectionResponse;
import com.langfuse.mcp.service.LlmConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LlmConnectionTools {

    private final LlmConnectionService llmConnectionService;

    @McpTool(name = "list_llm_connections", description = """
            Returns a paginated list of LLM provider connections configured in the Langfuse project.
            Each connection contains: id, provider, displaySecretKey (masked), baseURL, config.
            LLM connections define the provider credentials used by Langfuse for evaluations and playground.
            Pagination: page is 1-based (default 1), limit controls page size (default 20).
            """)
    public ApiResponse<PagedResponse<LlmConnectionResponse>> listLlmConnections(
            @McpToolParam(description = "Page number, 1-based. Omit to use default (1).") Integer page,
            @McpToolParam(description = "Results per page. Omit to use default (20).") Integer limit) {
        return llmConnectionService.listLlmConnections(
                page != null ? page : 1,
                limit != null ? limit : 20);
    }

    @McpTool(name = "upsert_llm_connection", description = """
            Creates or updates an LLM provider connection (upserted by provider name).
            If a connection for the given provider already exists, it is updated.
            provider and secretKey are required.
            provider examples: openai, anthropic, azure, google.
            """)
    public ApiResponse<LlmConnectionResponse> upsertLlmConnection(
            @McpToolParam(description = "Provider name, e.g. openai, anthropic, azure, google. Required.", required = true) String provider,
            @McpToolParam(description = "API secret key for the provider. Required.", required = true) String secretKey,
            @McpToolParam(description = "Adapter name. ") String adapter
    ) {
        if (provider == null || provider.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "provider is required");
        }
        if (secretKey == null || secretKey.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "secretKey is required");
        }
        return llmConnectionService.upsertLlmConnection(
                provider.strip(), secretKey.strip(), adapter.strip());
    }
}
