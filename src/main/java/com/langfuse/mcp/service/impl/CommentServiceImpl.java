package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.CommentFilterRequest;
import com.langfuse.mcp.dto.response.CommentResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.service.CommentService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final LangfuseApiClient apiClient;
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
}
