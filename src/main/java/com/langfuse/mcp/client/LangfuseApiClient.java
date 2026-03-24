package com.langfuse.mcp.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.config.LangfuseProperties;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Low-level HTTP gateway to the Langfuse Public REST API.
 *
 * <p><strong>Read-only by design:</strong> Only GET methods exist.
 * No POST, PATCH, or DELETE methods will be added to this class.
 *
 * <p>All tool and service classes must route HTTP calls through this component —
 * direct {@link RestClient} usage is prohibited elsewhere.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LangfuseApiClient {

    private static final int BODY_SNIPPET_LIMIT = 300;

    private final RestClient langfuseRestClient;
    private final ObjectMapper objectMapper;
    private final LangfuseProperties properties;

    // ── Generic GET ──────────────────────────────────────────────────────

    private static String abbreviate(String body) {
        if (body == null) return "<null>";
        String normalized = body.replaceAll("\\s+", " ").trim();
        return normalized.length() <= BODY_SNIPPET_LIMIT
                ? normalized
                : normalized.substring(0, BODY_SNIPPET_LIMIT) + "…";
    }

    public JsonNode get(String path) {
        log.debug("GET {}", path);
        String responseBody = null;
        try {
            responseBody = langfuseRestClient.get().uri(path).retrieve().body(String.class);
            if (responseBody == null || responseBody.isBlank()) {
                throw new LangfuseApiException("Unexpected empty response on GET " + path, -1, path);
            }
            return objectMapper.readTree(responseBody);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException(path, path);
            }
            throw new LangfuseApiException(
                    "Langfuse API error on GET " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex.getStatusCode().value(), path);
        } catch (RestClientException ex) {
            throw new LangfuseApiException(buildConnectivityMessage("GET", path, ex), ex, path);
        } catch (JsonProcessingException ex) {
            throw new LangfuseApiException(
                    "Unexpected JSON on GET " + path + " [base=" + resolvedBaseUrl() + "]: "
                            + ex.getOriginalMessage()
                            + " | body=" + abbreviate(responseBody),
                    ex, path);
        } catch (LangfuseApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LangfuseApiException(
                    "Unexpected error on GET " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex, path);
        }
    }

    public JsonNode post(String path, Object body) {
        log.debug("POST {}", path);
        String responseBody = null;
        try {
            responseBody = langfuseRestClient.post().uri(path)
                    .body(body).retrieve().body(String.class);
            if (responseBody == null || responseBody.isBlank()) {
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(responseBody);
        } catch (RestClientResponseException ex) {
            throw new LangfuseApiException(
                    "Langfuse API error on POST " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex.getStatusCode().value(), path);
        } catch (RestClientException ex) {
            throw new LangfuseApiException(buildConnectivityMessage("POST", path, ex), ex, path);
        } catch (JsonProcessingException ex) {
            throw new LangfuseApiException(
                    "Unexpected JSON on POST " + path + " [base=" + resolvedBaseUrl() + "]: "
                            + ex.getOriginalMessage()
                            + " | body=" + abbreviate(responseBody),
                    ex, path);
        } catch (LangfuseApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LangfuseApiException(
                    "Unexpected error on POST " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex, path);
        }
    }

    public JsonNode delete(String path) {
        log.debug("DELETE {}", path);
        String responseBody = null;
        try {
            responseBody = langfuseRestClient.delete().uri(path)
                    .retrieve().body(String.class);
            if (responseBody == null || responseBody.isBlank()) {
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(responseBody);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException(path, path);
            }
            throw new LangfuseApiException(
                    "Langfuse API error on DELETE " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex.getStatusCode().value(), path);
        } catch (RestClientException ex) {
            throw new LangfuseApiException(buildConnectivityMessage("DELETE", path, ex), ex, path);
        } catch (JsonProcessingException ex) {
            throw new LangfuseApiException(
                    "Unexpected JSON on DELETE " + path + " [base=" + resolvedBaseUrl() + "]: "
                            + ex.getOriginalMessage()
                            + " | body=" + abbreviate(responseBody),
                    ex, path);
        } catch (LangfuseApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LangfuseApiException(
                    "Unexpected error on DELETE " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex, path);
        }
    }

    public JsonNode delete(String path, Object body) {
        log.debug("DELETE {}", path);
        String responseBody = null;
        try {
            responseBody = langfuseRestClient.method(org.springframework.http.HttpMethod.DELETE).uri(path)
                    .body(body).retrieve().body(String.class);
            if (responseBody == null || responseBody.isBlank()) {
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(responseBody);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException(path, path);
            }
            throw new LangfuseApiException(
                    "Langfuse API error on DELETE " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex.getStatusCode().value(), path);
        } catch (RestClientException ex) {
            throw new LangfuseApiException(buildConnectivityMessage("DELETE", path, ex), ex, path);
        } catch (JsonProcessingException ex) {
            throw new LangfuseApiException(
                    "Unexpected JSON on DELETE " + path + " [base=" + resolvedBaseUrl() + "]: "
                            + ex.getOriginalMessage()
                            + " | body=" + abbreviate(responseBody),
                    ex, path);
        } catch (LangfuseApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LangfuseApiException(
                    "Unexpected error on DELETE " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex, path);
        }
    }

    public JsonNode patch(String path, Object body) {
        log.debug("PATCH {}", path);
        String responseBody = null;
        try {
            responseBody = langfuseRestClient.patch().uri(path)
                    .body(body).retrieve().body(String.class);
            if (responseBody == null || responseBody.isBlank()) {
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(responseBody);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException(path, path);
            }
            throw new LangfuseApiException(
                    "Langfuse API error on PATCH " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex.getStatusCode().value(), path);
        } catch (RestClientException ex) {
            throw new LangfuseApiException(buildConnectivityMessage("PATCH", path, ex), ex, path);
        } catch (JsonProcessingException ex) {
            throw new LangfuseApiException(
                    "Unexpected JSON on PATCH " + path + " [base=" + resolvedBaseUrl() + "]: "
                            + ex.getOriginalMessage()
                            + " | body=" + abbreviate(responseBody),
                    ex, path);
        } catch (LangfuseApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LangfuseApiException(
                    "Unexpected error on PATCH " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex, path);
        }
    }

    // ── Traces ────────────────────────────────────────────────────────────

    public JsonNode put(String path, Object body) {
        log.debug("PUT {}", path);
        String responseBody = null;
        try {
            responseBody = langfuseRestClient.put().uri(path)
                    .body(body).retrieve().body(String.class);
            if (responseBody == null || responseBody.isBlank()) {
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(responseBody);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException(path, path);
            }
            throw new LangfuseApiException(
                    "Langfuse API error on PUT " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex.getStatusCode().value(), path);
        } catch (RestClientException ex) {
            throw new LangfuseApiException(buildConnectivityMessage("PUT", path, ex), ex, path);
        } catch (JsonProcessingException ex) {
            throw new LangfuseApiException(
                    "Unexpected JSON on PUT " + path + " [base=" + resolvedBaseUrl() + "]: "
                            + ex.getOriginalMessage()
                            + " | body=" + abbreviate(responseBody),
                    ex, path);
        } catch (LangfuseApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LangfuseApiException(
                    "Unexpected error on PUT " + path + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage(),
                    ex, path);
        }
    }

    public JsonNode getTraces(Integer page, Integer limit, String userId,
                              String name, String sessionId, String tags,
                              String fromTimestamp, String toTimestamp) {
        String uri = UriComponentsBuilder.fromPath("/api/public/traces")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .queryParamIfPresent("userId", Optional.ofNullable(userId))
                .queryParamIfPresent("name", Optional.ofNullable(name))
                .queryParamIfPresent("sessionId", Optional.ofNullable(sessionId))
                .queryParamIfPresent("tags", Optional.ofNullable(tags))
                .queryParamIfPresent("fromTimestamp", Optional.ofNullable(fromTimestamp))
                .queryParamIfPresent("toTimestamp", Optional.ofNullable(toTimestamp))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getTrace(String traceId) {
        return get("/api/public/traces/" + traceId);
    }

    public JsonNode deleteTrace(String traceId) {
        return delete("/api/public/traces/" + traceId);
    }

    // ── Sessions ──────────────────────────────────────────────────────────

    public JsonNode deleteTraces(Object body) {
        return delete("/api/public/traces", body);
    }

    public JsonNode getSessions(Integer page, Integer limit,
                                String fromTimestamp, String toTimestamp, String userId) {
        String uri = UriComponentsBuilder.fromPath("/api/public/sessions")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .queryParamIfPresent("fromTimestamp", Optional.ofNullable(fromTimestamp))
                .queryParamIfPresent("toTimestamp", Optional.ofNullable(toTimestamp))
                .queryParamIfPresent("userId", Optional.ofNullable(userId))
                .build().toUriString();
        return get(uri);
    }

    // ── Prompts ───────────────────────────────────────────────────────────

    public JsonNode getSession(String sessionId) {
        return get("/api/public/sessions/" + sessionId);
    }

    public JsonNode getPrompts(Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/v2/prompts")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    // ── Datasets ──────────────────────────────────────────────────────────

    public JsonNode getPrompt(String promptName, Integer version, String label) {
        String uri = UriComponentsBuilder.fromPath("/api/public/v2/prompts/{name}")
                .queryParamIfPresent("version", Optional.ofNullable(version))
                .queryParamIfPresent("label", Optional.ofNullable(label))
                .buildAndExpand(promptName).toUriString();
        return get(uri);
    }

    public JsonNode getDatasets(Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/v2/datasets")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getDataset(String datasetName) {
        return get("/api/public/v2/datasets/" + datasetName);
    }

    public JsonNode createDataset(Object body) {
        return post("/api/public/v2/datasets", body);
    }

    public JsonNode getDatasetItems(String datasetName, Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/dataset-items")
                .queryParamIfPresent("datasetName", Optional.ofNullable(datasetName))
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getDatasetItem(String itemId) {
        return get("/api/public/dataset-items/" + itemId);
    }

    public JsonNode createDatasetItem(Object body) {
        return post("/api/public/dataset-items", body);
    }

    // ── Scores ────────────────────────────────────────────────────────────

    public JsonNode deleteDatasetItem(String itemId) {
        return delete("/api/public/dataset-items/" + itemId);
    }

    public JsonNode getScores(Integer page, Integer limit, String traceId,
                              String observationId, String name, String dataType,
                              String fromTimestamp, String toTimestamp) {
        String uri = UriComponentsBuilder.fromPath("/api/public/v2/scores")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .queryParamIfPresent("traceId", optionalText(traceId))
                .queryParamIfPresent("observationId", optionalText(observationId))
                .queryParamIfPresent("name", optionalText(name))
                .queryParamIfPresent("dataType", optionalText(dataType))
                .queryParamIfPresent("fromTimestamp", optionalText(fromTimestamp))
                .queryParamIfPresent("toTimestamp", optionalText(toTimestamp))
                .build()
                .toUriString();

        return get(uri);
    }

    private Optional<String> optionalText(String value) {
        return (value == null || value.isBlank())
                ? Optional.empty()
                : Optional.of(value.strip());
    }

    public JsonNode getScoreV2(String scoreId) {
        return get("/api/public/v2/scores/" + scoreId);
    }

    public JsonNode getScoreConfigs(Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/score-configs")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getScoreConfig(String configId) {
        return get("/api/public/score-configs/" + configId);
    }

    public JsonNode createScoreConfig(Object body) {
        return post("/api/public/score-configs", body);
    }

    // ── Comments ──────────────────────────────────────────────────────────

    public JsonNode updateScoreConfig(String configId, Object body) {
        return patch("/api/public/score-configs/" + configId, body);
    }

    public JsonNode getComments(String objectType, String objectId,
                                Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/comments")
                .queryParamIfPresent("objectType", Optional.ofNullable(objectType))
                .queryParamIfPresent("objectId", Optional.ofNullable(objectId))
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    // ── Annotation Queues ──────────────────────────────────────────────────

    public JsonNode getComment(String commentId) {
        return get("/api/public/comments/" + commentId);
    }

    public JsonNode getAnnotationQueues(Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/annotation-queues")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getAnnotationQueue(String queueId) {
        return get("/api/public/annotation-queues/" + queueId);
    }

    public JsonNode createAnnotationQueue(Object body) {
        return post("/api/public/annotation-queues", body);
    }

    public JsonNode getAnnotationQueueItems(String queueId, String status, Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/annotation-queues/{queueId}/items")
                .queryParamIfPresent("status", Optional.ofNullable(status))
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .buildAndExpand(queueId).toUriString();
        return get(uri);
    }

    public JsonNode getAnnotationQueueItem(String queueId, String itemId) {
        return get("/api/public/annotation-queues/" + queueId + "/items/" + itemId);
    }

    public JsonNode createAnnotationQueueItem(String queueId, Object body) {
        return post("/api/public/annotation-queues/" + queueId + "/items", body);
    }

    public JsonNode updateAnnotationQueueItem(String queueId, String itemId, Object body) {
        return patch("/api/public/annotation-queues/" + queueId + "/items/" + itemId, body);
    }


    // ── Models ─────────────────────────────────────────────────────────────

    public JsonNode deleteAnnotationQueueItem(String queueId, String itemId) {
        return delete("/api/public/annotation-queues/" + queueId + "/items/" + itemId);
    }

    public JsonNode getModels(Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/models")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getModel(String modelId) {
        return get("/api/public/models/" + modelId);
    }

    public JsonNode createModel(Object body) {
        return post("/api/public/models", body);
    }

    // ── LLM Connections ────────────────────────────────────────────────────

    public JsonNode deleteModel(String modelId) {
        return delete("/api/public/models/" + modelId);
    }

    public JsonNode getLlmConnections(Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/llm-connections")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    // ── Dataset Runs ───────────────────────────────────────────────────────

    public JsonNode upsertLlmConnection(Object body) {
        return put("/api/public/llm-connections", body);
    }

    public JsonNode getDatasetRuns(String datasetName, Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/datasets/{datasetName}/runs")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .buildAndExpand(datasetName).toUriString();
        return get(uri);
    }

    public JsonNode getDatasetRun(String datasetName, String runName) {
        return get("/api/public/datasets/" + datasetName + "/runs/" + runName);
    }

    public JsonNode deleteDatasetRun(String datasetName, String runName) {
        return delete("/api/public/datasets/" + datasetName + "/runs/" + runName);
    }

    // ── Prompts (write) ────────────────────────────────────────────────────

    public JsonNode getDatasetRunItems(String datasetId, String runName, Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/dataset-run-items")
                .queryParam("datasetId", datasetId)
                .queryParam("runName", runName)
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode createPrompt(Object body) {
        return post("/api/public/v2/prompts", body);
    }

    public JsonNode deletePrompt(String promptName, String label, Integer version) {
        String uri = UriComponentsBuilder.fromPath("/api/public/v2/prompts/{name}")
                .queryParamIfPresent("label", Optional.ofNullable(label))
                .queryParamIfPresent("version", Optional.ofNullable(version))
                .buildAndExpand(promptName).toUriString();
        return delete(uri);
    }

    // ── Projects ───────────────────────────────────────────────────────────

    public JsonNode updatePromptLabels(String promptName, Integer version, Object body) {
        return patch("/api/public/v2/prompts/" + promptName + "/versions/" + version, body);
    }

    public JsonNode getProject() {
        return get("/api/public/projects");
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    public JsonNode createDatasetRunItem(Object body) {
        return post("/api/public/dataset-run-items", body);
    }

    private String buildConnectivityMessage(String method, String path, Exception ex) {
        return "Upstream connectivity error on " + method + " " + path
                + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage()
                + ". Verify: Langfuse is running, host/port is reachable from the MCP process, "
                + "and HTTP vs HTTPS scheme matches the server.";
    }

    private String resolvedBaseUrl() {
        String baseUrl = properties.baseUrl();
        return baseUrl != null ? baseUrl : "<unset LANGFUSE_HOST>";
    }
}
