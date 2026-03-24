package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.CommentFilterRequest;
import com.langfuse.mcp.dto.response.CommentResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.exception.ResourceNotFoundException;
import com.langfuse.mcp.service.CommentService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<CommentResponse>> getComments(CommentFilterRequest request) {
        try {
            JsonNode raw = apiClient.getComments(
                    request.getObjectType(), request.getObjectId(),
                    request.getPage(), request.getLimit());
            return ApiResponse.ok(pageMapper.mapPaged(raw, CommentResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("getComments error: {}", ex.getMessage());
            return ApiResponse.error("COMMENT_FETCH_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<CommentResponse> getCommentById(String commentId) {
        try {
            JsonNode raw = apiClient.getComment(commentId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, CommentResponse.class));
        } catch (ResourceNotFoundException ex) {
            log.warn("getCommentById({}) not found", commentId);
            return ApiResponse.error("COMMENT_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("getCommentById({}) error: {}", commentId, ex.getMessage());
            return ApiResponse.error("COMMENT_FETCH_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("getCommentById mapping error", ex);
            return ApiResponse.error("COMMENT_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<MutationResponse> createComment(String objectType, String objectId, String content) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("objectType", objectType);
            body.put("objectId", objectId);
            body.put("content", content);
            body.put("authorUserId", "");
            body.put("projectId", "");
            JsonNode raw = apiClient.post("/api/public/comments", body);
            String id = raw.path("id").asText(null);
            return ApiResponse.ok(MutationResponse.builder().id(id).message("Comment created").build());
        } catch (LangfuseApiException ex) {
            log.error("createComment error: {}", ex.getMessage());
            return ApiResponse.error("COMMENT_CREATE_ERROR", ex.getMessage());
        }
    }
}
