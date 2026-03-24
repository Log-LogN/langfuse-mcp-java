package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.response.LlmConnectionResponse;

public interface LlmConnectionService {
    ApiResponse<PagedResponse<LlmConnectionResponse>> listLlmConnections(Integer page, Integer limit);

    ApiResponse<LlmConnectionResponse> upsertLlmConnection(String provider, String secretKey,
                                                           String adapter);
}
