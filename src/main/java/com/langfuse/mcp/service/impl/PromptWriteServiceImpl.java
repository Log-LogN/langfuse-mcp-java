package com.langfuse.mcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.client.LangfuseApiClient;
import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.dto.response.PromptResponse;
import com.langfuse.mcp.exception.LangfuseApiException;
import com.langfuse.mcp.exception.ResourceNotFoundException;
import com.langfuse.mcp.service.PromptWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromptWriteServiceImpl implements PromptWriteService {

    private final LangfuseApiClient apiClient;
    private final ObjectMapper objectMapper;

    @Override
    public ApiResponse<PromptResponse> createPrompt(String name, String type, Object prompt,
                                                      List<String> labels, List<String> tags,
                                                      Object config) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("type", type);
            body.put("prompt", prompt);
            if (labels != null) body.put("labels", labels);
            if (tags != null) body.put("tags", tags);
            if (config != null) body.put("config", config);
            JsonNode raw = apiClient.createPrompt(body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, PromptResponse.class));
        } catch (LangfuseApiException ex) {
            log.error("createPrompt({}) error: {}", name, ex.getMessage());
            return ApiResponse.error("PROMPT_CREATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("createPrompt mapping error", ex);
            return ApiResponse.error("PROMPT_MAPPING_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<MutationResponse> deletePrompt(String promptName, String label, Integer version) {
        try {
            apiClient.deletePrompt(promptName, label, version);
            return ApiResponse.ok(MutationResponse.builder()
                    .message("Prompt '" + promptName + "' deleted").build());
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error("PROMPT_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("deletePrompt({}) error: {}", promptName, ex.getMessage());
            return ApiResponse.error("PROMPT_DELETE_ERROR", ex.getMessage());
        }
    }

    @Override
    public ApiResponse<PromptResponse> updatePromptLabels(String promptName, Integer version,
                                                           List<String> newLabels) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("newLabels", newLabels);
            JsonNode raw = apiClient.updatePromptLabels(promptName, version, body);
            return ApiResponse.ok(objectMapper.treeToValue(raw, PromptResponse.class));
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.error("PROMPT_NOT_FOUND", ex.getMessage());
        } catch (LangfuseApiException ex) {
            log.error("updatePromptLabels({}) error: {}", promptName, ex.getMessage());
            return ApiResponse.error("PROMPT_LABELS_UPDATE_ERROR", ex.getMessage());
        } catch (Exception ex) {
            log.error("updatePromptLabels mapping error", ex);
            return ApiResponse.error("PROMPT_MAPPING_ERROR", ex.getMessage());
        }
    }
}
