package com.oakinvest.kiso.mcp.server.option;

import lombok.Getter;
import lombok.experimental.Accessors;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Additional hosts accepted by Tachyon's DNS-rebinding protection.
 */
public class AllowedHostsOption {

    /** Additional HTTP Host authorities, without URL schemes or paths. Empty preserves Tachyon's localhost-only default. */
    @Getter
    @Accessors(fluent = true)
    @CommandLine.Option(
            names = {"--allowedHosts", "--allowed-hosts"},
            split = ",",
            paramLabel = "HOST[:PORT]",
            description = "Additional allowed HTTP hosts, separated by commas. May be repeated. Localhost is always allowed."
    )
    private final List<String> allowedHosts = new ArrayList<>();

}
