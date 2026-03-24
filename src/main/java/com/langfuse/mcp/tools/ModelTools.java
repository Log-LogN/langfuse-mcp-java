package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.request.ModelFilterRequest;
import com.langfuse.mcp.dto.response.ModelResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelTools {

    private final ModelService modelService;

    @McpTool(name = "list_models", description = """
            Returns a paginated list of all models in the Langfuse project, including both
            Langfuse-managed models and custom models you have defined.
            Each model contains: id, modelName, matchPattern, unit, inputPrice, outputPrice,
            totalPrice, startDate, tokenizerId, isLangfuseManaged.
            Pagination: page is 1-based (default 1), limit controls page size (default 20).
            """)
    public ApiResponse<PagedResponse<ModelResponse>> listModels(
            @McpToolParam(description = "Page number, 1-based. Omit to use default (1).") Integer page,
            @McpToolParam(description = "Results per page. Omit to use default (20).") Integer limit) {
        return modelService.listModels(ModelFilterRequest.builder()
                .page(page != null ? page : 1)
                .limit(limit != null ? limit : 20)
                .build());
    }

    @McpTool(name = "get_model", description = """
            Returns a single model definition by its ID.
            Returns: id, modelName, matchPattern, unit, inputPrice, outputPrice, totalPrice,
            startDate, tokenizerId, isLangfuseManaged, projectId.
            modelId is required.
            """)
    public ApiResponse<ModelResponse> getModel(
            @McpToolParam(description = "The model ID. Required.", required = true) String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "modelId is required");
        }
        return modelService.getModel(modelId.strip());
    }

    @McpTool(name = "create_model", description = """
            Creates a custom model definition for cost tracking and token pricing.
            modelName, matchPattern, and unit are required.
            unit values: TOKENS | CHARACTERS | MILLISECONDS | SECONDS | IMAGES | REQUESTS.
            Prices are per unit in USD (e.g. inputPrice=0.000001 means $1 per million tokens).
            Omit prices for models where you do not want cost tracking.
            startDate format: ISO-8601 date, e.g. 2025-01-01T00:00:00Z.
            """)
    public ApiResponse<ModelResponse> createModel(
            @McpToolParam(description = "Model name used for display and matching. Required.", required = true) String modelName,
            @McpToolParam(description = "Regex pattern to match against generation model names. Required.", required = true) String matchPattern,
            @McpToolParam(description = "Pricing unit: TOKENS | CHARACTERS | MILLISECONDS | SECONDS | IMAGES | REQUESTS. Required.", required = true) String unit,
            @McpToolParam(description = "Input price per unit in USD. Omit if not tracking input cost.") Double inputPrice,
            @McpToolParam(description = "Output price per unit in USD. Omit if not tracking output cost.") Double outputPrice,
            @McpToolParam(description = "Total price per unit in USD (alternative to input/output split). Omit if using input/output prices.") Double totalPrice) {
        if (modelName == null || modelName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "modelName is required");
        }
        if (matchPattern == null || matchPattern.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "matchPattern is required");
        }
        if (unit == null || unit.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "unit is required");
        }
        return modelService.createModel(modelName.strip(), matchPattern.strip(), unit.strip(),
                inputPrice, outputPrice, totalPrice);
    }

    @McpTool(name = "delete_model", description = """
            Deletes a custom model definition by ID.
            Note: Langfuse-managed models cannot be deleted. Only custom models you created can be deleted.
            To override a Langfuse-managed model, create a new custom model with the same modelName instead.
            modelId is required. This action is irreversible.
            """)
    public ApiResponse<MutationResponse> deleteModel(
            @McpToolParam(description = "The model ID to delete. Required.", required = true) String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "modelId is required");
        }
        return modelService.deleteModel(modelId.strip());
    }
}
