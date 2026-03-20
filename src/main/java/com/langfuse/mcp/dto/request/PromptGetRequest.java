package com.langfuse.mcp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PromptGetRequest {
    private String name;
    private Integer version;
    private String label;
}
