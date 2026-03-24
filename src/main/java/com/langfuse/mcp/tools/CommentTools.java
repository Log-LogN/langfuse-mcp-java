package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.CommentFilterRequest;
import com.langfuse.mcp.dto.response.CommentResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
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

    @McpTool(name = "get_comment", description = """
            Returns a single comment by its ID.
            Returns: id, objectType, objectId, content, authorUserId, createdAt, updatedAt.
            commentId is required.
            """)
    public ApiResponse<CommentResponse> getComment(
            @McpToolParam(description = "The comment ID. Required.", required = true) String commentId) {
        if (commentId == null || commentId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "commentId is required");
        }
        return commentService.getCommentById(commentId.strip());
    }

    @McpTool(name = "create_comment", description = """
            Creates a comment attached to a trace, observation, session, or prompt.
            objectType values: TRACE | OBSERVATION | SESSION | PROMPT.
            Both objectType and objectId are required along with content.
            Returns the created comment with its assigned ID.
            """)
    public ApiResponse<MutationResponse> createComment(
            @McpToolParam(description = "Object type to attach the comment to: TRACE | OBSERVATION | SESSION | PROMPT. Required.", required = true) String objectType,
            @McpToolParam(description = "ID of the trace, observation, session, or prompt to attach the comment to. Required.", required = true) String objectId,
            @McpToolParam(description = "Comment text content. Required.", required = true) String content) {
        if (objectType == null || objectType.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "objectType is required");
        }
        if (objectId == null || objectId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "objectId is required");
        }
        if (content == null || content.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "content is required");
        }
        return commentService.createComment(objectType.strip(), objectId.strip(), content.strip());
    }
}
