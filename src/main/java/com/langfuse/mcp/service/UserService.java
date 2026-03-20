package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.TraceFilterRequest;
import com.langfuse.mcp.dto.response.TraceResponse;

public interface UserService {

    ApiResponse<PagedResponse<TraceResponse>> getUserTraces(String userId, TraceFilterRequest request);
}
