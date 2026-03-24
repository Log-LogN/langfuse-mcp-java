package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetRunFilterRequest;
import com.langfuse.mcp.dto.response.DatasetRunItemResponse;
import com.langfuse.mcp.dto.response.DatasetRunResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.service.DatasetRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatasetRunTools {

    private final DatasetRunService datasetRunService;

    @McpTool(name = "list_dataset_runs", description = """
            Returns a paginated list of runs for a specific dataset.
            Each run represents one experiment executed against a dataset.
            Returns: id, name, datasetId, datasetName, metadata, createdAt, updatedAt.
            datasetName is required. Pagination: page 1-based (default 1), limit (default 20).
            """)
    public ApiResponse<PagedResponse<DatasetRunResponse>> listDatasetRuns(
            @McpToolParam(description = "Dataset name (exact match). Required.", required = true) String datasetName,
            @McpToolParam(description = "Page number, 1-based. Omit to use default (1).") Integer page,
            @McpToolParam(description = "Results per page. Omit to use default (20).") Integer limit) {
        if (datasetName == null || datasetName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetName is required");
        }
        return datasetRunService.listDatasetRuns(DatasetRunFilterRequest.builder()
                .datasetName(datasetName.strip())
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }

    @McpTool(name = "get_dataset_run", description = """
            Returns a single dataset run including all its run items.
            Each run item links a dataset item to a trace and optional observation.
            Returns: id, name, datasetName, metadata, createdAt, updatedAt, datasetRunItems[].
            Both datasetName and runName are required.
            """)
    public ApiResponse<DatasetRunResponse> getDatasetRun(
            @McpToolParam(description = "Dataset name (exact match). Required.", required = true) String datasetName,
            @McpToolParam(description = "Run name (exact match). Required.", required = true) String runName) {
        if (datasetName == null || datasetName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetName is required");
        }
        if (runName == null || runName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "runName is required");
        }
        return datasetRunService.getDatasetRun(datasetName.strip(), runName.strip());
    }

    @McpTool(name = "delete_dataset_run", description = """
            Deletes a dataset run and all its run items. This action is irreversible.
            Use this to clean up experiment runs you no longer need.
            Both datasetName and runName are required.
            """)
    public ApiResponse<MutationResponse> deleteDatasetRun(
            @McpToolParam(description = "Dataset name (exact match). Required.", required = true) String datasetName,
            @McpToolParam(description = "Run name to delete (exact match). Required.", required = true) String runName) {
        if (datasetName == null || datasetName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetName is required");
        }
        if (runName == null || runName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "runName is required");
        }
        return datasetRunService.deleteDatasetRun(datasetName.strip(), runName.strip());
    }

    @McpTool(name = "list_dataset_run_items", description = """
            Returns a paginated list of items in a specific dataset run.
            Each run item links a dataset item to a trace and optional observation for evaluation.
            Returns: id, datasetRunId, datasetRunName, datasetItemId, traceId, observationId, createdAt.
            Both datasetId and runName are required.
            """)
    public ApiResponse<PagedResponse<DatasetRunItemResponse>> listDatasetRunItems(
            @McpToolParam(description = "Dataset ID (UUID). Required.", required = true) String datasetId,
            @McpToolParam(description = "Run name (exact match). Required.", required = true) String runName,
            @McpToolParam(description = "Page number, 1-based. Omit to use default (1).") Integer page,
            @McpToolParam(description = "Results per page. Omit to use default (20).") Integer limit) {
        if (datasetId == null || datasetId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetId is required");
        }
        if (runName == null || runName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "runName is required");
        }
        return datasetRunService.listDatasetRunItems(
                datasetId.strip(), runName.strip(),
                page != null ? page : 1,
                limit != null ? limit : 20);
    }

    @McpTool(name = "create_dataset_run_item", description = """
            Creates a dataset run item and creates or updates the dataset run if needed.
            runName and datasetItemId are required.
            traceId is strongly recommended and observationId is optional.
            metadataJson must be valid JSON when provided.
            """)
    public ApiResponse<DatasetRunItemResponse> createDatasetRunItem(
            @McpToolParam(description = "Run name. Required.", required = true) String runName,
            @McpToolParam(description = "Dataset item ID to evaluate in this run. Required.", required = true) String datasetItemId,
            @McpToolParam(description = "Optional trace ID associated with the run item.") String traceId,
            @McpToolParam(description = "Optional observation ID associated with the run item.") String observationId,
            @McpToolParam(description = "Optional run description.") String runDescription,
            @McpToolParam(description = "Optional run metadata as JSON.") String metadataJson,
            @McpToolParam(description = "Optional dataset version timestamp in ISO-8601 format.") String datasetVersion,
            @McpToolParam(description = "Optional createdAt timestamp in ISO-8601 format.") String createdAt) {
        if (runName == null || runName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "runName is required");
        }
        if (datasetItemId == null || datasetItemId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "datasetItemId is required");
        }
        return datasetRunService.createDatasetRunItem(runName.strip(), datasetItemId.strip(), traceId,
                observationId, runDescription, metadataJson, datasetVersion, createdAt);
    }
}
