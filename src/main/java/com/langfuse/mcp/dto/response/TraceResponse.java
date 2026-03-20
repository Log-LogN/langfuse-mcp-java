package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraceResponse extends RawJsonBackedResponse {
    private String id;
    private String projectId;
    private String name;
    private String timestamp;
    private String environment;
    private List<String> tags;
    private Boolean bookmarked;
    private String release;
    private String version;
    private String userId;
    private String sessionId;
    @JsonProperty("public")
    private Boolean isPublic;
    private Object input;
    private Object output;
    private Object metadata;
    private String createdAt;
    private String updatedAt;
    private String externalId;
    private Object scores;
    private Double latency;
    private Object observations;
    private String htmlPath;
    private Double totalCost;
    private String level;
    private Long totalTokens;
}
