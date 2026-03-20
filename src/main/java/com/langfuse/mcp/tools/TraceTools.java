package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.TraceFilterRequest;
import com.langfuse.mcp.dto.response.ErrorCountResponse;
import com.langfuse.mcp.dto.response.TraceResponse;
import com.langfuse.mcp.service.TraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraceTools {

    private final TraceService traceService;

    @McpTool(name = "fetch_traces", description = """
            Returns a paginated list of Langfuse traces.

            Each trace represents one end-to-end LLM pipeline execution. The response includes:
            id, name, userId, sessionId, level (DEFAULT | DEBUG | WARNING | ERROR),
            latency (seconds), totalTokens, totalCost (USD), tags, timestamp.

            All filter parameters are optional. Omit any filter you do not need — omitted
            filters are ignored and do not narrow the result set.

            Pagination: page is 1-based (default 1), limit controls page size (default 20, max 100).
            To page through results, increment page while keeping limit fixed.
            """)
    public ApiResponse<PagedResponse<TraceResponse>> fetchTraces(
            @McpToolParam(description = "Page number, 1-based. Omit to use default (1).") Integer page,
            @McpToolParam(description = "Results per page, max 100. Omit to use default (20).") Integer limit,
            @McpToolParam(description = "Filter by the Langfuse user ID attached to the trace. Omit to return traces for all users.") String userId,
            @McpToolParam(description = "Filter by trace name — must be an exact string match. Omit to return all trace names.") String name,
            @McpToolParam(description = "Filter by session ID to return only traces belonging to that session. Omit to return traces across all sessions.") String sessionId,
            @McpToolParam(description = "Filter by a single tag string. Omit to return traces regardless of tags.") String tags,
            @McpToolParam(description = "Start of time range in ISO-8601 format, e.g. 2025-01-01T00:00:00Z. Omit to include traces from the beginning of the project.") String fromTimestamp,
            @McpToolParam(description = "End of time range in ISO-8601 format, e.g. 2025-12-31T23:59:59Z. Omit to include traces up to the current time.") String toTimestamp) {

        return traceService.fetchTraces(TraceFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .userId(userId)
                .name(name)
                .sessionId(sessionId)
                .tags(tags)
                .fromTimestamp(fromTimestamp)
                .toTimestamp(toTimestamp)
                .build());
    }

    @McpTool(name = "fetch_trace", description = """
            Returns the full detail of a single Langfuse trace identified by its ID.

            The response includes all observations (spans, generations, events) nested under
            the trace, as well as input/output payloads, metadata, tags, latency, and token usage.

            Use this after fetch_traces to drill into a specific trace. The traceId is required.
            """)
    public ApiResponse<TraceResponse> fetchTrace(
            @McpToolParam(description = "The Langfuse trace ID (UUID). Required — the call will be rejected if this is missing or blank.", required = true) String traceId) {
        if (traceId == null || traceId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "traceId is required");
        }
        return traceService.fetchTrace(traceId.strip());
    }

    @McpTool(name = "find_exceptions", description = """
            Returns only traces whose level field equals ERROR.

            Filtering is performed on the server before the response is returned —
            the result set contains error traces only, never a mix of levels.

            Useful for surfacing pipeline failures and debugging production errors.
            Both time range parameters are optional. Omit them to search across all time.
            Pagination works the same way as fetch_traces.
            """)
    public ApiResponse<PagedResponse<TraceResponse>> findExceptions(
            @McpToolParam(description = "Page number, 1-based. Omit to use default (1).") Integer page,
            @McpToolParam(description = "Results per page. Omit to use default (20).") Integer limit,
            @McpToolParam(description = "Start of time range in ISO-8601 format, e.g. 2025-06-01T00:00:00Z. Omit to search from the beginning of the project.") String fromTimestamp,
            @McpToolParam(description = "End of time range in ISO-8601 format, e.g. 2025-06-30T23:59:59Z. Omit to search up to the current time.") String toTimestamp) {

        return traceService.findExceptions(TraceFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .fromTimestamp(fromTimestamp)
                .toTimestamp(toTimestamp)
                .build());
    }

    @McpTool(name = "find_exceptions_in_file", description = """
            Returns ERROR-level traces whose metadata contains the given file name as a substring.

            Both conditions must be true for a trace to appear in the result:
            (1) the trace level is ERROR, and
            (2) the trace metadata JSON contains the fileName string anywhere inside it.

            Use this to isolate errors originating from a specific source file.
            fileName is required. Both time range parameters are optional —
            omit them to search across the full project history.
            """)
    public ApiResponse<PagedResponse<TraceResponse>> findExceptionsInFile(
            @McpToolParam(description = "Substring to match against trace metadata, typically a source file name such as OrderService.java. Required — the call will be rejected if this is missing or blank.", required = true) String fileName,
            @McpToolParam(description = "Start of time range in ISO-8601 format, e.g. 2025-06-01T00:00:00Z. Omit to search from the beginning of the project.") String fromTimestamp,
            @McpToolParam(description = "End of time range in ISO-8601 format, e.g. 2025-06-30T23:59:59Z. Omit to search up to the current time.") String toTimestamp) {
        if (fileName == null || fileName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "fileName is required");
        }
        return traceService.findExceptionsInFile(fileName.strip(), TraceFilterRequest.builder()
                .page(1)
                .limit(100)
                .fromTimestamp(fromTimestamp)
                .toTimestamp(toTimestamp)
                .build());
    }

    @McpTool(name = "get_exception_details", description = """
            Returns the full detail of a single ERROR-level trace identified by its ID.

            Equivalent to fetch_trace but semantically scoped to error traces.
            Use this after find_exceptions to inspect a specific failure in depth —
            the response includes all nested observations, input/output, metadata, and timing.

            The traceId is required.
            """)
    public ApiResponse<TraceResponse> getExceptionDetails(
            @McpToolParam(description = "The Langfuse trace ID (UUID) of the error trace to inspect. Required — the call will be rejected if this is missing or blank.", required = true) String traceId) {
        if (traceId == null || traceId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "traceId is required");
        }
        return traceService.getExceptionDetails(traceId.strip());
    }

    @McpTool(name = "get_error_count", description = """
            Returns the total count of ERROR-level traces within the specified time range.

            The server scans up to 500 traces (5 pages of 100) and counts those with level=ERROR.
            The response contains errorCount, fromTimestamp, and toTimestamp.

            Both time range parameters are optional. Omit them to count errors across all time.
            Use this for a quick health signal before drilling into individual traces with find_exceptions.
            """)
    public ApiResponse<ErrorCountResponse> getErrorCount(
            @McpToolParam(description = "Start of time range in ISO-8601 format, e.g. 2025-06-01T00:00:00Z. Omit to count from the beginning of the project.") String fromTimestamp,
            @McpToolParam(description = "End of time range in ISO-8601 format, e.g. 2025-06-30T23:59:59Z. Omit to count up to the current time.") String toTimestamp) {
        return traceService.getErrorCount(fromTimestamp, toTimestamp);
    }
}