package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.response.CostMetricsResponse;
import com.langfuse.mcp.service.CostMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

/**
 * MCP tool that exposes the Langfuse Metrics API v1.
 *
 * <p>The agent constructs the full query JSON and passes it as a single parameter,
 * mirroring the raw curl: GET /api/public/metrics?query=&lt;json&gt;
 */
@Component
@RequiredArgsConstructor
public class CostMetricsTools {

    private final CostMetricsService costMetricsService;

    @McpTool(name = "get_cost_metrics", description = """
            Query Langfuse cost, token, latency, and usage analytics via the Metrics API.
            Mirrors: GET /api/public/metrics?query=<json>

            Pass the full query as a JSON string. All aggregation is server-side.

            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            QUERY STRUCTURE
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            {
              "view":           string,   // REQUIRED. traces | observations | scores-numeric | scores-categorical
              "metrics":        [...],    // REQUIRED. At least one { measure, aggregation, alias? }
              "fromTimestamp":  string,   // REQUIRED. ISO-8601 e.g. "2026-03-18T00:00:00Z"
              "toTimestamp":    string,   // REQUIRED. ISO-8601 e.g. "2026-03-25T23:59:59Z"
              "dimensions":     [...],    // Optional. [{ "field": "..." }]
              "filters":        [...],    // Optional. [{ "column", "operator", "value", "type", "key"? }]
              "timeDimension":  {...},    // Optional. { "granularity": "hour|day|week|month|auto" }
              "config":         {...}     // Optional. { "bins": 10, "row_limit": 100 }
            }

            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            VIEW
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
              traces         → end-to-end cost, tokens, latency per request
              observations   → per LLM call; USE for model breakdowns (providedModelName)
              scores-numeric → numeric/boolean evaluation scores
              scores-categorical → categorical evaluation scores

            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            MEASURES (by view)
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            traces:             count | observationsCount | scoresCount | latency | totalTokens | totalCost
            observations:       count | latency | totalTokens | totalCost | timeToFirstToken | countScores
            scores-numeric:     count | value
            scores-categorical: count
            ⚠ NEVER use inputTokens / outputTokens / promptTokens / completionTokens → 400 error. Use totalTokens.

            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            AGGREGATIONS
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
              sum | avg | count | max | min | p50 | p75 | p90 | p95 | p99
              sum → cost/token totals    avg/p95/p99 → latency    count → record counts

            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            DIMENSIONS (group-by, by view)
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            traces:       name | tags | userId | sessionId | release | version | environment | observationName | scoreName
            observations: providedModelName | type | name | level | version | environment | userId | sessionId |
                          traceName | traceRelease | traceVersion | promptName | promptVersion | scoreName
            ⚠ HIGH CARDINALITY — use in filters only, not dimensions: id | traceId | observationId

            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            FILTERS
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Each filter: { "column", "operator", "value", "type", "key"? }
              type string/stringObject/boolean → operator: =  |  contains  |  does not contain  |  starts with  |  ends with
              type number/datetime             → operator: =  |  <  |  >  |  <=  |  >=
            ⚠ NEVER use != / not_contains / not_equals for string fields → 400 error.

            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            EXAMPLES
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Total cost last 7 days:
              {"view":"traces","metrics":[{"measure":"totalCost","aggregation":"sum"}],
               "fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}

            Daily cost trend this week:
              {"view":"traces","metrics":[{"measure":"totalCost","aggregation":"sum"},{"measure":"count","aggregation":"count"}],
               "timeDimension":{"granularity":"day"},
               "fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}

            Cost by model:
              {"view":"observations","dimensions":[{"field":"providedModelName"}],
               "metrics":[{"measure":"totalCost","aggregation":"sum"},{"measure":"totalTokens","aggregation":"sum"}],
               "fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}

            Cost for a specific user:
              {"view":"traces","metrics":[{"measure":"totalCost","aggregation":"sum"}],
               "filters":[{"column":"userId","operator":"=","value":"user-123","type":"string"}],
               "fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}

            Production environment only:
              filters: [{"column":"environment","operator":"=","value":"production","type":"string"}]
            """)
    public ApiResponse<CostMetricsResponse> getCostMetrics(
            @McpToolParam(description = """
                    Full metrics query as a JSON string. Required.
                    Structure: { "view", "metrics", "fromTimestamp", "toTimestamp", "dimensions"?, "filters"?, "timeDimension"?, "config"? }
                    See tool description for allowed values per field.
                    Example: {"view":"traces","metrics":[{"measure":"totalCost","aggregation":"sum"}],"fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}
                    """,
                    required = true)
            String query) {

        if (query == null || query.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "query is required");
        }
        return costMetricsService.getMetrics(query.strip());
    }
}
