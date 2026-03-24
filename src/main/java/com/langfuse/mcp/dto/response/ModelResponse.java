package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelResponse {
    private String id;
    private String modelName;
    private String matchPattern;
    private String startDate;
    private String unit;
    private Double inputPrice;
    private Double outputPrice;
    private Double totalPrice;
    private String tokenizerId;
    private JsonNode tokenizerConfig;
    private Boolean isLangfuseManaged;
    private String projectId;
    private String createdAt;
    private String updatedAt;
}
