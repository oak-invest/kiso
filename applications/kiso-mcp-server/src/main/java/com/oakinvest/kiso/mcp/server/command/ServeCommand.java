package com.oakinvest.kiso.mcp.server.command;

import com.oakinvest.kiso.core.exception.KnowledgeBundleLoadingException;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.mcp.server.ApplicationVersion;
import com.oakinvest.kiso.mcp.server.option.PortOption;
import com.oakinvest.kiso.mcp.server.option.SourceOption;
import com.oakinvest.kiso.mcp.server.service.KnowledgeService;
import dev.tachyonmcp.api.server.features.tools.ToolResult;
import dev.tachyonmcp.api.server.features.tools.Tools;
import dev.tachyonmcp.core.server.TachyonServer;
import picocli.CommandLine;

import java.io.File;
import java.util.Map;

/**
 * Starts the Kiso MCP server.
 */
public class ServeCommand extends AbstractCommand implements Runnable {

    /** Source directory. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** MCP server port. */
    @CommandLine.Mixin
    private final PortOption portOption = new PortOption();

    /** Command specification. */
    @SuppressWarnings("unused")
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    /**
     * Registers the knowledge tools.
     *
     * @param tools            tool registry
     * @param knowledgeService knowledge service
     */
    static void registerTools(final Tools tools, final KnowledgeService knowledgeService) {
        // Search tool: searches concepts in the knowledge bundle ======================================================
        tools.register(
                // Builder for the searchConcept tool
                builder -> builder.name("searchConcept")
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
                // Handler for the searchConcept tool
                (context, request) -> {
                    final String text = request.arguments().stringOpt("text").orElseThrow();
                    return ToolResult.structured(Map.of("results", knowledgeService.searchConcept(text)));
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
                            .map(ToolResult::text)
                            .orElseGet(() -> ToolResult.error("Unknown concept: " + conceptId));
                });
    }

    /**
     * Get the command specification.
     *
     * @return command specification
     */
    @Override
    protected CommandLine.Model.CommandSpec commandSpec() {
        return commandSpec;
    }

    @Override
    @SuppressWarnings("resource")
    public final void run() {
        // Displaying information about the process ====================================================================
        final File sourceDirectory = sourceOption.sourceDirectory().toFile();
        print("Kiso-mcp-server " + ApplicationVersion.get() + " - Running on port " + portOption.port());
        print("Loading knowledge bundle in " + sourceDirectory.getAbsolutePath());

        try {
            // Creating the knowledge service and loading the knowledge bundle =========================================
            final KnowledgeService knowledgeService = new KnowledgeService(KnowledgeBundleLoader.load(sourceDirectory.toPath()));
            print("Knowledge bundle loaded with " + knowledgeService.getConceptCount() + " concepts.");
            blankLine();

            // Starting the server =====================================================================================
            final TachyonServer server = TachyonServer.builder()
                    .name("kiso-mcp-server")
                    .withTools(tools -> registerTools(tools, knowledgeService))
                    .port(portOption.port())
                    .build();
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(server::close, "kiso-mcp-server-shutdown"));
        } catch (KnowledgeBundleLoadingException exception) {
            printError("Failed to load knowledge bundle: " + exception.getMessage());
        }
    }

}
