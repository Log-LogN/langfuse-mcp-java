package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.SessionFilterRequest;
import com.langfuse.mcp.dto.response.SessionResponse;
import com.langfuse.mcp.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionTools {

    private final SessionService sessionService;

    @McpTool(name = "fetch_sessions", description = "Paginated list of all sessions with optional time range filter. Read-only.")
    public ApiResponse<PagedResponse<SessionResponse>> fetchSessions(
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page") Integer limit,
            @McpToolParam(description = "ISO-8601 start timestamp") String fromTimestamp,
            @McpToolParam(description = "ISO-8601 end timestamp") String toTimestamp) {

        return sessionService.fetchSessions(SessionFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .fromTimestamp(fromTimestamp).toTimestamp(toTimestamp)
                .build());
    }

    @McpTool(name = "get_session_details", description = "Full details of one session including all its traces. Read-only.")
    public ApiResponse<SessionResponse> getSessionDetails(
            @McpToolParam(description = "Session ID", required = true) String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "sessionId is required");
        }
        return sessionService.getSessionDetails(sessionId.strip());
    }

    @McpTool(name = "get_user_sessions", description = "All sessions for a specific user with pagination. Read-only.")
    public ApiResponse<PagedResponse<SessionResponse>> getUserSessions(
            @McpToolParam(description = "Langfuse user ID", required = true) String userId,
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page") Integer limit) {
        if (userId == null || userId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "userId is required");
        }
        return sessionService.getUserSessions(userId.strip(), SessionFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }
}
