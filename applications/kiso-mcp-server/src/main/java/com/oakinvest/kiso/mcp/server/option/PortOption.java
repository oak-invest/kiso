package com.oakinvest.kiso.mcp.server.option;

import lombok.Getter;
import lombok.experimental.Accessors;
import picocli.CommandLine;

/**
 * Port option for commands serving a bundle.
 */
public class PortOption {

    /** MCP server port option. */
    @Getter
    @Accessors(fluent = true)
    @CommandLine.Option(
            names = {"-p", "--port"},
            defaultValue = "8080",
            description = "Port used by the MCP server. Defaults to ${DEFAULT-VALUE}."
    )
    private int port;

}
