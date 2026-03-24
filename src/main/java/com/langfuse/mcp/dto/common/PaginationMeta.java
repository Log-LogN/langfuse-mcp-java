package com.langfuse.mcp.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMeta {
    private Integer page;
    private Integer limit;
    private Long totalItems;
    private Integer totalPages;
    private String cursor;
}
