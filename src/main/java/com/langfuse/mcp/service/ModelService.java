package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ModelFilterRequest;
import com.langfuse.mcp.dto.response.ModelResponse;
import com.langfuse.mcp.dto.response.MutationResponse;

public interface ModelService {
    ApiResponse<PagedResponse<ModelResponse>> listModels(ModelFilterRequest request);

    ApiResponse<ModelResponse> getModel(String modelId);

    ApiResponse<ModelResponse> createModel(String modelName, String matchPattern, String unit,
                                           Double inputPrice, Double outputPrice, Double totalPrice);

    ApiResponse<MutationResponse> deleteModel(String modelId);
}
