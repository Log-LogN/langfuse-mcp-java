package com.langfuse.mcp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScoreFilterRequest {
    @Builder.Default private Integer page = 1;
    @Builder.Default private Integer limit = 20;
    private String traceId;
    private String observationId;
    private String name;
    /** NUMERIC | CATEGORICAL | BOOLEAN */
    private String dataType;
    private String fromTimestamp;
    private String toTimestamp;
}
