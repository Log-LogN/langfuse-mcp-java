package com.langfuse.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Langfuse API connection properties.
 * Bound from the {@code langfuse.*} prefix in application.yml / environment variables.
 *
 * <p>Required env vars:
 * <ul>
 *   <li>LANGFUSE_PUBLIC_KEY</li>
 *   <li>LANGFUSE_SECRET_KEY</li>
 *   <li>LANGFUSE_HOST (for example {@code https://cloud.langfuse.com})</li>
 * </ul>
 */
@Data
@ConfigurationProperties(prefix = "langfuse")
public class LangfuseProperties {

    private String publicKey;
    private String secretKey;
    private String host;
    private Duration timeout = Duration.ofSeconds(30);
    private boolean readOnly = true;

    public String baseUrl() {
        return (host != null && host.endsWith("/"))
                ? host.substring(0, host.length() - 1)
                : host;
    }

    public long getTimeoutMs() {
        return timeout.toMillis();
    }
}