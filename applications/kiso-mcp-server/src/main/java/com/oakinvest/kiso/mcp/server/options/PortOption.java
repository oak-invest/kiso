package com.oakinvest.kiso.mcp.server.options;

import picocli.CommandLine;

/**
 * Port option for commands serving a bundle.
 */
public class PortOption {

    /** MCP server port option. */
    @CommandLine.Option(
            names = {"-p", "--port"},
            defaultValue = "8080",
            description = "Port used by the MCP server. Defaults to ${DEFAULT-VALUE}."
    )
    @SuppressWarnings("unused")
    private int port;

    /**
     * Port used by the MCP server.
     *
     * @return MCP server port
     */
    public int port() {
        return port;
    }

}
