package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aggregated score statistics returned by the Langfuse metrics/scores endpoint.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreAnalyticsResponse {
    private String name;
    private Double mean;
    private Double median;
    private Double p25;
    private Double p75;
    private Double p90;
    private Double p95;
    private Double stddev;
    private Long count;
}
