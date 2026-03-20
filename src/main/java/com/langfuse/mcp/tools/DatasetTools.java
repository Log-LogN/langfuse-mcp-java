package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetItemFilterRequest;
import com.langfuse.mcp.dto.response.DatasetItemResponse;
import com.langfuse.mcp.dto.response.DatasetResponse;
import com.langfuse.mcp.service.DatasetService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatasetTools {

    private final DatasetService datasetService;

    @McpTool(name = "list_datasets", description = "List all evaluation datasets in the Langfuse project. Read-only.")
    public ApiResponse<PagedResponse<DatasetResponse>> listDatasets(
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page") Integer limit) {
        return datasetService.listDatasets(
                page != null ? page : 1,
                limit != null ? limit : 20);
    }

    @McpTool(name = "get_dataset", description = "Get a Langfuse dataset by name. Read-only.")
    public ApiResponse<DatasetResponse> getDataset(
            @McpToolParam(description = "Dataset name (exact match)", required = true) String datasetName) {
        if (datasetName == null || datasetName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetName is required");
        }
        return datasetService.getDataset(datasetName.strip());
    }

    @McpTool(name = "list_dataset_items", description = "List items in a dataset with pagination. Read-only.")
    public ApiResponse<PagedResponse<DatasetItemResponse>> listDatasetItems(
            @McpToolParam(description = "Dataset name (exact match)", required = true) String datasetName,
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page") Integer limit) {
        if (datasetName == null || datasetName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetName is required");
        }
        return datasetService.listDatasetItems(DatasetItemFilterRequest.builder()
                .datasetName(datasetName.strip())
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }

    @McpTool(name = "get_dataset_item", description = "Get a single dataset item by ID. Read-only.")
    public ApiResponse<DatasetItemResponse> getDatasetItem(
            @McpToolParam(description = "Dataset item ID", required = true) String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "itemId is required");
        }
        return datasetService.getDatasetItem(itemId.strip());
    }
}
