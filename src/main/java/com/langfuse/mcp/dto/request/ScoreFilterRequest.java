package com.langfuse.mcp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScoreFilterRequest {
    @Builder.Default private Integer page = 1;
    @Builder.Default private Integer limit = 20;
    private String userId;
    private String traceId;
    private String observationId;
    private String name;
    private String sessionId;
    private String environmentCsv;
    private String source;
    private String operator;
    private String value;
    private String scoreIdsCsv;
    private String configId;
    private String datasetRunId;
    private String queueId;
    private String traceTagsCsv;
    private String fields;
    private String filter;
    /** NUMERIC | CATEGORICAL | BOOLEAN | CORRECTION */
    private String dataType;
    private String fromTimestamp;
    private String toTimestamp;
}
