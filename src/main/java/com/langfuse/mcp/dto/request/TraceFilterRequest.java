package com.langfuse.mcp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TraceFilterRequest {
    @Builder.Default private Integer page = 1;
    @Builder.Default private Integer limit = 20;
    private String userId;
    private String name;
    private String sessionId;
    private String tags;
    private String fromTimestamp;
    private String toTimestamp;
}
