package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.TraceFilterRequest;
import com.langfuse.mcp.dto.request.UserFilterRequest;
import com.langfuse.mcp.dto.response.TraceResponse;
import com.langfuse.mcp.dto.response.UserResponse;
import com.langfuse.mcp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTools {

    private final UserService userService;

    @McpTool(name = "get_user_traces", description = "All traces for a specific user with pagination. Read-only.")
    public ApiResponse<PagedResponse<TraceResponse>> getUserTraces(
            @McpToolParam(description = "Langfuse user ID", required = true) String userId,
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page") Integer limit) {
        if (userId == null || userId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "userId is required");
        }
        return userService.getUserTraces(userId.strip(), TraceFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }
}
