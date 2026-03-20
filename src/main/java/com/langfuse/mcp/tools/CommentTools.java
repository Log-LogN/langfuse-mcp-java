package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.CommentFilterRequest;
import com.langfuse.mcp.dto.response.CommentResponse;
import com.langfuse.mcp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentTools {

    private final CommentService commentService;

    @McpTool(name = "get_comments", description = """
            List comments filtered by objectType and objectId.
            objectType values: TRACE | OBSERVATION.
            Returns: id, objectType, objectId, content, authorUserId, createdAt.
            Read-only.
            """)
    public ApiResponse<PagedResponse<CommentResponse>> getComments(
            @McpToolParam(description = "Object type: TRACE | OBSERVATION") String objectType,
            @McpToolParam(description = "Trace ID or Observation ID to filter by") String objectId,
            @McpToolParam(description = "Page number") Integer page,
            @McpToolParam(description = "Items per page") Integer limit) {

        return commentService.getComments(CommentFilterRequest.builder()
                .objectType(objectType).objectId(objectId)
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }
}
