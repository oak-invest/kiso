package com.oakinvest.kiso.mcp.server.option;

import lombok.Getter;
import lombok.experimental.Accessors;
import picocli.CommandLine;

/**
 * Host option for commands serving a bundle.
 */
public class HostOption {

    /** MCP server host option. */
    @Getter
    @Accessors(fluent = true)
    @CommandLine.Option(
            names = {"-H", "--host"},
            defaultValue = "127.0.0.1",
            paramLabel = "ADDRESS",
            description = "Address used by the MCP server. Defaults to ${DEFAULT-VALUE}."
    )
    private String host;

}
