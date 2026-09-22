package com.oakinvest.kiso.mcp.server.option;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Allowed hosts option tests")
public class AllowedHostsOptionTest {

    @Test
    public void defaultsToNoAdditionalHosts() {
        final AllowedHostsOption option = new AllowedHostsOption();
        new CommandLine(option).parseArgs();
        assertThat(option.allowedHosts()).isEmpty();
    }

    @Test
    public void acceptsCommaSeparatedAndRepeatedHosts() {
        final AllowedHostsOption option = new AllowedHostsOption();
        new CommandLine(option).parseArgs(
                "--allowedHosts", "mcp.example.com,host.docker.internal:8080",
                "--allowed-hosts", "[2001:db8::1]:8080");
        assertThat(option.allowedHosts()).containsExactly(
                "mcp.example.com", "host.docker.internal:8080", "[2001:db8::1]:8080");
    }

    @Test
    public void requiresAValue() {
        final CommandLine commandLine = new CommandLine(new AllowedHostsOption());
        assertThatThrownBy(() -> commandLine.parseArgs("--allowedHosts"))
                .isInstanceOf(CommandLine.MissingParameterException.class);
    }

}
