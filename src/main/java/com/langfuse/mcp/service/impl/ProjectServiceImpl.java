package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.response.ProjectResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.ProjectService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final LangfuseApiClient apiClient;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<ProjectResponse>> getProjectsForApiKey() {
        try {
            JsonNode raw = apiClient.getProject();
            return ApiResponse.ok(pageMapper.mapPaged(raw, ProjectResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getProjectsForApiKey error: {}", ex.getMessage());
            return ApiResponse.error("PROJECT_FETCH_ERROR", ex.getMessage());
        }
    }
}
