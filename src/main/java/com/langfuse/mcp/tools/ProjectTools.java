package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.response.ProjectResponse;
import com.langfuse.mcp.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectTools {

    private final ProjectService projectService;

    @McpTool(name = "get_projects_for_api_key", description = """
            Returns the project or projects visible to the currently configured API key.
            With a project-scoped key this normally returns one project.
            With broader credentials, use this to confirm which project metadata is available.
            """)
    public ApiResponse<PagedResponse<ProjectResponse>> getProjectsForApiKey() {
        return projectService.getProjectsForApiKey();
    }
}
