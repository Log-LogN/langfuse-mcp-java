package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.AnnotationQueueFilterRequest;
import com.langfuse.mcp.dto.request.AnnotationQueueItemFilterRequest;
import com.langfuse.mcp.dto.response.AnnotationQueueItemResponse;
import com.langfuse.mcp.dto.response.AnnotationQueueResponse;
import com.langfuse.mcp.dto.response.MutationResponse;

import java.util.List;

public interface AnnotationQueueService {
    ApiResponse<PagedResponse<AnnotationQueueResponse>> listQueues(AnnotationQueueFilterRequest request);

    ApiResponse<AnnotationQueueResponse> getQueue(String queueId);

    ApiResponse<AnnotationQueueResponse> createQueue(String name, String description, List<String> scoreConfigId);

    ApiResponse<PagedResponse<AnnotationQueueItemResponse>> listQueueItems(AnnotationQueueItemFilterRequest request);

    ApiResponse<AnnotationQueueItemResponse> getQueueItem(String queueId, String itemId);

    ApiResponse<AnnotationQueueItemResponse> createQueueItem(String queueId, String objectId, String objectType,
                                                             String status);

    ApiResponse<AnnotationQueueItemResponse> updateQueueItem(String queueId, String itemId, String status);

    ApiResponse<MutationResponse> deleteQueueItem(String queueId, String itemId);

}
