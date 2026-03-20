package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ObservationFilterRequest;
import com.langfuse.mcp.dto.response.ObservationResponse;
import com.langfuse.mcp.service.ObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservationTools {

    private final ObservationService observationService;

    @McpTool(name = "fetch_observations", description = """
            List observations, optionally filtered by traceId or type.
            type values: GENERATION | SPAN | EVENT.
            Returns: id, traceId, type, name, model, inputTokens, outputTokens, totalCost, latency, level.
            Read-only.
            """)
    public ApiResponse<PagedResponse<ObservationResponse>> fetchObservations(
            @McpToolParam(description = "Filter by trace ID") String traceId,
            @McpToolParam(description = "Filter by type: GENERATION | SPAN | EVENT") String type,
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page, max 100") Integer limit) {

        return observationService.fetchObservations(ObservationFilterRequest.builder()
                .traceId(traceId).type(type)
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }

    @McpTool(name = "fetch_observation", description = "Fetch a single observation by ID. Read-only.")
    public ApiResponse<ObservationResponse> fetchObservation(
            @McpToolParam(description = "Observation ID", required = true) String observationId) {
        if (observationId == null || observationId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "observationId is required");
        }
        return observationService.fetchObservation(observationId.strip());
    }
}
