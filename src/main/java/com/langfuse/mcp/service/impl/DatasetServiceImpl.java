package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetItemFilterRequest;
import com.langfuse.mcp.dto.response.DatasetItemResponse;
import com.langfuse.mcp.dto.response.DatasetResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.exception.ResourceNotFoundException;
import com.langfuse.mcp.service.DatasetService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatasetServiceImpl implements DatasetService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<DatasetResponse>> listDatasets(Integer page, Integer limit) {
        try {
            JsonNode raw = apiClient.getDatasets(page, limit);
            return ApiResponse.ok(pageMapper.mapPaged(raw, DatasetResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listDatasets error: {}", ex.getMessage());
            return ApiResponse.error("DATASET_LIST_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<DatasetResponse> getDataset(String datasetName) {
        try {
            JsonNode raw = apiClient.getDataset(datasetName);
            return ApiResponse.ok(objectMapper.treeToValue(raw, DatasetResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getDataset({}) error: {}", datasetName, ex.getMessage());
            return ApiResponse.error("DATASET_NOT_FOUND", ex.getMessage());
        } catch (Exception ex) {
            log.error("getDataset({}) mapping error", datasetName, ex);
            return ApiResponse.error("DATASET_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<DatasetResponse> createDataset(String name, String description, String metadataJson,
                                                      String inputSchemaJson, String expectedOutputSchemaJson) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("name", name);
            putIfPresent(body, "description", description);
            putParsedIfPresent(body, "metadata", metadataJson);
            putParsedIfPresent(body, "inputSchema", inputSchemaJson);
            putParsedIfPresent(body, "expectedOutputSchema", expectedOutputSchemaJson);
            JsonNode raw = apiClient.createDataset(body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, DatasetResponse.class));
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error("INVALID_INPUT", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("createDataset error: {}", ex.getMessage());
            return ApiResponse.error("DATASET_CREATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("createDataset mapping error", ex);
            return ApiResponse.error("DATASET_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<PagedResponse<DatasetItemResponse>> listDatasetItems(DatasetItemFilterRequest request) {
        try {
            JsonNode raw = apiClient.getDatasetItems(
                    request.getDatasetName(), request.getPage(), request.getLimit());
            return ApiResponse.ok(pageMapper.mapPaged(raw, DatasetItemResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listDatasetItems error: {}", ex.getMessage());
            return ApiResponse.error("DATASET_ITEMS_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<DatasetItemResponse> getDatasetItem(String itemId) {
        try {
            JsonNode raw = apiClient.getDatasetItem(itemId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, DatasetItemResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getDatasetItem({}) error: {}", itemId, ex.getMessage());
            return ApiResponse.error("DATASET_ITEM_NOT_FOUND", ex.getMessage());
        } catch (Exception ex) {
            log.error("getDatasetItem({}) mapping error", itemId, ex);
            return ApiResponse.error("DATASET_ITEM_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<DatasetItemResponse> createDatasetItem(String datasetName, String inputJson,
                                                              String expectedOutputJson, String metadataJson,
                                                              String sourceTraceId, String sourceObservationId,
                                                              String itemId, String status) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("datasetName", datasetName);
            putParsedIfPresent(body, "input", inputJson);
            putParsedIfPresent(body, "expectedOutput", expectedOutputJson);
            putParsedIfPresent(body, "metadata", metadataJson);
            putIfPresent(body, "sourceTraceId", sourceTraceId);
            putIfPresent(body, "sourceObservationId", sourceObservationId);
            putIfPresent(body, "id", itemId);
            putIfPresent(body, "status", status);
            JsonNode raw = apiClient.createDatasetItem(body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, DatasetItemResponse.class));
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error("INVALID_INPUT", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("createDatasetItem error: {}", ex.getMessage());
            return ApiResponse.error("DATASET_ITEM_CREATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("createDatasetItem mapping error", ex);
            return ApiResponse.error("DATASET_ITEM_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<MutationResponse> deleteDatasetItem(String itemId) {
        try {
            apiClient.deleteDatasetItem(itemId);
            return ApiResponse.ok(MutationResponse.builder()
                    .id(itemId)
                    .message("Dataset item deleted")
                    .build());
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error("DATASET_ITEM_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("deleteDatasetItem({}) error: {}", itemId, ex.getMessage());
            return ApiResponse.error("DATASET_ITEM_DELETE_ERROR", ex.getMessage());
        }
    }

    private void putIfPresent(Map<String, Object> body, String key, String value) {
        if (value != null && !value.isBlank()) {
            body.put(key, value);
        }
    }

    private void putParsedIfPresent(Map<String, Object> body, String key, String json) {
        if (json == null || json.isBlank()) {
            return;
        }
        try {
            body.put(key, objectMapper.readTree(json));
        } catch (Exception ex) {
            throw new IllegalArgumentException(key + " must be valid JSON");
        }
    }
}
