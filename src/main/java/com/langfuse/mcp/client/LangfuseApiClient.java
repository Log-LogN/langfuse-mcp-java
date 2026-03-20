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
            throw new LangfuseApiException(buildConnectivityMessage(path, ex), ex, path);
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

    // ── Traces ────────────────────────────────────────────────────────────

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

    // ── Observations ──────────────────────────────────────────────────────

    public JsonNode getObservations(String traceId, String type, Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/observations")
                .queryParamIfPresent("traceId", Optional.ofNullable(traceId))
                .queryParamIfPresent("type", Optional.ofNullable(type))
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getObservation(String observationId) {
        return get("/api/public/observations/" + observationId);
    }

    // ── Sessions ──────────────────────────────────────────────────────────

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

    public JsonNode getSession(String sessionId) {
        return get("/api/public/sessions/" + sessionId);
    }

    // ── Prompts ───────────────────────────────────────────────────────────

    public JsonNode getPrompts(Integer page, Integer limit) {
        String uri = UriComponentsBuilder.fromPath("/api/public/v2/prompts")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getPrompt(String promptName, Integer version, String label) {
        String uri = UriComponentsBuilder.fromPath("/api/public/v2/prompts/{name}")
                .queryParamIfPresent("version", Optional.ofNullable(version))
                .queryParamIfPresent("label", Optional.ofNullable(label))
                .buildAndExpand(promptName).toUriString();
        return get(uri);
    }

    // ── Datasets ──────────────────────────────────────────────────────────

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

    // ── Scores ────────────────────────────────────────────────────────────

    public JsonNode getScores(Integer page, Integer limit, String traceId,
                              String observationId, String name, String dataType,
                              String fromTimestamp, String toTimestamp) {
        String uri = UriComponentsBuilder.fromPath("/api/public/scores")
                .queryParamIfPresent("page", Optional.ofNullable(page))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .queryParamIfPresent("traceId", Optional.ofNullable(traceId))
                .queryParamIfPresent("observationId", Optional.ofNullable(observationId))
                .queryParamIfPresent("name", Optional.ofNullable(name))
                .queryParamIfPresent("dataType", Optional.ofNullable(dataType))
                .queryParamIfPresent("fromTimestamp", Optional.ofNullable(fromTimestamp))
                .queryParamIfPresent("toTimestamp", Optional.ofNullable(toTimestamp))
                .build().toUriString();
        return get(uri);
    }

    public JsonNode getScore(String scoreId) {
        return get("/api/public/scores/" + scoreId);
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

    // ── Comments ──────────────────────────────────────────────────────────

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

    // ── Helpers ───────────────────────────────────────────────────────────

    private String buildConnectivityMessage(String path, Exception ex) {
        return "Upstream connectivity error on GET " + path
                + " [base=" + resolvedBaseUrl() + "]: " + ex.getMessage()
                + ". Verify: Langfuse is running, host/port is reachable from the MCP process, "
                + "and HTTP vs HTTPS scheme matches the server.";
    }

    private String resolvedBaseUrl() {
        String baseUrl = properties.baseUrl();
        return baseUrl != null ? baseUrl : "<unset LANGFUSE_HOST>";
    }

    private static String abbreviate(String body) {
        if (body == null) return "<null>";
        String normalized = body.replaceAll("\\s+", " ").trim();
        return normalized.length() <= BODY_SNIPPET_LIMIT
                ? normalized
                : normalized.substring(0, BODY_SNIPPET_LIMIT) + "…";
    }
}
