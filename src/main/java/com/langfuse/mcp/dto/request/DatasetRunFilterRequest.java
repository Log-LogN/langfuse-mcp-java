package com.langfuse.mcp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DatasetRunFilterRequest {
    private String datasetName;
    @Builder.Default private Integer page = 1;
    @Builder.Default private Integer limit = 20;
}
