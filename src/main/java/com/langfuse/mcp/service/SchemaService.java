package com.langfuse.mcp.service;

import com.langfuse.mcp.dto.common.ApiResponse;

public interface SchemaService {
    ApiResponse<String> getDataSchema();
}
