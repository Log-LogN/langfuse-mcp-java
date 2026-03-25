package com.langfuse.mcp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response envelope returned by the Langfuse Metrics API.
 *
 * <p>The {@code data} field contains a list of result rows where each row is a
 * map of dynamic field names to values (column names vary by query).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CostMetricsResponse {

    /** The rows returned by the Metrics API. Each row is a dynamic map of column→value. */
    private List<Map<String, Object>> data;

    /** Total number of rows in the result set. */
    private Integer rowCount;
}
