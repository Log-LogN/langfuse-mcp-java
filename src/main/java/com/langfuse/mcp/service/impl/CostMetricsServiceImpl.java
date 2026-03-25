package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.response.CostMetricsResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.CostMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostMetricsServiceImpl implements CostMetricsService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;

    @Override
    public ApiResponse<CostMetricsResponse> getMetrics(String queryJson) {
        try {
            log.debug("Metrics API query: {}", queryJson);

            JsonNode raw = apiClient.getMetrics(queryJson);

            // Parse response rows — the API returns { "data": [...] }
            List<Map<String, Object>> rows = new ArrayList<>();
            JsonNode dataNode = raw.has("data") ? raw.get("data") : raw;
            if (dataNode.isArray()) {
                for (JsonNode rowNode : dataNode) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    rowNode.properties().forEach(entry ->
                            row.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), Object.class)));
                    rows.add(row);
                }
            }

            CostMetricsResponse response = CostMetricsResponse.builder()
                    .data(rows)
                    .rowCount(rows.size())
                    .build();

            return ApiResponse.ok(response);

        } catch (LangfuseApiException ex) {
            log.error("getMetrics error: {}", ex.getMessage());
            return ApiResponse.error("METRICS_FETCH_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("getMetrics unexpected error", ex);
            return ApiResponse.error("METRICS_ERROR", ex.getMessage());
        }
    }
}
