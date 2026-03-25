# Langfuse MCP Server — Java / Spring AI

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.0--M3-6DB33F?style=flat&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-red?style=flat&logo=lombok&logoColor=white)

A production-grade MCP server that connects any MCP-compatible AI agent to your Langfuse observability data.  
Query traces, debug errors, inspect sessions, manage prompts, run evaluations, annotate data, and configure models — all through natural language.

> **Transport:** Streamable HTTP on port 8080, compatible with Cursor, Claude Desktop, VS Code / GitHub Copilot, and any MCP client that supports HTTP transport.

---

## Why this server?

| Capability | This server | Official Langfuse MCP |
|---|---|---|
| Traces & Observations | ✅ | ❌ |
| Sessions & Users | ✅ | ❌ |
| Exception tracking | ✅ | ❌ |
| Prompt management (read + write) | ✅ | ✅ read-only |
| Dataset & run management | ✅ | ❌ |
| Scores & score configs | ✅ | ❌ |
| Annotation queues | ✅ | ❌ |
| Comments | ✅ | ❌ |
| Model definitions | ✅ | ❌ |
| LLM connections | ✅ | ❌ |
| Project introspection | ✅ | ❌ |
| Schema introspection | ✅ | ❌ |
| Java / Spring AI | ✅ | ❌ (Python) |

---

## Prerequisites

