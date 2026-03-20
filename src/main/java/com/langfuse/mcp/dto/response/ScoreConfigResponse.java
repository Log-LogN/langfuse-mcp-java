package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreConfigResponse {
    private String id;
    private String name;
    /** NUMERIC | CATEGORICAL | BOOLEAN */
    private String dataType;
    private Double minValue;
    private Double maxValue;
    private List<String> categories;
    private String createdAt;
    private String updatedAt;
}
