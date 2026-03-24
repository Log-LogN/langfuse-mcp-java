package com.langfuse.mcp.tools;

import com.langfuse.mcp.dto.common.ApiResponse;
import com.langfuse.mcp.dto.response.MutationResponse;
import com.langfuse.mcp.dto.response.PromptResponse;
import com.langfuse.mcp.service.PromptWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PromptWriteTools {

    private final PromptWriteService promptWriteService;

    @McpTool(name = "create_prompt", description = """
            Creates a new version of a prompt. If the prompt name does not exist, a new prompt is created.
            If it does exist, a new version is appended.
            type values: text (plain string prompt) | chat (array of message objects).
            For text prompts, provide prompt as a plain string.
            For chat prompts, provide prompt as a JSON array of message objects with role and content fields.
            labels examples: production, staging, latest. The 'latest' label is managed by Langfuse automatically.
            Returns the created prompt version with its assigned version number.
            name, type, and prompt are required.
            """)
    public ApiResponse<PromptResponse> createPrompt(
            @McpToolParam(description = "Prompt name. If it already exists, a new version is created. Required.", required = true) String name,
            @McpToolParam(description = "Prompt type: text | chat. Required.", required = true) String type,
            @McpToolParam(description = "Prompt content. Plain string for text type; JSON array of messages for chat type. Required.", required = true) String prompt,
            @McpToolParam(description = "Comma-separated labels to apply, e.g. production,staging. Omit to create without labels.") String labels,
            @McpToolParam(description = "Comma-separated tags for organisation, e.g. summarisation,rag. Omit if no tags needed.") String tags) {
        if (name == null || name.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "name is required");
        }
        if (type == null || type.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "type is required (text or chat)");
        }
        if (prompt == null || prompt.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "prompt content is required");
        }
        List<String> labelList = (labels != null && !labels.isBlank())
                ? Arrays.asList(labels.split(","))
                : null;
        List<String> tagList = (tags != null && !tags.isBlank())
                ? Arrays.asList(tags.split(","))
                : null;
        // For chat prompts the agent passes a JSON string; pass as-is (service serialises it)
        return promptWriteService.createPrompt(name.strip(), type.strip(), prompt.strip(),
                labelList, tagList, null);
    }

    @McpTool(name = "delete_prompt", description = """
            Deletes prompt versions by name. Behaviour depends on which filters are supplied:
            - Omit both label and version: deletes ALL versions of the prompt.
            - Supply label only: deletes all versions carrying that label.
            - Supply version only: deletes that specific version number.
            promptName is required. This action is irreversible.
            """)
    public ApiResponse<MutationResponse> deletePrompt(
            @McpToolParam(description = "Prompt name (exact match). Required.", required = true) String promptName,
            @McpToolParam(description = "Delete only versions with this label, e.g. staging. Omit to ignore label filter.") String label,
            @McpToolParam(description = "Delete only this specific version number. Omit to ignore version filter.") Integer version) {
        if (promptName == null || promptName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "promptName is required");
        }
        return promptWriteService.deletePrompt(promptName.strip(), label, version);
    }

    @McpTool(name = "update_prompt_labels", description = """
            Replaces the labels on a specific prompt version.
            newLabels completely replaces the existing label set on that version.
            The 'latest' label is reserved and managed by Langfuse — do not include it.
            Both promptName and version are required.
            newLabels is required — supply an empty string to remove all labels from this version.
            """)
    public ApiResponse<PromptResponse> updatePromptLabels(
            @McpToolParam(description = "Prompt name (exact match). Required.", required = true) String promptName,
            @McpToolParam(description = "Version number to update. Required.", required = true) Integer version,
            @McpToolParam(description = "Comma-separated new labels, e.g. production,staging. Supply empty string to remove all labels.", required = true) String newLabels) {
        if (promptName == null || promptName.isBlank()) {
            return ApiResponse.error("INVALID_INPUT", "promptName is required");
        }
        if (version == null) {
            return ApiResponse.error("INVALID_INPUT", "version is required");
        }
        List<String> labelList = (newLabels != null && !newLabels.isBlank())
                ? Arrays.asList(newLabels.split(","))
                : List.of();
        return promptWriteService.updatePromptLabels(promptName.strip(), version, labelList);
    }
}
