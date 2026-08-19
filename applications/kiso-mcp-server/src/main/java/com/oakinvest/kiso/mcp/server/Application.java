package com.oakinvest.kiso.mcp.server;

import dev.tachyonmcp.api.server.features.tools.ToolResult;
import dev.tachyonmcp.core.server.TachyonServer;

/**
 * The main entry point for the application.
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Application {

    /** Default port for the server. */
    private static final int DEFAULT_PORT = 8080;

    /**
     * The main entry point for the application.
     *
     * @param args the command line arguments
     */
    public static void main(final String... args) {
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
                .port(DEFAULT_PORT)
                .build();
        server.start();
    }

}
