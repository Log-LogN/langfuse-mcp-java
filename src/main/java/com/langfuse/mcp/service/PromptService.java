package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.PromptFilterRequest;
import com.langfuse.mcp.dto.request.PromptGetRequest;
import com.langfuse.mcp.dto.response.PromptResponse;

public interface PromptService {
    ApiResponse<PagedResponse<PromptResponse>> listPrompts(PromptFilterRequest request);

    ApiResponse<PromptResponse> getPrompt(PromptGetRequest request);
}
