package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.DatasetItemFilterRequest;
import com.langfuse.mcp.dto.response.DatasetItemResponse;
import com.langfuse.mcp.dto.response.DatasetResponse;

public interface DatasetService {
    ApiResponse<PagedResponse<DatasetResponse>> listDatasets(Integer page, Integer limit);
    ApiResponse<DatasetResponse> getDataset(String datasetName);
    ApiResponse<PagedResponse<DatasetItemResponse>> listDatasetItems(DatasetItemFilterRequest request);
    ApiResponse<DatasetItemResponse> getDatasetItem(String itemId);
}
