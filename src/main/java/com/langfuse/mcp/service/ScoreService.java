package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ScoreFilterRequest;
import com.langfuse.mcp.dto.response.ScoreConfigResponse;
import com.langfuse.mcp.dto.response.ScoreResponse;

public interface ScoreService {
    ApiResponse<PagedResponse<ScoreResponse>> getScores(ScoreFilterRequest request);

    ApiResponse<ScoreResponse> getScore(String scoreId);

    ApiResponse<PagedResponse<ScoreConfigResponse>> getScoreConfigs(Integer page, Integer limit);

    ApiResponse<ScoreConfigResponse> getScoreConfig(String configId);

    ApiResponse<ScoreConfigResponse> createScoreConfig(String name, String dataType, String categoriesJson,
                                                       Double minValue, Double maxValue, String description);

    ApiResponse<ScoreConfigResponse> updateScoreConfig(String configId, String name, String categoriesJson,
                                                       Double minValue, Double maxValue, String description,
                                                       Boolean isArchived);
}
