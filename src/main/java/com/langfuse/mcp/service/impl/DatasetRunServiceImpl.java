package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetRunFilterRequest;
import com.langfuse.mcp.dto.response.DatasetRunItemResponse;
import com.langfuse.mcp.dto.response.DatasetRunResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.exception.ResourceNotFoundException;
import com.langfuse.mcp.service.DatasetRunService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatasetRunServiceImpl implements DatasetRunService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<DatasetRunResponse>> listDatasetRuns(DatasetRunFilterRequest request) {
        try {
            JsonNode raw = apiClient.getDatasetRuns(
                    request.getDatasetName(), request.getPage(), request.getLimit());
            return ApiResponse.ok(pageMapper.mapPaged(raw, DatasetRunResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listDatasetRuns error: {}", ex.getMessage());
            return ApiResponse.error("DATASET_RUN_LIST_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<DatasetRunResponse> getDatasetRun(String datasetName, String runName) {
        try {
            JsonNode raw = apiClient.getDatasetRun(datasetName, runName);
            return ApiResponse.ok(objectMapper.treeToValue(raw, DatasetRunResponse.class));
        } catch (ResourceNotFoundException ex) {
            log.warn("getDatasetRun({}/{}) not found", datasetName, runName);
            return ApiResponse.error("DATASET_RUN_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("getDatasetRun({}/{}) error: {}", datasetName, runName, ex.getMessage());
            return ApiResponse.error("DATASET_RUN_FETCH_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("getDatasetRun mapping error", ex);
            return ApiResponse.error("DATASET_RUN_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<MutationResponse> deleteDatasetRun(String datasetName, String runName) {
        try {
            apiClient.deleteDatasetRun(datasetName, runName);
            return ApiResponse.ok(MutationResponse.builder()
                    .message("Dataset run '" + runName + "' deleted from dataset '" + datasetName + "'").build());
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error("DATASET_RUN_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("deleteDatasetRun error: {}", ex.getMessage());
            return ApiResponse.error("DATASET_RUN_DELETE_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<PagedResponse<DatasetRunItemResponse>> listDatasetRunItems(String datasetId,
                                                                                   String runName,
                                                                                   Integer page,
                                                                                   Integer limit) {
        try {
            JsonNode raw = apiClient.getDatasetRunItems(datasetId, runName, page, limit);
            return ApiResponse.ok(pageMapper.mapPaged(raw, DatasetRunItemResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listDatasetRunItems error: {}", ex.getMessage());
            return ApiResponse.error("DATASET_RUN_ITEMS_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<DatasetRunItemResponse> createDatasetRunItem(String runName, String datasetItemId,
                                                                    String traceId, String observationId,
                                                                    String runDescription, String metadataJson,
                                                                    String datasetVersion, String createdAt) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("runName", runName);
            body.put("datasetItemId", datasetItemId);
            putIfPresent(body, "traceId", traceId);
            putIfPresent(body, "observationId", observationId);
            putIfPresent(body, "runDescription", runDescription);
            putIfPresent(body, "datasetVersion", datasetVersion);
            putIfPresent(body, "createdAt", createdAt);
            if (metadataJson != null && !metadataJson.isBlank()) {
                try {
                    body.put("metadata", objectMapper.readTree(metadataJson));
                } catch (Exception ex) {
                    return ApiResponse.error("INVALID_INPUT", "metadataJson must be valid JSON");
                }
            }
            JsonNode raw = apiClient.createDatasetRunItem(body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, DatasetRunItemResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("createDatasetRunItem error: {}", ex.getMessage());
            return ApiResponse.error("DATASET_RUN_ITEM_CREATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("createDatasetRunItem mapping error", ex);
            return ApiResponse.error("DATASET_RUN_ITEM_MAPPING_ERROR", ex.getMessage());
        }
    }

    private void putIfPresent(Map<String, Object> body, String key, String value) {
        if (value != null && !value.isBlank()) {
            body.put(key, value);
        }
    }
}
