package com.langfuse.mcp.exception;

import lombok.Getter;

/**
 * Wraps all errors originating from the Langfuse Public REST API or HTTP connectivity layer.
 */
@Getter
public class LangfuseApiException extends RuntimeException {

    private final int statusCode;
    private final String endpoint;

    public LangfuseApiException(String message, int statusCode, String endpoint) {
        super(message);
        this.statusCode = statusCode;
        this.endpoint = endpoint;
    }

    /** Connectivity / serialization errors where no HTTP status is available. */
    public LangfuseApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.endpoint = "unknown";
    }

    /** Connectivity / serialization errors with a known endpoint path. */
    public LangfuseApiException(String message, Throwable cause, String endpoint) {
        super(message, cause);
        this.statusCode = -1;
        this.endpoint = endpoint;
    }
}
