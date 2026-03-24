package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.SessionFilterRequest;
import com.langfuse.mcp.dto.response.SessionResponse;

public interface SessionService {
    ApiResponse<PagedResponse<SessionResponse>> fetchSessions(SessionFilterRequest request);

    ApiResponse<SessionResponse> getSessionDetails(String sessionId);

    ApiResponse<PagedResponse<SessionResponse>> getUserSessions(String userId, SessionFilterRequest request);
}
