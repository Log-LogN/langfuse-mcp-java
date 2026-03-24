package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** status values: PENDING | ACTIVE | DONE */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationQueueItemResponse {
    private String id;
    private String queueId;
    private String traceId;
    private String observationId;
    /** PENDING | ACTIVE | DONE */
    private String status;
    private String annotatorUserId;
    private String completedAt;
    private String createdAt;
    private String updatedAt;
}
