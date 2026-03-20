package com.langfuse.mcp.exception;

/**
 * Thrown when the Langfuse API responds with HTTP 404 for a specific resource.
 */
public class ResourceNotFoundException extends LangfuseApiException {

    public ResourceNotFoundException(String resourceType, String id) {
        super(resourceType + " not found with id: " + id, 404, resourceType);
    }
}
