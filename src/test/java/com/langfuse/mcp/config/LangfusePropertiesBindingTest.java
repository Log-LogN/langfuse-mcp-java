package com.langfuse.mcp.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class LangfusePropertiesBindingTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "langfuse.public-key=test-pk",
                    "langfuse.secret-key=test-sk",
                    "langfuse.host=https://cloud.langfuse.com/",
                    "langfuse.timeout=5s",
                    "langfuse.read-only=true");

    @Test
    void bindsDurationTimeoutAndBuildsRestClient() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(LangfuseProperties.class);
            assertThat(context).hasSingleBean(RestClient.class);

            LangfuseProperties properties = context.getBean(LangfuseProperties.class);
            assertThat(properties.getTimeout()).isEqualTo(Duration.ofSeconds(5));
            assertThat(properties.getTimeoutMs()).isEqualTo(5_000L);
            assertThat(properties.baseUrl()).isEqualTo("https://cloud.langfuse.com");
        });
    }

    @Configuration
    @EnableConfigurationProperties(LangfuseProperties.class)
    @Import(LangfuseClientConfig.class)
    static class TestConfig {
    }
}

