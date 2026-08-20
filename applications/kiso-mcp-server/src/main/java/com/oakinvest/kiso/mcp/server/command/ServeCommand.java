package com.oakinvest.kiso.mcp.server.command;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.mcp.server.options.PortOption;
import com.oakinvest.kiso.mcp.server.options.SourceOption;
import com.oakinvest.kiso.mcp.server.service.KnowledgeService;
import dev.tachyonmcp.api.server.features.tools.ToolResult;
import dev.tachyonmcp.api.server.features.tools.Tools;
import dev.tachyonmcp.core.server.TachyonServer;
import picocli.CommandLine;

import java.util.Map;

/**
 * Starts the Kiso MCP server.
 */
public class ServeCommand implements Runnable {

    /** Source directory. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** MCP server port. */
    @CommandLine.Mixin
    private final PortOption portOption = new PortOption();

    /**
     * Registers the knowledge tools.
     *
     * @param tools            tool registry
     * @param knowledgeService knowledge service
     */
    static void registerTools(final Tools tools, final KnowledgeService knowledgeService) {
        // Search tool: searches concepts in the knowledge bundle ======================================================
        tools.register(
                // Builder for the search tool
                builder -> builder.name("search")
                        .description("Searches concepts in the knowledge bundle.")
                        .inputSchema("""
                                {
                                  "type": "object",
                                  "properties": {
                                    "text": {"type": "string", "minLength": 1}
                                  },
                                  "required": ["text"],
                                  "additionalProperties": false
                                }
                                """),
                // Handler for the search tool
                (context, request) -> {
                    final String text = request.arguments().stringOpt("text").orElseThrow();
                    return ToolResult.structured(Map.of("results", knowledgeService.search(text)));
                });

        // Get concept content tool: returns the Markdown content of a concept =========================================
        tools.register(
                // Builder for the get concept content tool
                builder -> builder.name("get_concept_content")
                        .description("Returns the Markdown content of a concept.")
                        .inputSchema("""
                                {
                                  "type": "object",
                                  "properties": {
                                    "conceptId": {"type": "string", "minLength": 1}
                                  },
                                  "required": ["conceptId"],
                                  "additionalProperties": false
                                }
                                """),
                // Handler for the get concept content tool
                (context, request) -> {
                    final String conceptId = request.arguments().stringOpt("conceptId").orElseThrow();
                    return knowledgeService.getConceptContent(conceptId)
                            .<ToolResult>map(ToolResult::text)
                            .orElseGet(() -> ToolResult.error("Unknown concept: " + conceptId));
                });
    }

    @Override
    @SuppressWarnings("resource")
    public final void run() {
        final KnowledgeService knowledgeService = new KnowledgeService(KnowledgeBundleLoader.load(sourceOption.sourceDirectory()));
        final TachyonServer server = TachyonServer.builder()
                .name("kiso-mcp-server")
                .session(session -> session.enabled(false))
                .withTools(tools -> registerTools(tools, knowledgeService))
                .port(portOption.port())
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close, "kiso-mcp-server-shutdown"));
    }

}
