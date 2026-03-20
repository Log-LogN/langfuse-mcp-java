package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ObservationFilterRequest;
import com.langfuse.mcp.dto.response.ObservationResponse;

public interface ObservationService {
    ApiResponse<PagedResponse<ObservationResponse>> fetchObservations(ObservationFilterRequest request);
    ApiResponse<ObservationResponse> fetchObservation(String observationId);
}
