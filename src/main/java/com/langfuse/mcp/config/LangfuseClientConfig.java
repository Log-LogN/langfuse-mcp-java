package com.langfuse.mcp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Configures the {@link RestClient} used to call the Langfuse Public REST API.
 *
 * <p>Uses Spring's simple client request factory with connect/read timeouts driven by
 * {@link LangfuseProperties#getTimeout()}.
 */
@Configuration
@RequiredArgsConstructor
public class LangfuseClientConfig {

    private final LangfuseProperties properties;

    @Bean
    public RestClient langfuseRestClient() {
        String credentials = properties.getPublicKey() + ":" + properties.getSecretKey();
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) properties.getTimeoutMs());
        requestFactory.setReadTimeout((int) properties.getTimeoutMs());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .defaultHeader("Authorization", basicAuth)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
