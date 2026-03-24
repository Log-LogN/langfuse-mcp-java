package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetItemFilterRequest;
import com.langfuse.mcp.dto.response.DatasetItemResponse;
import com.langfuse.mcp.dto.response.DatasetResponse;
import com.langfuse.mcp.dto.response.MutationResponse;

public interface DatasetService {
    ApiResponse<PagedResponse<DatasetResponse>> listDatasets(Integer page, Integer limit);

    ApiResponse<DatasetResponse> getDataset(String datasetName);

    ApiResponse<DatasetResponse> createDataset(String name, String description, String metadataJson,
                                               String inputSchemaJson, String expectedOutputSchemaJson);

    ApiResponse<PagedResponse<DatasetItemResponse>> listDatasetItems(DatasetItemFilterRequest request);

    ApiResponse<DatasetItemResponse> getDatasetItem(String itemId);

    ApiResponse<DatasetItemResponse> createDatasetItem(String datasetName, String inputJson,
                                                       String expectedOutputJson, String metadataJson,
                                                       String sourceTraceId, String sourceObservationId,
                                                       String itemId, String status);

    ApiResponse<MutationResponse> deleteDatasetItem(String itemId);
}
