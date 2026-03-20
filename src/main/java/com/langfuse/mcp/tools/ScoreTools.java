package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ScoreFilterRequest;
import com.langfuse.mcp.dto.response.ScoreConfigResponse;
import com.langfuse.mcp.dto.response.ScoreResponse;
import com.langfuse.mcp.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScoreTools {

    private final ScoreService scoreService;

    @McpTool(name = "get_scores", description = """
            List evaluation scores with optional filters.
            dataType values: NUMERIC | CATEGORICAL | BOOLEAN.
            Returns: id, traceId, observationId, name, value, dataType, comment, source.
            Read-only.
            """)
    public ApiResponse<PagedResponse<ScoreResponse>> getScores(
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page, max 100") Integer limit,
            @McpToolParam(description = "Filter by trace ID") String traceId,
            @McpToolParam(description = "Filter by observation ID") String observationId,
            @McpToolParam(description = "Filter by score name") String name,
            @McpToolParam(description = "Filter by type: NUMERIC | CATEGORICAL | BOOLEAN") String dataType,
            @McpToolParam(description = "ISO-8601 start timestamp") String fromTimestamp,
            @McpToolParam(description = "ISO-8601 end timestamp") String toTimestamp) {

        return scoreService.getScores(ScoreFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .traceId(traceId).observationId(observationId)
                .name(name).dataType(dataType)
                .fromTimestamp(fromTimestamp).toTimestamp(toTimestamp)
                .build());
    }

    @McpTool(name = "get_score", description = "Fetch a single evaluation score by ID. Read-only.")
    public ApiResponse<ScoreResponse> getScore(
            @McpToolParam(description = "Score ID", required = true) String scoreId) {
        if (scoreId == null || scoreId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "scoreId is required");
        }
        return scoreService.getScore(scoreId.strip());
    }

    @McpTool(name = "get_score_configs", description = """
            List all score config schemas. Configs define constraints for NUMERIC (min/max),
            CATEGORICAL (allowed categories), or BOOLEAN scores. Read-only.
            """)
    public ApiResponse<PagedResponse<ScoreConfigResponse>> getScoreConfigs(
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page") Integer limit) {
        return scoreService.getScoreConfigs(
                page != null ? page : 1,
                limit != null ? limit : 20);
    }

    @McpTool(name = "get_score_config", description = "Get a specific score config schema by ID. Read-only.")
    public ApiResponse<ScoreConfigResponse> getScoreConfig(
            @McpToolParam(description = "Score config ID", required = true) String configId) {
        if (configId == null || configId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "configId is required");
        }
        return scoreService.getScoreConfig(configId.strip());
    }
}
