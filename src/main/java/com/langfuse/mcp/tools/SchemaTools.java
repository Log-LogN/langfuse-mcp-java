package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.service.SchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchemaTools {

    private final SchemaService schemaService;

    @McpTool(name = "get_data_schema", description = """
            Returns the Langfuse data model schema: all entity types, fields, and valid enum values.
            Call this first before running any query to understand the available data structures.
            Read-only.
            """)
    public ApiResponse<String> getDataSchema() {
        return schemaService.getDataSchema();
    }
}
