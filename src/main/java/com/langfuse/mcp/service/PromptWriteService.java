package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.dto.response.PromptResponse;

import java.util.List;

public interface PromptWriteService {
    ApiResponse<PromptResponse> createPrompt(String name, String type, Object prompt,
                                              List<String> labels, List<String> tags,
                                              Object config);
    ApiResponse<MutationResponse> deletePrompt(String promptName, String label, Integer version);
    ApiResponse<PromptResponse> updatePromptLabels(String promptName, Integer version,
                                                    List<String> newLabels);
}
