package com.oakinvest.kiso.mcp.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Application version")
public class ApplicationVersionTest {

    @Test
    @DisplayName("Load application version from version properties")
    public void testLoadApplicationVersionFromVersionProperties() {
        assertThat(ApplicationVersion.get())
                .isNotBlank()
                .isNotEqualTo("unknown")
                .isNotEqualTo("@project.version@");
    }

}
