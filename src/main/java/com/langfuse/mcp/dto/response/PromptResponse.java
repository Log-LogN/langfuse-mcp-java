package com.langfuse.mcp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PromptResponse extends RawJsonBackedResponse {
    private String name;
    private Integer version;
    /** text | chat */
    private String type;
    /** String for text prompts; array of message objects for chat prompts. */
    private Object prompt;
    private List<String> labels;
    private List<String> tags;
    private Map<String, Object> config;
    private String createdAt;
    private String updatedAt;
}
