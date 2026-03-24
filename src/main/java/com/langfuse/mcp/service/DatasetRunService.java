package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetRunFilterRequest;
import com.langfuse.mcp.dto.response.DatasetRunItemResponse;
import com.langfuse.mcp.dto.response.DatasetRunResponse;
import com.langfuse.mcp.dto.response.MutationResponse;

public interface DatasetRunService {
    ApiResponse<PagedResponse<DatasetRunResponse>> listDatasetRuns(DatasetRunFilterRequest request);

    ApiResponse<DatasetRunResponse> getDatasetRun(String datasetName, String runName);

    ApiResponse<MutationResponse> deleteDatasetRun(String datasetName, String runName);

    ApiResponse<PagedResponse<DatasetRunItemResponse>> listDatasetRunItems(String datasetId,
                                                                           String runName,
                                                                           Integer page,
                                                                           Integer limit);

    ApiResponse<DatasetRunItemResponse> createDatasetRunItem(String runName, String datasetItemId,
                                                             String traceId, String observationId,
                                                             String runDescription, String metadataJson,
                                                             String datasetVersion, String createdAt);
}
