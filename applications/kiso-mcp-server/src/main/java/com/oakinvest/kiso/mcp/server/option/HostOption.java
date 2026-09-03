package com.oakinvest.kiso.mcp.server.option;

import picocli.CommandLine;

/**
 * Host option for commands serving a bundle.
 */
public class HostOption {

    /** MCP server host option. */
    @CommandLine.Option(
            names = {"-H", "--host"},
            defaultValue = "127.0.0.1",
            paramLabel = "ADDRESS",
            description = "Address used by the MCP server. Defaults to ${DEFAULT-VALUE}."
    )
    @SuppressWarnings("unused")
    private String host;

    /**
     * Address used by the MCP server.
     *
     * @return MCP server host
     */
    public String host() {
        return host;
    }

}
