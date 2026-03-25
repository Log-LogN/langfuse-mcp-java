package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.response.CostMetricsResponse;

public interface CostMetricsService {

    /**
     * Execute a metrics query against the Langfuse Metrics API.
     *
     * @param queryJson  raw JSON string passed as the {@code ?query=} parameter — exactly as the agent constructs it
     * @return aggregated metrics rows wrapped in an {@link ApiResponse}
     */
    ApiResponse<CostMetricsResponse> getMetrics(String queryJson);
}
