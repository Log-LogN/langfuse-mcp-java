package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ModelFilterRequest;
import com.langfuse.mcp.dto.response.ModelResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.exception.ResourceNotFoundException;
import com.langfuse.mcp.service.ModelService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<ModelResponse>> listModels(ModelFilterRequest request) {
        try {
            JsonNode raw = apiClient.getModels(request.getPage(), request.getLimit());
            return ApiResponse.ok(pageMapper.mapPaged(raw, ModelResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listModels error: {}", ex.getMessage());
            return ApiResponse.error("MODEL_LIST_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<ModelResponse> getModel(String modelId) {
        try {
            JsonNode raw = apiClient.getModel(modelId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, ModelResponse.class));
        } catch (ResourceNotFoundException ex) {
            log.warn("getModel({}) not found", modelId);
            return ApiResponse.error("MODEL_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("getModel({}) error: {}", modelId, ex.getMessage());
            return ApiResponse.error("MODEL_FETCH_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("getModel({}) mapping error", modelId, ex);
            return ApiResponse.error("MODEL_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<ModelResponse> createModel(String modelName, String matchPattern, String unit,
                                                   Double inputPrice, Double outputPrice, Double totalPrice) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("modelName", modelName);
            body.put("matchPattern", matchPattern);
            body.put("unit", unit);
            if (inputPrice != null) body.put("inputPrice", inputPrice);
            if (outputPrice != null) body.put("outputPrice", outputPrice);
            if (totalPrice != null) body.put("totalPrice", totalPrice);
            JsonNode raw = apiClient.createModel(body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, ModelResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("createModel error: {}", ex.getMessage());
            return ApiResponse.error("MODEL_CREATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("createModel mapping error", ex);
            return ApiResponse.error("MODEL_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<MutationResponse> deleteModel(String modelId) {
        try {
            apiClient.deleteModel(modelId);
            return ApiResponse.ok(MutationResponse.builder()
                    .id(modelId).message("Model deleted").build());
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error("MODEL_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("deleteModel({}) error: {}", modelId, ex.getMessage());
            return ApiResponse.error("MODEL_DELETE_ERROR", ex.getMessage());
        }
    }
}
