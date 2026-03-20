package com.langfuse.mcp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DatasetItemResponse extends RawJsonBackedResponse {
    private String id;
    private String datasetName;
    private Object input;
    private Object expectedOutput;
    private Object metadata;
    private String createdAt;
    private String updatedAt;
}
