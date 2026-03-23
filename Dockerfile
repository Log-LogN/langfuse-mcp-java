FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests package

FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=build /workspace/target/langfuse-mcp-*.jar /app/app.jar

# Expose the SSE/WebMVC port (transport: SSE on /sse, messages on /mcp/message)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# ─────────────────────────────────────────────────────────────────────────────
# Usage examples
# ─────────────────────────────────────────────────────────────────────────────
#
# Build image (compiles the jar inside Docker):
#   docker build -t langfuse-mcp:latest .
#
# Run (SSE mode, port 8080):
#   docker run --rm -p 8080:8080 \
#     -e LANGFUSE_PUBLIC_KEY=pk-lf-... \
#     -e LANGFUSE_SECRET_KEY=sk-lf-... \
#     -e LANGFUSE_HOST=https://cloud.langfuse.com \
#     langfuse-mcp:latest
#
# If Langfuse runs in Docker on the same host, use host.docker.internal:
#   -e LANGFUSE_HOST=http://host.docker.internal:3000
#
# Health check:
#   curl http://localhost:8080/actuator/health
#
# MCP Inspector:
#   npx @modelcontextprotocol/inspector http://localhost:8080/sse
