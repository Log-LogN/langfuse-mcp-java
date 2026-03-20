FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Build the fat JAR first: mvn clean package -DskipTests
COPY target/langfuse-mcp-1.0.0.jar app.jar

# Expose the SSE/WebMVC port (transport: SSE on /sse, messages on /mcp/message)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# ─────────────────────────────────────────────────────────────────────────────
# Usage examples
# ─────────────────────────────────────────────────────────────────────────────
#
# Build image:
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
