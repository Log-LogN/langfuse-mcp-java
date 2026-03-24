package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.response.LlmConnectionResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.LlmConnectionService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmConnectionServiceImpl implements LlmConnectionService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<LlmConnectionResponse>> listLlmConnections(Integer page, Integer limit) {
        try {
            JsonNode raw = apiClient.getLlmConnections(page, limit);
            return ApiResponse.ok(pageMapper.mapPaged(raw, LlmConnectionResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listLlmConnections error: {}", ex.getMessage());
            return ApiResponse.error("LLM_CONNECTION_LIST_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<LlmConnectionResponse> upsertLlmConnection(String provider, String secretKey,
                                                                    String adapter) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("provider", provider);
            body.put("secretKey", secretKey);
            body.put("adapter", adapter);
            JsonNode raw = apiClient.upsertLlmConnection(body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, LlmConnectionResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("upsertLlmConnection error: {}", ex.getMessage());
            return ApiResponse.error("LLM_CONNECTION_UPSERT_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("upsertLlmConnection mapping error", ex);
            return ApiResponse.error("LLM_CONNECTION_MAPPING_ERROR", ex.getMessage());
        }
    }
}
