package com.langfuse.mcp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ObservationResponse extends RawJsonBackedResponse {
    private String id;
    private String traceId;
    private String parentObservationId;
    /** GENERATION | SPAN | EVENT */
    private String type;
    private String name;
    private String startTime;
    private String endTime;
    private Object input;
    private Object output;
    private Object metadata;
    private String model;
    private Long inputTokens;
    private Long outputTokens;
    private Long totalTokens;
    private Double totalCost;
    private String level;
    private String environment;
}
