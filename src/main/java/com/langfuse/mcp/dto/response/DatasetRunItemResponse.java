package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetRunItemResponse {
    private String id;
    private String datasetRunId;
    private String datasetRunName;
    private String datasetItemId;
    private String traceId;
    private String observationId;
    private String createdAt;
    private String updatedAt;
}
