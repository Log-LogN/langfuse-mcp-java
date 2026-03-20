package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.PromptFilterRequest;
import com.langfuse.mcp.dto.request.PromptGetRequest;
import com.langfuse.mcp.dto.response.PromptResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.PromptService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<PromptResponse>> listPrompts(PromptFilterRequest request) {
        try {
            JsonNode raw = apiClient.getPrompts(request.getPage(), request.getLimit());
            return ApiResponse.ok(pageMapper.mapPaged(raw, PromptResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listPrompts error: {}", ex.getMessage());
            return ApiResponse.error("PROMPT_LIST_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<PromptResponse> getPrompt(PromptGetRequest request) {
        try {
            JsonNode raw = apiClient.getPrompt(request.getName(), request.getVersion(), request.getLabel());
            return ApiResponse.ok(objectMapper.treeToValue(raw, PromptResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getPrompt({}) error: {}", request.getName(), ex.getMessage());
            return ApiResponse.error("PROMPT_NOT_FOUND", ex.getMessage());
        } catch (Exception ex) {
            log.error("getPrompt({}) mapping error", request.getName(), ex);
            return ApiResponse.error("PROMPT_MAPPING_ERROR", ex.getMessage());
        }
    }
}
