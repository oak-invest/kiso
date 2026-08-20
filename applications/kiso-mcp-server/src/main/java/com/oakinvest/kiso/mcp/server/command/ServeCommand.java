package com.oakinvest.kiso.mcp.server.command;

import com.oakinvest.kiso.mcp.server.options.PortOption;
import com.oakinvest.kiso.mcp.server.options.SourceOption;
import dev.tachyonmcp.api.server.features.tools.ToolResult;
import dev.tachyonmcp.core.server.TachyonServer;
import picocli.CommandLine;

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

    @Override
    @SuppressWarnings("resource")
    public final void run() {
        var server = TachyonServer.builder()
                .name("weather-mcp")
                .session(session -> session.enabled(false))
                .withTools(tools -> tools.register(
                        b -> b.name("get_forecast")
                                .description("Get weather forecast")
                                .inputSchema("""
                                        {"type":"object","properties":{"city":{"type":"string"}},"required":["city"]}
                                        """),
                        (ctx, request) -> ToolResult.text("☀️ 22°C")))
                .port(portOption.port())
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close, "kiso-mcp-server-shutdown"));
    }

}
