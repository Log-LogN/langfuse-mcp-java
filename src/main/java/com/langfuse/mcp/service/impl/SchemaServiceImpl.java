package com.langfuse.mcp.service.impl;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.service.SchemaService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SchemaServiceImpl implements SchemaService {

    private static final String SCHEMA = """
            LANGFUSE DATA SCHEMA
            ====================
            TRACE       : id, timestamp, name, userId, sessionId, input, output, metadata,
                          tags, version, release, level(DEFAULT|DEBUG|WARNING|ERROR), environment,
                          latency, totalTokens, totalCost, isPublic, projectId
            OBSERVATION : id, traceId, parentObservationId, type(GENERATION|SPAN|EVENT), name,
                          startTime, endTime, input, output, model, inputTokens, outputTokens,
                          totalTokens, totalCost, level, environment
            SESSION     : id, createdAt, projectId, environment, traces[]
            PROMPT      : name, version, type(text|chat), prompt, labels[], tags[], config{}
            DATASET     : name, description, projectId, createdAt, updatedAt
            DATASET_ITEM: id, datasetName, input, expectedOutput, metadata, createdAt, updatedAt
            SCORE       : id, traceId, observationId, sessionId, name, value,
                          dataType(NUMERIC|CATEGORICAL|BOOLEAN), comment, configId, source,
                          createdAt, updatedAt
            SCORE_CONFIG: id, name, dataType, minValue, maxValue, categories[], createdAt, updatedAt
            USER        : userId, firstTrace, lastTrace, totalTraces, totalTokens, totalCost
            COMMENT     : id, objectType(TRACE|OBSERVATION), objectId, content, authorUserId, createdAt
            DAILY_METRICS: date, traceCount, observationCount, totalTokens, totalCost,
                           latencyP50, latencyP75, latencyP95, latencyP99
            MODEL_USAGE : model, inputTokens, outputTokens, totalTokens, totalCost, requestCount
            SCORE_ANALYTICS: name, mean, median, p25, p75, p90, p95, stddev, count

            NOTE: This MCP server now exposes read operations plus selected write actions
                  for prompts, comments, scores, datasets, models, media, annotation queues,
                  traces, dataset runs, and score configs.
            """;

    @Override
    public ApiResponse<String> getDataSchema() {
        return ApiResponse.ok(SCHEMA);
    }
}