- **Java 21** or later
- **Maven 3.9+** (or use the Docker build — no local Maven required)
- A [Langfuse](https://langfuse.com) account with an API key pair (`public-key` + `secret-key`)

---

## Quick Start

```bash
# 1. Build
mvn clean package -DskipTests

# 2. Set credentials
export LANGFUSE_PUBLIC_KEY=pk-lf-...
export LANGFUSE_SECRET_KEY=sk-lf-...
export LANGFUSE_HOST=https://cloud.langfuse.com

# 3. Run (Streamable HTTP transport — port 8080)
java -jar target/langfuse-mcp-1.0.0.jar

# 4. Verify
curl http://localhost:8080/actuator/health

# 5. Inspect all tools
npx @modelcontextprotocol/inspector http://localhost:8080/mcp
```

Get credentials from [Langfuse Cloud](https://cloud.langfuse.com) → Settings → API Keys.  
Self-hosted Langfuse? Set `LANGFUSE_HOST` to your instance URL.

---

## Configuration

All configuration is driven by environment variables (or `application.yml` for local overrides).

| Property | Env var | Required | Default | Description |
|---|---|---|---|---|
| `langfuse.public-key` | `LANGFUSE_PUBLIC_KEY` | ✅ | — | Langfuse project public key |
| `langfuse.secret-key` | `LANGFUSE_SECRET_KEY` | ✅ | — | Langfuse project secret key |
| `langfuse.host` | `LANGFUSE_HOST` | ✅ | — | Langfuse base URL, e.g. `https://cloud.langfuse.com` |
| `langfuse.timeout` | `LANGFUSE_TIMEOUT` | ❌ | `30s` | HTTP request timeout — Spring Duration format, e.g. `30s`, `1m`, `90s` |
| `langfuse.read-only` | — | ❌ | `true` | Informational flag; write operations are available through specific tools |

### Trailing slash handling

`LANGFUSE_HOST` may be specified with or without a trailing slash — the server normalises it automatically.

---

## Client Configuration

### Cursor (`.cursor/mcp.json`)

```json
{
  "mcpServers": {
    "langfuse": {
      "url": "http://localhost:8080/mcp"
    }
  }
}
```

### Claude Desktop (`claude_desktop_config.json`)

```json
{
  "mcpServers": {
    "langfuse": {
      "url": "http://localhost:8080/mcp"
    }
  }
}
```

On macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`

### VS Code / GitHub Copilot

**URL mode:**

```json
{
  "github.copilot.chat.mcp.servers": {
    "langfuse": {
      "url": "http://localhost:8080/mcp"
    }
  }
}
```

**Command mode** (stdio-only clients):

```json
{
  "github.copilot.chat.mcp.servers": {
    "langfuse": {
      "command": "java",
      "args": ["-jar", "/absolute/path/to/langfuse-mcp-1.0.0.jar"],
      "env": {
        "LANGFUSE_PUBLIC_KEY": "pk-lf-...",
        "LANGFUSE_SECRET_KEY": "sk-lf-...",
        "LANGFUSE_HOST": "https://cloud.langfuse.com"
      }
    }
  }
}
```

> **Note:** The MCP endpoint is `/mcp` (streamable HTTP). The legacy SSE `/sse` endpoint is not used by this server.

---

## Docker

The `Dockerfile` is a multi-stage build: it compiles the Spring Boot jar inside Docker and runs the MCP server on port `8080`. No local Maven installation is needed.

```bash
# Build image (compiles inside Docker)
docker build -t langfuse-mcp:latest .

# Run
docker run --rm -p 8080:8080 \
  -e LANGFUSE_PUBLIC_KEY=pk-lf-... \
  -e LANGFUSE_SECRET_KEY=sk-lf-... \
  -e LANGFUSE_HOST=https://cloud.langfuse.com \
  langfuse-mcp:latest
```

After the container starts:

| Endpoint | URL |
|---|---|
| Health check | `http://localhost:8080/actuator/health` |
| Ping | `http://localhost:8080/ping` |
| MCP endpoint | `http://localhost:8080/mcp` |

**Langfuse running in another container on the same host:**

```bash
-e LANGFUSE_HOST=http://host.docker.internal:3000
```

---

## Tools Reference (55 total)

Every tool returns a consistent `ApiResponse<T>` envelope:

```json
{ "success": true,  "data": { ... }, "timestamp": "2025-01-15T10:30:00Z" }
{ "success": false, "errorCode": "TRACE_NOT_FOUND", "errorMessage": "...", "timestamp": "..." }
```

Paginated list responses wrap their items in a `PagedResponse<T>`:

```json
{
  "data": [ ... ],
  "meta": { "page": 1, "limit": 20, "totalItems": 142, "totalPages": 8 }
}
```

Pagination is 1-based (`page` defaults to `1`). `limit` defaults to `20` and is capped at `100` where noted. To page through results, increment `page` while keeping `limit` fixed.

---

### Traces (8 tools)

| Tool | Description |
|---|---|
| `fetch_traces` | Paginated list of traces. Filter by `userId`, `name`, `sessionId`, `tags`, `fromTimestamp`, `toTimestamp`. |
| `fetch_trace` | Full detail of a single trace including nested observations, input/output, metadata, latency, and token usage. Requires `traceId`. |
| `find_exceptions` | Traces whose `level` equals `ERROR`. Supports time range and pagination. |
| `find_exceptions_in_file` | Error-level traces whose metadata contains a given file name substring. Requires `fileName`. |
| `get_exception_details` | Full detail of a single error trace. Requires `traceId`. |
| `get_error_count` | Count of `ERROR`-level traces in a time range (scans up to 500 traces). |
| `delete_trace` | Permanently deletes a single trace by ID. **Irreversible.** |
| `delete_traces` | Permanently deletes multiple traces. Pass a comma-separated list of trace IDs. **Irreversible.** |

---

### Sessions (3 tools)

| Tool | Description |
|---|---|
| `fetch_sessions` | Paginated list of sessions with optional time range filter. |
| `get_session_details` | Full session detail including all its traces. Requires `sessionId`. |
| `get_user_sessions` | All sessions for a specific user with pagination. Requires `userId`. |

---

### Prompts (5 tools)

| Tool | Description |
|---|---|
| `list_prompts` | Paginated list of all prompts in the project. |
| `get_prompt` | Fetch a prompt by name. Optionally pin to a `version` number or a `label` (e.g. `production`, `staging`). |
| `create_prompt` | Create a new prompt or append a new version to an existing prompt. `type` is `text` (plain string) or `chat` (JSON array of `{role, content}` messages). Supports comma-separated `labels` and `tags`. |
| `delete_prompt` | Delete prompt versions by name. Scope to a specific `label` or `version`; omit both to delete all versions. **Irreversible.** |
| `update_prompt_labels` | Replace the full label set on a specific prompt version. Supply an empty string to remove all labels. The `latest` label is reserved by Langfuse. |

---

### Datasets (7 tools)

| Tool | Description |
|---|---|
| `list_datasets` | Paginated list of all evaluation datasets. |
| `get_dataset` | Fetch a dataset by exact name. |
| `create_dataset` | Create a new dataset. Optionally supply `description`, `metadataJson`, `inputSchemaJson`, and `expectedOutputSchemaJson` (all as JSON strings). |
| `list_dataset_items` | Paginated list of items in a dataset. Requires `datasetName`. |
| `get_dataset_item` | Fetch a single dataset item by ID. |
| `create_dataset_item` | Create or upsert a dataset item. Optionally link to a `sourceTraceId` or `sourceObservationId`. Supports `itemId` for upsert semantics. |
| `delete_dataset_item` | Permanently delete a dataset item by ID. **Irreversible.** |

---

### Dataset Runs (5 tools)

| Tool | Description |
|---|---|
| `list_dataset_runs` | Paginated list of experiment runs for a dataset. Requires `datasetName`. |
| `get_dataset_run` | Full run detail including all run items. Requires `datasetName` and `runName`. |
| `delete_dataset_run` | Delete a run and all its items. **Irreversible.** Requires `datasetName` and `runName`. |
| `list_dataset_run_items` | Paginated list of items in a run. Requires `datasetId` and `runName`. |
| `create_dataset_run_item` | Create a run item linking a dataset item to a trace/observation. Creates the run automatically if it does not yet exist. |

---

### Metrics (1 tool)

| Tool | Description |
|---|---|
| `get_cost_metrics` | Query Langfuse cost, token, latency, and usage analytics via the Metrics API v1. Mirrors: GET /api/public/metrics?query=<json>. Pass the full query as a JSON string. All aggregation is server-side. |

This tool accepts a single required parameter `query` which must be a JSON-serialised string matching the Metrics API schema. Examples (pass these as a single JSON string):

- Total cost last 7 days:

  {"view":"traces","metrics":[{"measure":"totalCost","aggregation":"sum"}],"fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}

- Daily cost trend this week:

  {"view":"traces","metrics":[{"measure":"totalCost","aggregation":"sum"},{"measure":"count","aggregation":"count"}],"timeDimension":{"granularity":"day"},"fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}

- Cost by model:

  {"view":"observations","dimensions":[{"field":"providedModelName"}],"metrics":[{"measure":"totalCost","aggregation":"sum"},{"measure":"totalTokens","aggregation":"sum"}],"fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}

- Cost for a specific user:

  {"view":"traces","metrics":[{"measure":"totalCost","aggregation":"sum"}],"filters":[{"column":"userId","operator":"=","value":"user-123","type":"string"}],"fromTimestamp":"2026-03-18T00:00:00Z","toTimestamp":"2026-03-25T23:59:59Z"}

- Production environment only:

  filters: [{"column":"environment","operator":"=","value":"production","type":"string"}]

---

### Scores (6 tools)

| Tool | Description |
|---|---|
| `get_scores` | Paginated list of evaluation scores. Filter by `traceId`, `observationId`, `name`, `dataType` (`NUMERIC`\|`CATEGORICAL`\|`BOOLEAN`), and time range. |
| `get_score` | Fetch a single score by ID. |
| `get_score_configs` | Paginated list of score config schemas. |
| `get_score_config` | Fetch a single score config by ID. |
| `create_score_config` | Create a score config. `NUMERIC` supports optional `minValue`/`maxValue`. `CATEGORICAL` accepts a `categoriesJson` array of `{label, value}` objects. |
| `update_score_config` | Update an existing score config. Optionally set `isArchived` to archive it. |

---

### Annotation Queues (8 tools)

| Tool | Description |
|---|---|
| `list_annotation_queues` | Paginated list of annotation queues. |
| `get_annotation_queue` | Fetch a single queue by ID. |
| `create_annotation_queue` | Create a queue for human-in-the-loop review. Optionally link a `scoreConfigId`. |
| `list_annotation_queue_items` | Paginated list of items in a queue. Optionally filter by `status` (`PENDING`\|`COMPLETED`). Requires `queueId`. |
| `get_annotation_queue_item` | Fetch a specific queue item by `queueId` and `itemId`. |
| `create_annotation_queue_item` | Add a trace, observation, or session to a queue for review. `objectType` is `TRACE`, `OBSERVATION`, or `SESSION`. |
| `update_annotation_queue_item` | Update the status of a queue item (`PENDING`\|`COMPLETED`). |
| `delete_annotation_queue_item` | Remove an item from a queue. **Irreversible.** |

---

### Comments (3 tools)

| Tool | Description |
|---|---|
| `get_comments` | Paginated list of comments. Optionally filter by `objectType` (`TRACE`\|`OBSERVATION`) and `objectId`. |
| `get_comment` | Fetch a single comment by ID. |
| `create_comment` | Attach a comment to a trace, observation, session, or prompt. `objectType` values: `TRACE`, `OBSERVATION`, `SESSION`, `PROMPT`. |

---

### Models (4 tools)

| Tool | Description |
|---|---|
| `list_models` | Paginated list of all model definitions (Langfuse-managed and custom). |
| `get_model` | Fetch a model definition by ID. |
| `create_model` | Create a custom model for cost tracking. Requires `modelName`, `matchPattern` (regex), and `unit` (`TOKENS`\|`CHARACTERS`\|`MILLISECONDS`\|`SECONDS`\|`IMAGES`\|`REQUESTS`). Optionally set per-unit USD prices. |
| `delete_model` | Delete a custom model definition. Langfuse-managed models cannot be deleted. **Irreversible.** |

---

### LLM Connections (2 tools)

| Tool | Description |
|---|---|
| `list_llm_connections` | Paginated list of LLM provider connections (secret keys are masked in the response). |
| `upsert_llm_connection` | Create or update a provider connection by `provider` name (e.g. `openai`, `anthropic`, `azure`, `google`). Upserts by provider — if a connection already exists it is updated. |

---

### Project (1 tool)

| Tool | Description |
|---|---|
| `get_projects_for_api_key` | Returns the project(s) visible to the configured API key. Useful for confirming credentials and project metadata. |

---

### Users (1 tool)

| Tool | Description |
|---|---|
| `get_user_traces` | All traces for a specific Langfuse user ID with pagination. Requires `userId`. |

---

### Schema (1 tool)

| Tool | Description |
|---|---|
| `get_data_schema` | Returns the full Langfuse data model: all entity types, fields, and valid enum values. Call this first to understand the available data structures before running queries. |

---

## Architecture

```
MCP Client (Cursor / Claude Desktop / Copilot / other)
    │   Streamable HTTP transport (/mcp)
    ▼
Tool class  (@McpTool — validates required params, delegates to service)
    ▼
Service interface + impl  (business logic, filtering, error mapping)
    ▼
LangfuseApiClient  (HTTP gateway — GET / POST / PATCH / DELETE, typed exceptions)
    ▼
Langfuse Public REST API
```

The architecture is strictly layered:

- **`client/`** — Langfuse integration boundary: HTTP with Basic-Auth (Apache HttpComponents 5), typed exceptions, `UriComponentsBuilder` for query params
- **`service/`** — domain logic: filtering, mapping, pagination, error translation into `ApiResponse`
- **`tools/`** — MCP surface: agent-friendly descriptions, parameter validation, delegation to services
- **Spring Boot** — runtime and transport wrapper only

### API Client

`LangfuseApiClient` supports four HTTP methods. All methods throw `LangfuseApiException` or `ResourceNotFoundException` on error, which the service layer converts into structured `ApiResponse.error(...)` responses — agents never see raw stack traces.

| Method | Used for |
|---|---|
| `GET` | All read operations |
| `POST` | Create operations |
| `PATCH` | Update operations |
| `DELETE` | Delete operations |

---

## Package Structure

```
com.langfuse.mcp
├── LangfuseMcpApplication.java          @SpringBootApplication @ConfigurationPropertiesScan
├── config/
│   ├── LangfuseProperties.java          @ConfigurationProperties — publicKey, secretKey, host, timeout, readOnly
│   ├── LangfuseClientConfig.java        RestClient bean — Basic-Auth, Apache HttpComponents 5, configurable timeout
│   └── JacksonConfig.java               Primary ObjectMapper (JSR310, ignore unknown fields)
├── client/
│   └── LangfuseApiClient.java           HTTP gateway (GET/POST/PATCH/DELETE); typed exceptions; UriComponentsBuilder queries
├── controller/
│   └── PingController.java              GET /ping → {"status":"ok"}
├── exception/
│   ├── LangfuseApiException.java        Wraps HTTP/connectivity errors — statusCode + endpoint
│   └── ResourceNotFoundException.java   Thrown on HTTP 404
├── dto/
│   ├── common/    ApiResponse · PagedResponse · PaginationMeta
│   ├── request/   Filter/get request classes (12 classes)
│   └── response/  Response classes (19 classes — JsonNode for open-schema fields)
├── service/       Interfaces (15): Trace · Session · Prompt · PromptWrite · Dataset · DatasetRun
│   │              · Score · AnnotationQueue · Comment · Model · LlmConnection · Project · User · Schema · CostMetrics
│   └── impl/      *ServiceImpl (15) — business logic, filtering, error mapping
├── tools/         @McpTool classes (15) — param validation, delegation, agent-friendly descriptions
│   ├── TraceTools.java             (8 tools)
│   ├── SessionTools.java           (3 tools)
│   ├── PromptTools.java            (2 tools)
│   ├── PromptWriteTools.java       (3 tools)
│   ├── DatasetTools.java           (7 tools)
│   ├── DatasetRunTools.java        (5 tools)
│   ├── ScoreTools.java             (6 tools)
│   ├── AnnotationQueueTools.java   (8 tools)
│   ├── CommentTools.java           (3 tools)
│   ├── ModelTools.java             (4 tools)
│   ├── LlmConnectionTools.java     (2 tools)
│   ├── ProjectTools.java           (1 tool)
│   ├── UserTools.java              (1 tool)
│   ├── SchemaTools.java            (1 tool)
│   └── CostMetricsTools.java       (1 tool)
└── util/
    └── JsonPageMapper.java         Centralised JSON → PagedResponse mapper (no duplication)
```

---

## Running Tests

```bash
mvn test
```

Test coverage includes:

- `LangfusePropertiesBindingTest` — config binding from `application-test.yml` and property-level validation
- `PromptWriteServiceImplTest` — service logic for prompt create / delete / label update
- `ProjectServiceImplTest` — project API response mapping
- `ObservationServiceImplTest` — observation fetch and field mapping
- `MetricsServiceImplTest` — metrics aggregation logic

Tests run with `spring.ai.mcp.server.enabled=false` (set in `src/test/resources/application-test.yml`) so no MCP transport is started during test execution.

---

## Troubleshooting

### `TRACE_FETCH_ERROR: HTTP/1.1 header parser received no bytes`

Connectivity issue — not a code bug. Check:

1. `LANGFUSE_HOST` points to a running Langfuse instance
2. The host is reachable from the JVM process
3. For Docker: use `host.docker.internal` instead of `localhost`
4. The scheme matches your server (`http://` vs `https://`)
5. Confirm the API is up: `curl $LANGFUSE_HOST/api/public/health`

### `INVALID_INPUT: <param> is required`

A required parameter was not provided. All `required = true` parameters are validated at the tool layer before any HTTP call is made.

### Connection timeouts

Increase the timeout:

```bash
export LANGFUSE_TIMEOUT=60s
```

### Agent cannot see the server

1. Confirm the server is running: `curl http://localhost:8080/actuator/health`
2. Confirm the MCP endpoint is reachable: `curl http://localhost:8080/ping`
3. Check that the client config URL points to `http://localhost:8080/mcp`
4. Inspect all available tools: `npx @modelcontextprotocol/inspector http://localhost:8080/mcp`

### Langfuse-managed models cannot be deleted

`delete_model` only works for custom model definitions you have created. To override a Langfuse-managed model's pricing, create a new custom model with the same `modelName`.

---

## License

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat&logo=opensourceinitiative&logoColor=white)](https://opensource.org/licenses/MIT)
