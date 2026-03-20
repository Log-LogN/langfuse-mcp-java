package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.TraceFilterRequest;
import com.langfuse.mcp.dto.response.ErrorCountResponse;
import com.langfuse.mcp.dto.response.TraceResponse;

public interface TraceService {
    ApiResponse<PagedResponse<TraceResponse>> fetchTraces(TraceFilterRequest request);

    ApiResponse<TraceResponse> fetchTrace(String traceId);

    ApiResponse<PagedResponse<TraceResponse>> findExceptions(TraceFilterRequest request);

    ApiResponse<PagedResponse<TraceResponse>> findExceptionsInFile(String fileName, TraceFilterRequest request);

    ApiResponse<TraceResponse> getExceptionDetails(String traceId);

    ApiResponse<ErrorCountResponse> getErrorCount(String fromTimestamp, String toTimestamp);
}
