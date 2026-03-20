package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetItemFilterRequest;
import com.langfuse.mcp.dto.response.DatasetItemResponse;
import com.langfuse.mcp.dto.response.DatasetResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.DatasetService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
