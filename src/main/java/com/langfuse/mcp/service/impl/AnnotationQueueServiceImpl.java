package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.AnnotationQueueFilterRequest;
import com.langfuse.mcp.dto.request.AnnotationQueueItemFilterRequest;
import com.langfuse.mcp.dto.response.AnnotationQueueItemResponse;
import com.langfuse.mcp.dto.response.AnnotationQueueResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.exception.ResourceNotFoundException;
import com.langfuse.mcp.service.AnnotationQueueService;
import com.langfuse.mcp.util.JsonPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnotationQueueServiceImpl implements AnnotationQueueService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final JsonPageMapper pageMapper;

    @Override
    public ApiResponse<PagedResponse<AnnotationQueueResponse>> listQueues(AnnotationQueueFilterRequest request) {
        try {
            JsonNode raw = apiClient.getAnnotationQueues(request.getPage(), request.getLimit());
            return ApiResponse.ok(pageMapper.mapPaged(raw, AnnotationQueueResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listQueues error: {}", ex.getMessage());
            return ApiResponse.error("ANNOTATION_QUEUE_LIST_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<AnnotationQueueResponse> getQueue(String queueId) {
        try {
            JsonNode raw = apiClient.getAnnotationQueue(queueId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, AnnotationQueueResponse.class));
        } catch (ResourceNotFoundException ex) {
            log.warn("getQueue({}) not found", queueId);
            return ApiResponse.error("ANNOTATION_QUEUE_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("getQueue({}) error: {}", queueId, ex.getMessage());
            return ApiResponse.error("ANNOTATION_QUEUE_FETCH_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("getQueue({}) mapping error", queueId, ex);
            return ApiResponse.error("ANNOTATION_QUEUE_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<AnnotationQueueResponse> createQueue(String name, String description, List<String> scoreConfigId) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            if (description != null) body.put("description", description);
            if (scoreConfigId != null) body.put("scoreConfigIds", scoreConfigId);
            JsonNode raw = apiClient.createAnnotationQueue(body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, AnnotationQueueResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("createQueue error: {}", ex.getMessage());
            return ApiResponse.error("ANNOTATION_QUEUE_CREATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("createQueue mapping error", ex);
            return ApiResponse.error("ANNOTATION_QUEUE_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<PagedResponse<AnnotationQueueItemResponse>> listQueueItems(AnnotationQueueItemFilterRequest request) {
        try {
            JsonNode raw = apiClient.getAnnotationQueueItems(
                    request.getQueueId(), request.getStatus(), request.getPage(), request.getLimit());
            return ApiResponse.ok(pageMapper.mapPaged(raw, AnnotationQueueItemResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("listQueueItems error: {}", ex.getMessage());
            return ApiResponse.error("ANNOTATION_QUEUE_ITEMS_LIST_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<AnnotationQueueItemResponse> getQueueItem(String queueId, String itemId) {
        try {
            JsonNode raw = apiClient.getAnnotationQueueItem(queueId, itemId);
            return ApiResponse.ok(objectMapper.treeToValue(raw, AnnotationQueueItemResponse.class));
        } catch (ResourceNotFoundException ex) {
            log.warn("getQueueItem({}/{}) not found", queueId, itemId);
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("getQueueItem({}/{}) error: {}", queueId, itemId, ex.getMessage());
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_FETCH_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("getQueueItem mapping error", ex);
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<AnnotationQueueItemResponse> createQueueItem(String queueId, String objectId, String objectType,
                                                                    String status
    ) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("objectId", objectId);
            body.put("objectType", objectType);
            if (status != null) {
                body.put("status", status);
            }

            JsonNode raw = apiClient.createAnnotationQueueItem(queueId, body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, AnnotationQueueItemResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("createQueueItem error: {}", ex.getMessage(), ex);
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_CREATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("createQueueItem mapping error", ex);
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<AnnotationQueueItemResponse> updateQueueItem(String queueId, String itemId, String status) {
        try {
            Map<String, Object> body = new HashMap<>();
            if (status != null) body.put("status", status);
            JsonNode raw = apiClient.updateAnnotationQueueItem(queueId, itemId, body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, AnnotationQueueItemResponse.class));
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("updateQueueItem error: {}", ex.getMessage());
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_UPDATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("updateQueueItem mapping error", ex);
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<MutationResponse> deleteQueueItem(String queueId, String itemId) {
        try {
            apiClient.deleteAnnotationQueueItem(queueId, itemId);
            return ApiResponse.ok(MutationResponse.builder()
                    .id(itemId).message("Annotation queue item deleted").build());
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("deleteQueueItem error: {}", ex.getMessage());
            return ApiResponse.error("ANNOTATION_QUEUE_ITEM_DELETE_ERROR", ex.getMessage());
        }
    }

}
