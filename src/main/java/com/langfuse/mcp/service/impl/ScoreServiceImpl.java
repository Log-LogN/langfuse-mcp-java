package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ScoreFilterRequest;
import com.langfuse.mcp.dto.response.ScoreConfigResponse;
import com.langfuse.mcp.dto.response.ScoreResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.ScoreService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<ScoreResponse>> getScores(ScoreFilterRequest request) {
        try {
            JsonNode raw = apiClient.getScores(
                    request.getPage(), request.getLimit(), request.getTraceId(),
                    request.getObservationId(), request.getName(), request.getDataType(),
                    request.getFromTimestamp(), request.getToTimestamp());
            return ApiResponse.ok(pageMapper.mapPaged(raw, ScoreResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getScores error: {}", ex.getMessage());
            return ApiResponse.error("SCORE_FETCH_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<ScoreResponse> getScore(String scoreId) {
        try {
            JsonNode raw = apiClient.getScore(scoreId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, ScoreResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getScore({}) error: {}", scoreId, ex.getMessage());
            return ApiResponse.error("SCORE_NOT_FOUND", ex.getMessage());
        } catch (Exception ex) {
            log.error("getScore({}) mapping error", scoreId, ex);
            return ApiResponse.error("SCORE_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<PagedResponse<ScoreConfigResponse>> getScoreConfigs(Integer page, Integer limit) {
        try {
            JsonNode raw = apiClient.getScoreConfigs(page, limit);
            return ApiResponse.ok(pageMapper.mapPaged(raw, ScoreConfigResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getScoreConfigs error: {}", ex.getMessage());
            return ApiResponse.error("SCORE_CONFIG_LIST_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<ScoreConfigResponse> getScoreConfig(String configId) {
        try {
            JsonNode raw = apiClient.getScoreConfig(configId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, ScoreConfigResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getScoreConfig({}) error: {}", configId, ex.getMessage());
            return ApiResponse.error("SCORE_CONFIG_NOT_FOUND", ex.getMessage());
        } catch (Exception ex) {
            log.error("getScoreConfig({}) mapping error", configId, ex);
            return ApiResponse.error("SCORE_CONFIG_MAPPING_ERROR", ex.getMessage());
        }
    }
}
