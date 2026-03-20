package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.PromptFilterRequest;
import com.langfuse.mcp.dto.request.PromptGetRequest;
import com.langfuse.mcp.dto.response.PromptResponse;
import com.langfuse.mcp.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptTools {

    private final PromptService promptService;

    @McpTool(name = "list_prompts", description = "List all prompts in the Langfuse project with pagination. Read-only.")
    public ApiResponse<PagedResponse<PromptResponse>> listPrompts(
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page") Integer limit) {

        return promptService.listPrompts(PromptFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }

    @McpTool(name = "get_prompt", description = """
            Fetch a specific prompt by name.
            Optionally pin to a version number or a label (e.g. 'production', 'staging').
            Returns: name, version, type (text|chat), prompt content, labels, tags, config.
            Read-only.
            """)
    public ApiResponse<PromptResponse> getPrompt(
            @McpToolParam(description = "Prompt name (exact match)", required = true) String name,
            @McpToolParam(description = "Version number — omit for latest") Integer version,
            @McpToolParam(description = "Label, e.g. 'production' or 'staging'") String label) {
        if (name == null || name.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "name is required");
        }
        return promptService.getPrompt(PromptGetRequest.builder()
                .name(name.strip()).version(version).label(label)
                .build());
    }
}
