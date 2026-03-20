package com.langfuse.mcp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScoreResponse extends RawJsonBackedResponse {
    private String id;
    private String traceId;
    private String observationId;
    private String sessionId;
    private String name;
    private Object value;
    /** NUMERIC | CATEGORICAL | BOOLEAN */
    private String dataType;
    private String comment;
    private String configId;
    private String source;
    private String createdAt;
    private String updatedAt;
}
