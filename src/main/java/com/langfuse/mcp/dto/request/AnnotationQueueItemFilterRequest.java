package com.langfuse.mcp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AnnotationQueueItemFilterRequest {
    private String queueId;
    /** PENDING | ACTIVE | DONE — omit to return all statuses */
    private String status;
    @Builder.Default private Integer page = 1;
    @Builder.Default private Integer limit = 20;
}
