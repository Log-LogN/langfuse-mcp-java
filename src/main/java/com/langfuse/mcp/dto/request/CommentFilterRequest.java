package com.langfuse.mcp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CommentFilterRequest {
    /** TRACE | OBSERVATION */
    private String objectType;
    private String objectId;
    @Builder.Default private Integer page = 1;
    @Builder.Default private Integer limit = 20;
}
