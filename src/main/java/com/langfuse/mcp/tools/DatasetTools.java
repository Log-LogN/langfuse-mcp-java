package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetItemFilterRequest;
import com.langfuse.mcp.dto.response.DatasetItemResponse;
import com.langfuse.mcp.dto.response.DatasetResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
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
    public ApiResponse<PagedResponse<DatasetResponse>> listDatasets(@McpToolParam(description = "Page number") Integer page, @McpToolParam(description = "Items per page") Integer limit) {
        return datasetService.listDatasets(page != null ? page : 1, limit != null ? limit : 20);
    }

    @McpTool(name = "get_dataset", description = "Get a Langfuse dataset by name. Read-only.")
    public ApiResponse<DatasetResponse> getDataset(@McpToolParam(description = "Dataset name (exact match)", required = true) String datasetName) {
        if (datasetName == null || datasetName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetName is required");
        }
        return datasetService.getDataset(datasetName.strip());
    }

    @McpTool(name = "create_dataset", description = """
            Creates a new dataset in Langfuse.
            name is required.
            description is optional.
            metadataJson, inputSchemaJson, and expectedOutputSchemaJson must be valid JSON when provided.
            Returns the created dataset definition.
            """)
    public ApiResponse<DatasetResponse> createDataset(@McpToolParam(description = "Dataset name. Required.", required = true) String name, @McpToolParam(description = "Optional dataset description.") String description, @McpToolParam(description = "Optional metadata object as JSON.") String metadataJson, @McpToolParam(description = "Optional JSON Schema for item inputs, encoded as JSON.") String inputSchemaJson, @McpToolParam(description = "Optional JSON Schema for expected outputs, encoded as JSON.") String expectedOutputSchemaJson) {
        if (name == null || name.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "name is required");
        }
        return datasetService.createDataset(name.strip(), description, metadataJson, inputSchemaJson, expectedOutputSchemaJson);
    }

    @McpTool(name = "list_dataset_items", description = "List items in a dataset with pagination. Read-only.")
    public ApiResponse<PagedResponse<DatasetItemResponse>> listDatasetItems(@McpToolParam(description = "Dataset name (exact match)", required = true) String datasetName, @McpToolParam(description = "Page number") Integer page, @McpToolParam(description = "Items per page") Integer limit) {
        if (datasetName == null || datasetName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetName is required");
        }
        return datasetService.listDatasetItems(DatasetItemFilterRequest.builder().datasetName(datasetName.strip()).page(page != null ? page : 1).limit(limit != null ? limit : 20).build());
    }

    @McpTool(name = "get_dataset_item", description = "Get a single dataset item by ID. Read-only.")
    public ApiResponse<DatasetItemResponse> getDatasetItem(@McpToolParam(description = "Dataset item ID", required = true) String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "itemId is required");
        }
        return datasetService.getDatasetItem(itemId.strip());
    }

    @McpTool(name = "create_dataset_item", description = """
            Creates or upserts a dataset item in an existing dataset.
            datasetName is required.
            inputJson, expectedOutputJson, and metadataJson must be valid JSON when provided.
            Optional sourceTraceId or sourceObservationId can link the item back to Langfuse data.
            """)
    public ApiResponse<DatasetItemResponse> createDatasetItem(@McpToolParam(description = "Dataset name to add the item to. Required.", required = true) String datasetName, @McpToolParam(description = "Optional item input payload as JSON.") String inputJson, @McpToolParam(description = "Optional expected output payload as JSON.") String expectedOutputJson, @McpToolParam(description = "Optional metadata object as JSON.") String metadataJson, @McpToolParam(description = "Optional source trace ID.") String sourceTraceId, @McpToolParam(description = "Optional source observation ID.") String sourceObservationId, @McpToolParam(description = "Optional item ID for upsert semantics.") String itemId, @McpToolParam(description = "Optional item status, for example ACTIVE or ARCHIVED.") String status) {
        if (datasetName == null || datasetName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetName is required");
        }
        return datasetService.createDatasetItem(datasetName.strip(), inputJson, expectedOutputJson, metadataJson, sourceTraceId, sourceObservationId, itemId, status);
    }

    @McpTool(name = "delete_dataset_item", description = """
            Deletes a dataset item by its ID.
            This action is irreversible.
            """)
    public ApiResponse<MutationResponse> deleteDatasetItem(@McpToolParam(description = "Dataset item ID. Required.", required = true) String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "itemId is required");
        }
        return datasetService.deleteDatasetItem(itemId.strip());
    }
}
