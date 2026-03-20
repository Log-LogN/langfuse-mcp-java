# Langfuse MCP Server — Java / Spring AI

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.0--M3-6DB33F?style=flat&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-red?style=flat&logo=lombok&logoColor=white)

A production-hardened, **read-only** MCP server that connects any MCP-compatible AI agent to your Langfuse observability data.  
Query traces, debug errors, inspect sessions, analyze prompts, and explore datasets — all through natural language.

> **Transport:** HTTP/SSE on port 8080, compatible with Cursor and Claude Desktop out of the box.

---

## Why this server?

| Capability | This server | Official Langfuse MCP |
|---|---|---|
| Traces & Observations | ✅ | ❌ |
| Sessions & Users | ✅ | ❌ |
| Exception tracking | ✅ | ❌ |
| Prompt management | ✅ | ✅ |
| Dataset management | ✅ | ❌ |
| Scores & metrics | ✅ | ❌ |
| Schema introspection | ✅ | ❌ |
| Java / Spring AI | ✅ | ❌ (Python) |
| Read-only by design | ✅ | N/A |

---

## Tools (24 total)

| Category | Count | Tools |
|---|---|---|
| Traces | 6 | `fetch_traces`, `fetch_trace`, `find_exceptions`, `find_exceptions_in_file`, `get_exception_details`, `get_error_count` |
| Observations | 2 | `fetch_observations`, `fetch_observation` |
| Sessions | 3 | `fetch_sessions`, `get_session_details`, `get_user_sessions` |
| Prompts | 2 | `list_prompts`, `get_prompt` |
| Datasets | 4 | `list_datasets`, `get_dataset`, `list_dataset_items`, `get_dataset_item` |
| Scores | 4 | `get_scores`, `get_score`, `get_score_configs`, `get_score_config` |
| Users | 1 | `get_user_traces` |
| Comments | 1 | `get_comments` |
| Schema | 1 | `get_data_schema` |

---

## Quick start

```bash
# 1. Build
mvn clean package

# 2. Set credentials
export LANGFUSE_PUBLIC_KEY=pk-lf-...
export LANGFUSE_SECRET_KEY=sk-lf-...
export LANGFUSE_HOST=https://cloud.langfuse.com

# 3. Run (SSE transport — port 8080)
java -jar target/langfuse-mcp-1.0.0.jar

# 4. Verify
curl http://localhost:8080/actuator/health

# 5. Inspect all tools
npx @modelcontextprotocol/inspector http://localhost:8080/sse
```

