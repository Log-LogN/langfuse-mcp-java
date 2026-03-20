package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.config.JacksonConfig;
import com.langfuse.mcp.dto.common.ApiResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RawJsonResponseSerializationTest {

    private final ObjectMapper objectMapper = new JacksonConfig().objectMapper();

    @Test
    void preservesFullTracePayloadAndSerializesRawNestedJson() throws Exception {
        JsonNode rawTrace = objectMapper.readTree("""
                {
                  "id": "9c63e520fffa09389a250e21880cc068",
                  "projectId": "cmmvzx5kr0006jz079swjf74s",
                  "name": "spring_ai chat_client",
                  "timestamp": "2026-03-19T04:13:31.150Z",
                  "environment": "default",
                  "tags": [],
                  "bookmarked": false,
                  "release": null,
                  "version": null,
                  "userId": null,
                  "sessionId": null,
                  "public": false,
                  "input": null,
                  "output": null,
                  "metadata": {
                    "attributes": {
                      "gen_ai.system": "spring_ai",
                      "spring.ai.kind": "chat_client"
                    }
                  },
                  "createdAt": "2026-03-19T04:13:35.977Z",
                  "updatedAt": "2026-03-19T04:13:36.026Z",
                  "externalId": null,
                  "scores": [],
                  "latency": 2.665,
                  "observations": [
                    {
                      "id": "dbb126a8eaa2f877",
                      "traceId": "9c63e520fffa09389a250e21880cc068",
                      "type": "GENERATION",
                      "name": "chat gpt-5-mini",
                      "input": ["Reply with the word 'java'"],
                      "output": ["java"],
                      "metadata": {
                        "attributes": {
                          "gen_ai.request.model": "gpt-5-mini"
                        }
                      },
                      "usageDetails": {
                        "total": 87,
                        "input": 13,
                        "output": 74
                      }
                    }
                  ],
                  "htmlPath": "/project/cmmvzx5kr0006jz079swjf74s/traces/9c63e520fffa09389a250e21880cc068",
                  "totalCost": 0.00015125,
                  "fooBar": {
                    "nested": 1
                  }
                }
                """);

        TraceResponse traceResponse = objectMapper.treeToValue(rawTrace, TraceResponse.class);
        String serializedJson = objectMapper.writeValueAsString(ApiResponse.ok(traceResponse));
        JsonNode serialized = objectMapper.readTree(serializedJson).path("data");

        assertThat(serialized.path("id").asText()).isEqualTo("9c63e520fffa09389a250e21880cc068");
        assertThat(serialized.path("public").asBoolean()).isFalse();
        assertThat(serialized.path("bookmarked").asBoolean()).isFalse();
        assertThat(serialized.path("input").isNull()).isTrue();
        assertThat(serialized.path("metadata").path("attributes").path("gen_ai.system").asText())
                .isEqualTo("spring_ai");
        assertThat(serialized.path("observations").isArray()).isTrue();
        assertThat(serialized.path("observations").get(0).path("metadata").path("attributes")
                .path("gen_ai.request.model").asText()).isEqualTo("gpt-5-mini");
        assertThat(serialized.path("htmlPath").asText()).contains("/traces/9c63e520fffa09389a250e21880cc068");
        assertThat(serialized.path("totalCost").decimalValue()).isEqualByComparingTo("0.00015125");
        assertThat(serialized.path("fooBar").path("nested").asInt()).isEqualTo(1);
        assertThat(serializedJson)
                .doesNotContain("\"nodeType\"")
                .doesNotContain("\"containerNode\"")
                .doesNotContain("\"valueNode\"");
    }

    @Test
    void preservesPromptPayloadAsRawJavaStructure() throws Exception {
        JsonNode rawPrompt = objectMapper.readTree("""
                {
                  "name": "support-assistant",
                  "version": 7,
                  "type": "chat",
                  "prompt": [
                    {
                      "role": "system",
                      "content": "You are helpful"
                    }
                  ],
                  "labels": ["prod"],
                  "tags": ["chat"],
                  "config": {
                    "temperature": 0.2
                  },
                  "commit": "abc123"
                }
                """);

        PromptResponse promptResponse = objectMapper.treeToValue(rawPrompt, PromptResponse.class);
        String serializedJson = objectMapper.writeValueAsString(ApiResponse.ok(promptResponse));
        JsonNode serialized = objectMapper.readTree(serializedJson).path("data");

        assertThat(serialized.path("prompt").isArray()).isTrue();
        assertThat(serialized.path("prompt").get(0).path("role").asText()).isEqualTo("system");
        assertThat(serialized.path("config").path("temperature").decimalValue()).isEqualByComparingTo("0.2");
        assertThat(serialized.path("commit").asText()).isEqualTo("abc123");
        assertThat(serializedJson)
                .doesNotContain("\"nodeType\"")
                .doesNotContain("\"containerNode\"");
    }
}

