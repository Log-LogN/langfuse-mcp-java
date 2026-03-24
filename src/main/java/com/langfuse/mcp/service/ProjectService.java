package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.response.ProjectResponse;

public interface ProjectService {
    ApiResponse<PagedResponse<ProjectResponse>> getProjectsForApiKey();
}
