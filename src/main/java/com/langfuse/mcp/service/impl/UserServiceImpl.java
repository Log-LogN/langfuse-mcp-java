package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.TraceFilterRequest;
import com.langfuse.mcp.dto.request.UserFilterRequest;
import com.langfuse.mcp.dto.response.TraceResponse;
import com.langfuse.mcp.dto.response.UserResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.UserService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final LangfuseApiClient apiClient;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<TraceResponse>> getUserTraces(
            String userId, TraceFilterRequest request) {
        try {
            JsonNode raw = apiClient.getTraces(
                    request.getPage(), request.getLimit(), userId,
                    null, null, null, null, null);
            return ApiResponse.ok(pageMapper.mapPaged(raw, TraceResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getUserTraces({}) error: {}", userId, ex.getMessage());
            return ApiResponse.error("USER_TRACES_ERROR", ex.getMessage());
        }
    }
}
