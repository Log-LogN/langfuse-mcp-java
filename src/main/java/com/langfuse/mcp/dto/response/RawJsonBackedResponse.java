package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base response model that preserves unmapped Langfuse fields.
 *
 * <p>This keeps MCP tool outputs forward-compatible when Langfuse adds new
 * properties that are not yet modelled explicitly in our DTOs.
 */
public abstract class RawJsonBackedResponse {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonAnySetter
    public void putAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> anyProperties() {
        return additionalProperties;
    }

    @JsonIgnore
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }
}

