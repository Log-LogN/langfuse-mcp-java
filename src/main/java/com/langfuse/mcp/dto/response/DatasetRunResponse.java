package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetRunResponse {
    private String id;
    private String name;
    private String datasetId;
    private String datasetName;
    private JsonNode metadata;
    private String createdAt;
    private String updatedAt;
    /** Populated only when fetching a single run with its items */
    private List<DatasetRunItemResponse> datasetRunItems;
}
