package com.oakinvest.kiso.mcp.server.option;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Host option tests")
public class HostOptionTest {

    @Test
    @DisplayName("Defaults to the loopback address")
    public void defaultHost() {
        final HostOption hostOption = new HostOption();

        new CommandLine(hostOption).parseArgs();

        assertThat(hostOption.host()).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("Accepts a custom bind address")
    public void customHost() {
        final HostOption hostOption = new HostOption();

        new CommandLine(hostOption).parseArgs("--host", "0.0.0.0");

        assertThat(hostOption.host()).isEqualTo("0.0.0.0");
    }

}
