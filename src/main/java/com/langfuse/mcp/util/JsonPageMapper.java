package com.langfuse.mcp.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.mcp.dto.common.PagedResponse;
import com.langfuse.mcp.dto.common.PaginationMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralised JSON-to-PagedResponse mapper.
 *
 * <p>Eliminates the repeated mapPaged() boilerplate that existed in every
 * ServiceImpl. All list/filter service methods delegate to this utility.
 *
 * <p>Expected raw JSON shape:
 * <pre>
 * {
 *   "data": [ {...}, {...} ],
 *   "meta": { "page": 1, "limit": 20, "totalItems": 100, "totalPages": 5 }
 * }
 * </pre>
 * If the root node itself is an array (no wrapping object), it is treated as
 * the data array directly and meta will be empty.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonPageMapper {

    private final ObjectMapper objectMapper;

    /**
     * Maps a raw Langfuse API response to a typed {@link PagedResponse}.
     *
     * @param raw  the root JsonNode returned by {@link com.langfuse.mcp.client.LangfuseApiClient}
     * @param type the target DTO class
     * @param <T>  DTO type
     * @return paged response with items and pagination metadata
     */
    public <T> PagedResponse<T> mapPaged(JsonNode raw, Class<T> type) {
        List<T> items = new ArrayList<>();
        JsonNode dataNode = raw.has("data") ? raw.get("data") : raw;

        if (dataNode.isArray()) {
            for (JsonNode node : dataNode) {
                try {
                    items.add(objectMapper.treeToValue(node, type));
                } catch (Exception e) {
                    log.warn("Skipping unmappable {} node: {}", type.getSimpleName(), e.getMessage());
                }
            }
        }

        return new PagedResponse<>(items, buildMeta(raw));
    }

    /**
     * Extracts pagination metadata from the {@code meta} block of a Langfuse response.
     * Returns an empty meta object if no {@code meta} block is present.
     */
    public PaginationMeta buildMeta(JsonNode raw) {
        if (!raw.has("meta")) {
            return PaginationMeta.builder().build();
        }
        JsonNode m = raw.get("meta");
        return PaginationMeta.builder()
                .page(m.path("page").asInt())
                .limit(m.path("limit").asInt())
                .totalItems(m.path("totalItems").asLong())
                .totalPages(m.path("totalPages").asInt())
                .cursor(m.path("cursor").isMissingNode() || m.path("cursor").isNull() ? null : m.path("cursor").asText())
                .build();
    }
}
