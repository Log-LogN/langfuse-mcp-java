package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.AnnotationQueueFilterRequest;
import com.langfuse.mcp.dto.request.AnnotationQueueItemFilterRequest;
import com.langfuse.mcp.dto.response.AnnotationQueueItemResponse;
import com.langfuse.mcp.dto.response.AnnotationQueueResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.service.AnnotationQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnotationQueueTools {

    private final AnnotationQueueService annotationQueueService;

    @McpTool(name = "list_annotation_queues", description = """
            Returns a paginated list of annotation queues in the Langfuse project.
            Each queue contains: id, name, description, scoreConfigId, projectId, createdAt, updatedAt.
            Annotation queues are used for human-in-the-loop review workflows.
            Pagination: page is 1-based (default 1), limit controls page size (default 20).
            """)
    public ApiResponse<PagedResponse<AnnotationQueueResponse>> listAnnotationQueues(
            @McpToolParam(description = "Page number, 1-based. Omit to use default (1).") Integer page,
            @McpToolParam(description = "Results per page. Omit to use default (20).") Integer limit) {
        return annotationQueueService.listQueues(AnnotationQueueFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }

    @McpTool(name = "get_annotation_queue", description = """
            Returns a single annotation queue by its ID.
            Returns: id, name, description, scoreConfigId, projectId, createdAt, updatedAt.
            queueId is required.
            """)
    public ApiResponse<AnnotationQueueResponse> getAnnotationQueue(
            @McpToolParam(description = "The annotation queue ID. Required.", required = true) String queueId) {
        if (queueId == null || queueId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "queueId is required");
        }
        return annotationQueueService.getQueue(queueId.strip());
    }

    @McpTool(name = "create_annotation_queue", description = """
            Creates a new annotation queue for human review workflows.
            Returns the created queue with its assigned ID.
            name is required. description and scoreConfigId are optional.
            """)
    public ApiResponse<AnnotationQueueResponse> createAnnotationQueue(
            @McpToolParam(description = "Queue name. Required.", required = true) String name,
            @McpToolParam(description = "Optional description of the queue's purpose.") String description,
            @McpToolParam(description = "Optional score config ID to associate with this queue.") List<String> scoreConfigId) {
        if (name == null || name.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "name is required");
        }
        return annotationQueueService.createQueue(name.strip(), description, scoreConfigId);
    }

    @McpTool(name = "list_annotation_queue_items", description = """
            Returns items in a specific annotation queue, optionally filtered by status.
            status values: PENDING | COMPLETED. Omit status to return all items regardless of status.
            Each item contains: id, queueId, traceId, observationId, status, annotatorUserId, completedAt.
            queueId is required.
            """)
    public ApiResponse<PagedResponse<AnnotationQueueItemResponse>> listAnnotationQueueItems(
            @McpToolParam(description = "The annotation queue ID. Required.", required = true) String queueId,
            @McpToolParam(description = "Filter by status: PENDING | COMPLETED Omit to return all statuses.") String status,
            @McpToolParam(description = "Page number, 1-based. Omit to use default (1).") Integer page,
            @McpToolParam(description = "Results per page. Omit to use default (20).") Integer limit) {
        if (queueId == null || queueId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "queueId is required");
        }
        return annotationQueueService.listQueueItems(AnnotationQueueItemFilterRequest.builder()
                .queueId(queueId.strip())
                .status(status)
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }

    @McpTool(name = "get_annotation_queue_item", description = """
            Returns a specific item from an annotation queue by queue ID and item ID.
            Returns: id, queueId, traceId, observationId, status, annotatorUserId, completedAt.
            Both queueId and itemId are required.
            """)
    public ApiResponse<AnnotationQueueItemResponse> getAnnotationQueueItem(
            @McpToolParam(description = "The annotation queue ID. Required.", required = true) String queueId,
            @McpToolParam(description = "The annotation queue item ID. Required.", required = true) String itemId) {
        if (queueId == null || queueId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "queueId is required");
        }
        if (itemId == null || itemId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "itemId is required");
        }
        return annotationQueueService.getQueueItem(queueId.strip(), itemId.strip());
    }

    @McpTool(name = "create_annotation_queue_item", description = """
            Adds an item to an annotation queue for human review.
            queueId and objectId are required.
            objectType can be SESSION, TRACE, or OBSERVATION.
            status is optional PENDING | COMPLETED.
            """)
    public ApiResponse<AnnotationQueueItemResponse> createAnnotationQueueItem(
            @McpToolParam(description = "The annotation queue ID to add the item to. Required.", required = true)
            String queueId,
            @McpToolParam(description = "Object ID (session, trace, or observation id). Required.", required = true)
            String objectId,
            @McpToolParam(description = "Object type: SESSION | TRACE | OBSERVATION. Required.", required = true)
            String objectType,
            @McpToolParam(description = "Status value. Optional. PENDING | COMPLETED")
            String status
    ) {
        if (queueId == null || queueId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "queueId is required");
        }
        if (objectId == null || objectId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "objectId is required");
        }
        if (objectType == null || objectType.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "objectType is required");
        }
        return annotationQueueService.createQueueItem(queueId.strip(), objectId.strip(), objectType.strip().toUpperCase(),
                status != null && !status.isBlank() ? status.strip().toUpperCase() : null
        );
    }

    @McpTool(name = "update_annotation_queue_item", description = """
            Updates the status of an annotation queue item.
            status values: PENDING | COMPLETED.
            Both queueId and itemId are required.
            """)
    public ApiResponse<AnnotationQueueItemResponse> updateAnnotationQueueItem(
            @McpToolParam(description = "The annotation queue ID. Required.", required = true) String queueId,
            @McpToolParam(description = "The annotation queue item ID. Required.", required = true) String itemId,
            @McpToolParam(description = "New status: PENDING | COMPLETED. Omit to leave status unchanged.") String status) {
        if (queueId == null || queueId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "queueId is required");
        }
        if (itemId == null || itemId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "itemId is required");
        }
        return annotationQueueService.updateQueueItem(queueId.strip(), itemId.strip(), status);
    }

    @McpTool(name = "delete_annotation_queue_item", description = """
            Removes an item from an annotation queue. This action is irreversible.
            Both queueId and itemId are required.
            """)
    public ApiResponse<MutationResponse> deleteAnnotationQueueItem(
            @McpToolParam(description = "The annotation queue ID. Required.", required = true) String queueId,
            @McpToolParam(description = "The annotation queue item ID to remove. Required.", required = true) String itemId) {
        if (queueId == null || queueId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "queueId is required");
        }
        if (itemId == null || itemId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "itemId is required");
        }
        return annotationQueueService.deleteQueueItem(queueId.strip(), itemId.strip());
    }

}
