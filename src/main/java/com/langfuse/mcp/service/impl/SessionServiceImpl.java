package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.SessionFilterRequest;
import com.langfuse.mcp.dto.response.SessionResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.SessionService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<SessionResponse>> fetchSessions(SessionFilterRequest request) {
        try {
            JsonNode raw = apiClient.getSessions(
                    request.getPage(), request.getLimit(),
                    request.getFromTimestamp(), request.getToTimestamp(), request.getUserId());
            return ApiResponse.ok(pageMapper.mapPaged(raw, SessionResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("fetchSessions error: {}", ex.getMessage());
            return ApiResponse.error("SESSION_FETCH_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<SessionResponse> getSessionDetails(String sessionId) {
        try {
            JsonNode raw = apiClient.getSession(sessionId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, SessionResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getSessionDetails({}) error: {}", sessionId, ex.getMessage());
            return ApiResponse.error("SESSION_NOT_FOUND", ex.getMessage());
        } catch (Exception ex) {
            log.error("getSessionDetails({}) mapping error", sessionId, ex);
            return ApiResponse.error("SESSION_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<PagedResponse<SessionResponse>> getUserSessions(
            String userId, SessionFilterRequest request) {
        try {
            JsonNode raw = apiClient.getSessions(
                    request.getPage(), request.getLimit(),
                    request.getFromTimestamp(), request.getToTimestamp(), userId);
            return ApiResponse.ok(pageMapper.mapPaged(raw, SessionResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getUserSessions({}) error: {}", userId, ex.getMessage());
            return ApiResponse.error("USER_SESSION_FETCH_ERROR", ex.getMessage());
        }
    }
}