Get credentials from [Langfuse Cloud](https://cloud.langfuse.com) → Settings → API Keys.  
Self-hosted Langfuse? Set `LANGFUSE_HOST` to your instance URL.

---

## Architecture

```
MCP Client (Cursor / Claude Desktop / other)
    │   HTTP/SSE transport (/sse + /mcp/message)
    ▼
Tool class  (@McpTool — thin delegation layer, validates required params)
    ▼
Service interface + impl  (business logic, error mapping, server-side filtering)
    ▼
LangfuseApiClient  (GET-only HTTP gateway, typed exceptions)
    ▼
Langfuse Public REST API
```

The architecture is strictly layered:

- `client/` — Langfuse integration boundary (HTTP, Basic-Auth, error handling)
- `service/` — domain logic (filtering, mapping, pagination)
- `tools/` — MCP-facing surface (descriptions, param validation, delegation)
- Spring Boot — runtime and transport wrapper only

**Read-only by design:** `LangfuseApiClient` exposes only GET methods. No POST, PATCH, or DELETE operations exist anywhere in the codebase. The `langfuse.read-only=true` flag is enforced at the properties level.

Every tool returns a consistent `ApiResponse<T>` envelope:

```json
{ "success": true,  "data": { ... }, "timestamp": "2025-01-15T10:30:00Z" }
{ "success": false, "errorCode": "TRACE_NOT_FOUND", "errorMessage": "...", "timestamp": "..." }
```

---

## Configuration

| Property | Env var | Required | Default | Description |
|---|---|---|---|---|
| `langfuse.public-key` | `LANGFUSE_PUBLIC_KEY` | ✅ | — | Langfuse project public key |
| `langfuse.secret-key` | `LANGFUSE_SECRET_KEY` | ✅ | — | Langfuse project secret key |
| `langfuse.host` | `LANGFUSE_HOST` | ✅ | — | Langfuse base URL |
| `langfuse.timeout` | `LANGFUSE_TIMEOUT` | ❌ | `30s` | HTTP request timeout (Spring Duration format, e.g. `30s`, `1m`) |
| `langfuse.read-only` | — | ❌ | `true` | Safety flag — always true, no writes are possible |

---

## Client config

### Cursor (`.cursor/mcp.json`)

```json
{
  "mcpServers": {
    "langfuse": {
      "url": "http://localhost:8080/sse"
    }
  }
}
```

### Claude Desktop (`claude_desktop_config.json`)

```json
{
  "mcpServers": {
    "langfuse": {
      "url": "http://localhost:8080/sse"
    }
  }
}
```

On macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`

### VS Code / GitHub Copilot

**URL mode** (if your client supports it):

```json
{
  "github.copilot.chat.mcp.servers": {
    "langfuse": {
      "url": "http://localhost:8080/sse"
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
      "args": ["-jar", "/path/to/langfuse-mcp-1.0.0.jar"],
      "env": {
        "LANGFUSE_PUBLIC_KEY": "pk-lf-...",
        "LANGFUSE_SECRET_KEY": "sk-lf-...",
        "LANGFUSE_HOST": "https://cloud.langfuse.com"
      }
    }
  }
}
```

---

## Docker

```bash
# Build image
docker build -t langfuse-mcp:latest .

# Run
docker run --rm -p 8080:8080 \
  -e LANGFUSE_PUBLIC_KEY=pk-lf-... \
  -e LANGFUSE_SECRET_KEY=sk-lf-... \
  -e LANGFUSE_HOST=https://cloud.langfuse.com \
  langfuse-mcp:latest
```

If Langfuse runs in Docker on the same host, use `host.docker.internal`:

```bash
-e LANGFUSE_HOST=http://host.docker.internal:3000
```

---

## Running tests

```bash
mvn test
```

Test coverage includes:

- `LangfusePropertiesBindingTest` — config binding from `application-test.yml` and constructor-level validation
- `RawJsonResponseSerializationTest` — Jackson serialization round-trips for `RawJsonBackedResponse`

---

## Package structure

```
com.langfuse.mcp
├── LangfuseMcpApplication.java          @SpringBootApplication @ConfigurationPropertiesScan
├── config/
│   ├── LangfuseProperties.java          @ConfigurationProperties — publicKey, secretKey, host, timeout, readOnly
│   ├── LangfuseClientConfig.java        RestClient bean with Basic-Auth and JdkClientHttpRequestFactory
│   └── JacksonConfig.java               Primary ObjectMapper (JSR310, ignore unknowns)
├── client/
│   └── LangfuseApiClient.java           GET-only HTTP gateway; typed exceptions; UriComponentsBuilder queries
├── exception/
│   ├── LangfuseApiException.java        Wraps HTTP errors — statusCode + endpoint on all error paths
│   └── ResourceNotFoundException.java   Thrown on HTTP 404
├── dto/
│   ├── common/    ApiResponse · PagedResponse · PaginationMeta
│   ├── request/   *FilterRequest · PromptGetRequest (9 filter classes + 1 get class)
│   └── response/  *Response (14 classes — JsonNode for open-schema fields)
├── service/       Interfaces (9): Trace · Observation · Session · Prompt · Dataset · Score · User · Comment · Schema
│   └── impl/      *ServiceImpl (9) — business logic, server-side filtering, error mapping
├── tools/         @McpTool classes (9) — param validation, delegation, agent-friendly descriptions
│   ├── TraceTools.java        (6 tools)
│   ├── ObservationTools.java  (2 tools)
│   ├── SessionTools.java      (3 tools)
│   ├── PromptTools.java       (2 tools)
│   ├── DatasetTools.java      (4 tools)
│   ├── ScoreTools.java        (4 tools)
│   ├── UserTools.java         (1 tool)
│   ├── CommentTools.java      (1 tool)
│   └── SchemaTools.java       (1 tool)
└── util/
    └── JsonPageMapper.java    Centralised JSON → PagedResponse mapper (no duplication)
```

---

## Troubleshooting

### `TRACE_FETCH_ERROR: HTTP/1.1 header parser received no bytes`

Connectivity issue — not a code bug. Check:

1. `LANGFUSE_HOST` points to a running Langfuse instance
2. The host is reachable from the JVM process
3. For Docker: use `host.docker.internal` instead of `localhost`
4. The HTTP/HTTPS scheme matches your server (`http://` vs `https://`)
5. Confirm the API is up: `curl $LANGFUSE_HOST/api/public/health`

### `INVALID_INPUT: <param> is required`

A required parameter was not provided by the agent. All `required = true` parameters are enforced at the tool layer before any HTTP call is made.

### Connection timeouts

Increase the timeout via the env var:

```bash
export LANGFUSE_TIMEOUT=60s
```

### Copilot / Claude can't see the server

1. Confirm the server is running: `curl http://localhost:8080/actuator/health`
2. Confirm the MCP SSE endpoint is alive: `curl http://localhost:8080/sse`
3. Check that the URL in the client config points to `http://localhost:8080/sse`

---

## License

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat&logo=opensourceinitiative&logoColor=white)](https://opensource.org/licenses/MIT)
