package com.oakinvest.kiso.mcp.server.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oakinvest.kiso.mcp.server.option.AllowedHostsOption;
import com.oakinvest.kiso.mcp.server.service.KnowledgeService;
import com.oakinvest.kiso.mcp.server.util.BaseTest;
import dev.tachyonmcp.api.server.features.tools.ToolDescriptor;
import dev.tachyonmcp.core.server.TachyonServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MCP server tests")
public class MCPServerTest extends BaseTest {

    /** JSON object mapper. */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** HTTP client. */
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /** MCP server. */
    private TachyonServer server;

    /** Knowledge service. */
    private KnowledgeService knowledgeService;

    @BeforeEach
    public void setUp() {
        final var resourcePath = getResourcePath(KB_ACME_V_0_2);
        knowledgeService = new KnowledgeService(resourcePath);
        final AllowedHostsOption allowedHostsOption = new AllowedHostsOption();
        new CommandLine(allowedHostsOption).parseArgs("--allowedHosts", "mcp.example.com");
        server = TachyonServer.builder()
                .name("test-server")
                .stateless()
                .withTools(tools -> ServeCommand.registerTools(tools, knowledgeService))
                .port(0)
                .network(network -> network.allowedHosts(allowedHostsOption.allowedHosts().toArray(String[]::new)))
                .build();
        server.start();
    }

    @AfterEach
    public void tearDown() {
        try {
            server.close();
        } finally {
            knowledgeService.close();
        }
    }

    @Test
    @DisplayName("Registers knowledge tools")
    public void registerKnowledgeTools() {
        // The server should have two tools registered: "get_concept_content" and "searchConcepts" ======================
        assertThat(server.tools().descriptors())
                .extracting(ToolDescriptor::name)
                .containsExactlyInAnyOrder("search_concepts", "get_concept_content");

        // searchConcepts() tool input schema should contain "text" property ============================================
        assertThat(server.tools().find("search_concepts")).isPresent()
                .hasValueSatisfying(descriptor -> {
                    final var inputSchema = descriptor.inputSchema();
                    assertThat(inputSchema).isNotNull();
                    assertThat(inputSchema.json()).isNotNull();
                    assertThat(inputSchema.json()).contains("text");
                });

        // get_concept_content() tool input schema should contain "conceptId" property =================================
        assertThat(server.tools().find("get_concept_content")).isPresent()
                .hasValueSatisfying(descriptor -> {
                    final var inputSchema = descriptor.inputSchema();
                    assertThat(inputSchema).isNotNull();
                    assertThat(inputSchema.json()).isNotNull();
                    assertThat(inputSchema.json()).contains("conceptId");
                });
    }

    @Test
    @DisplayName("Calls search_concepts() through MCP")
    public void searchConcepts() throws IOException, InterruptedException {
        final var response = callTool(
                "search_concepts",
                Map.of("text", "discount_amount")
        );

        assertThat(response.at("/result/structuredContent/results").isArray()).isTrue();
        assertThat(response.at("/result/structuredContent/results/0/conceptId").asText()).isEqualTo("tables/orders");
        assertThat(response.at("/result/structuredContent/results/1/conceptId").asText()).isEqualTo("policies/revenue-recognition");
    }

    @Test
    @DisplayName("Calls get_concept_content() through MCP for a known concept")
    public void getConceptContent() throws IOException, InterruptedException {
        final var response = callTool(
                "get_concept_content",
                Map.of("conceptId", "computations/revenue-ytd")
        );

        assertThat(response.at("/result/content/0/type").asText()).isEqualTo("text");
        assertThat(response.at("/result/content/0/text").asText()).contains("# Computation", "# Freshness");
    }

    @Test
    @DisplayName("Calls getConceptContent through MCP for an unknown concept")
    public void getUnknownConceptContent() throws IOException, InterruptedException {
        final JsonNode response = callTool(
                "get_concept_content",
                Map.of("conceptId", "unknown-concept")
        );

        assertThat(response.at("/result/isError").asBoolean()).isTrue();
        assertThat(response.at("/result/content/0/text").asText()).isEqualTo("Unknown concept: unknown-concept");
    }

    @Test
    @DisplayName("Passes allowed hosts from command-line options to Tachyon")
    public void acceptsConfiguredHost() throws IOException {
        assertThat(callWithHost("mcp.example.com")).contains("200 OK");
    }

    @Test
    @DisplayName("Rejects hosts absent from command-line options")
    public void rejectsUnconfiguredHost() throws IOException {
        assertThat(callWithHost("untrusted.example.com")).contains("403 Forbidden");
    }

    /** Sends a raw HTTP request so the Host header can be set explicitly. */
    private String callWithHost(final String host) throws IOException {
        final String body = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"tools/list\"}";
        try (Socket socket = new Socket("127.0.0.1", server.port())) {
            socket.setSoTimeout(5000);
            final String request = "POST /mcp HTTP/1.1\r\n"
                    + "Host: " + host + "\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Accept: application/json, text/event-stream\r\n"
                    + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                    + "Connection: close\r\n\r\n" + body;
            socket.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().flush();
            return new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.UTF_8)).readLine();
        }
    }

    /**
     * Calls an MCP tool through the HTTP transport.
     *
     * @param toolName  tool name
     * @param arguments tool arguments
     * @return JSON-RPC response
     * @throws IOException          if the request cannot be sent or decoded
     * @throws InterruptedException if the request is interrupted
     */
    private JsonNode callTool(
            final String toolName,
            final Map<String, String> arguments
    ) throws IOException, InterruptedException {
        final String requestBody = objectMapper.writeValueAsString(Map.of(
                "jsonrpc", "2.0",
                "id", 1,
                "method", "tools/call",
                "params", Map.of(
                        "name", toolName,
                        "arguments", arguments
                )
        ));
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + server.port() + "/mcp"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/event-stream")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        final HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(response.statusCode()).isEqualTo(200);
        return objectMapper.readTree(response.body());
    }

}
