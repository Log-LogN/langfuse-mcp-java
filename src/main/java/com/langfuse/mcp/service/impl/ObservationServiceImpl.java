package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ObservationFilterRequest;
import com.langfuse.mcp.dto.response.ObservationResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.ObservationService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObservationServiceImpl implements ObservationService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<ObservationResponse>> fetchObservations(ObservationFilterRequest request) {
        try {
            JsonNode raw = apiClient.getObservations(
                    request.getTraceId(), request.getType(),
                    request.getPage(), request.getLimit());
            return ApiResponse.ok(pageMapper.mapPaged(raw, ObservationResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("fetchObservations error: {}", ex.getMessage());
            return ApiResponse.error("OBS_FETCH_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<ObservationResponse> fetchObservation(String observationId) {
        try {
            JsonNode raw = apiClient.getObservation(observationId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, ObservationResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("fetchObservation({}) error: {}", observationId, ex.getMessage());
            return ApiResponse.error("OBS_NOT_FOUND", ex.getMessage());
        } catch (Exception ex) {
            log.error("fetchObservation({}) mapping error", observationId, ex);
            return ApiResponse.error("OBS_MAPPING_ERROR", ex.getMessage());
        }
    }
}
