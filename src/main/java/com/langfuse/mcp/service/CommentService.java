package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.CommentFilterRequest;
import com.langfuse.mcp.dto.response.CommentResponse;
import com.langfuse.mcp.dto.response.MutationResponse;

public interface CommentService {
    ApiResponse<PagedResponse<CommentResponse>> getComments(CommentFilterRequest request);

    ApiResponse<CommentResponse> getCommentById(String commentId);

    ApiResponse<MutationResponse> createComment(String objectType, String objectId, String content);
}
