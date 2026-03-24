package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.common.PaginationMeta;
import com.langfuse.mcp.dto.request.TraceFilterRequest;
import com.langfuse.mcp.dto.response.ErrorCountResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.dto.response.TraceResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.exception.ResourceNotFoundException;
import com.langfuse.mcp.service.TraceService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraceServiceImpl implements TraceService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<TraceResponse>> fetchTraces(TraceFilterRequest request) {
        try {
            JsonNode raw = apiClient.getTraces(
                    request.getPage(), request.getLimit(), request.getUserId(),
                    request.getName(), request.getSessionId(), request.getTags(),
                    request.getFromTimestamp(), request.getToTimestamp());
            return ApiResponse.ok(pageMapper.mapPaged(raw, TraceResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("fetchTraces error: {}", ex.getMessage());
            return ApiResponse.error("TRACE_FETCH_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<TraceResponse> fetchTrace(String traceId) {
        try {
            JsonNode raw = apiClient.getTrace(traceId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, TraceResponse.class));
        } catch (ResourceNotFoundException ex) {
            log.warn("fetchTrace({}) not found", traceId);
            return ApiResponse.error("TRACE_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("fetchTrace({}) error: {}", traceId, ex.getMessage());
            return ApiResponse.error("TRACE_FETCH_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("fetchTrace({}) mapping error", traceId, ex);
            return ApiResponse.error("TRACE_MAPPING_ERROR", ex.getMessage());
        }
    }

    /**
     * Returns only traces whose {@code level} field equals "ERROR".
     *
     * <p>Fetches a batch from the Langfuse API and filters server-side.
     * If the Langfuse API ever exposes a {@code level} query parameter,
     * replace this with a direct API-side filter for better performance.
     */
    @Override
    public ApiResponse<PagedResponse<TraceResponse>> findExceptions(TraceFilterRequest request) {
        try {
            // Fetch up to 100 traces; filter to ERROR level before returning to agent
            JsonNode raw = apiClient.getTraces(
                    request.getPage(), 100, null, null,
                    null, null,
                    request.getFromTimestamp(), request.getToTimestamp());

            List<TraceResponse> errorTraces = pageMapper
                    .mapPaged(raw, TraceResponse.class)
                    .getData()
                    .stream()
                    .filter(t -> "ERROR".equalsIgnoreCase(t.getLevel()))
                    .toList();

            PaginationMeta meta = PaginationMeta.builder()
                    .page(request.getPage())
                    .limit(request.getLimit())
                    .totalItems((long) errorTraces.size())
                    .totalPages(1)
                    .build();

            return ApiResponse.ok(new PagedResponse<>(errorTraces, meta));
        } catch (LangfuseApiException ex) {
            log.error("findExceptions error: {}", ex.getMessage());
            return ApiResponse.error("EXCEPTION_FETCH_ERROR", ex.getMessage());
        }
    }

    /**
     * Returns error-level traces where {@code metadata} contains the given {@code fileName} pattern.
     *
     * <p>Filtering is performed on the server side (this service), not delegated to the agent.
     */
    @Override
    public ApiResponse<PagedResponse<TraceResponse>> findExceptionsInFile(
            String fileName, TraceFilterRequest request) {
        try {
            JsonNode raw = apiClient.getTraces(
                    request.getPage(), 100, null, null,
                    null, null,
                    request.getFromTimestamp(), request.getToTimestamp());

            List<TraceResponse> filtered = pageMapper
                    .mapPaged(raw, TraceResponse.class)
                    .getData()
                    .stream()
                    .filter(t -> "ERROR".equalsIgnoreCase(t.getLevel()))
                    .filter(t -> {
                        if (t.getMetadata() == null) return false;
                        // Check if metadata JSON (as string) contains the fileName pattern
                        return t.getMetadata().toString().contains(fileName);
                    })
                    .toList();

            PaginationMeta meta = PaginationMeta.builder()
                    .page(1)
                    .limit(filtered.size())
                    .totalItems((long) filtered.size())
                    .totalPages(1)
                    .build();

            return ApiResponse.ok(new PagedResponse<>(filtered, meta));
        } catch (LangfuseApiException ex) {
            log.error("findExceptionsInFile({}) error: {}", fileName, ex.getMessage());
            return ApiResponse.error("EXCEPTION_FILE_FETCH_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<TraceResponse> getExceptionDetails(String traceId) {
        return fetchTrace(traceId);
    }

    /**
     * Counts error-level traces in the given time range.
     *
     * <p>Fetches up to 500 traces and counts those with {@code level=ERROR}.
     * For very large datasets, consider paginating or using the metrics API.
     */
    @Override
    public ApiResponse<ErrorCountResponse> getErrorCount(String fromTimestamp, String toTimestamp) {
        try {
            long errorCount = 0;
            int page = 1;
            boolean hasMore = true;

            while (hasMore) {
                JsonNode raw = apiClient.getTraces(page, 100, null, null, null, null,
                        fromTimestamp, toTimestamp);
                List<TraceResponse> batch = pageMapper.mapPaged(raw, TraceResponse.class).getData();

                errorCount += batch.stream()
                        .filter(t -> "ERROR".equalsIgnoreCase(t.getLevel()))
                        .count();

                // Check if more pages exist
                JsonNode meta = raw.path("meta");
                int totalPages = meta.path("totalPages").asInt(1);
                hasMore = page < totalPages && page < 5; // cap at 5 pages (500 traces) for safety
                page++;
            }

            return ApiResponse.ok(ErrorCountResponse.builder()
                    .errorCount(errorCount)
                    .fromTimestamp(fromTimestamp)
                    .toTimestamp(toTimestamp)
                    .build());
        } catch (LangfuseApiException ex) {
            log.error("getErrorCount error: {}", ex.getMessage());
            return ApiResponse.error("ERROR_COUNT_FETCH_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<MutationResponse> deleteTrace(String traceId) {
        try {
            JsonNode raw = apiClient.deleteTrace(traceId);
            return ApiResponse.ok(MutationResponse.builder()
                    .id(traceId)
                    .message(raw.path("message").asText("Trace deleted"))
                    .build());
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error("TRACE_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("deleteTrace({}) error: {}", traceId, ex.getMessage());
            return ApiResponse.error("TRACE_DELETE_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<MutationResponse> deleteTraces(String traceIdsCsv) {
        try {
            List<String> traceIds = Arrays.stream(traceIdsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
            JsonNode raw = apiClient.deleteTraces(Map.of("traceIds", traceIds));
            return ApiResponse.ok(MutationResponse.builder()
                    .message(raw.path("message").asText("Traces deleted"))
                    .build());
        } catch (LangfuseApiException ex) {
            log.error("deleteTraces error: {}", ex.getMessage());
            return ApiResponse.error("TRACE_DELETE_ERROR", ex.getMessage());
        }
    }
}
