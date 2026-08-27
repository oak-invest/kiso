package com.oakinvest.kiso.cli.v0_2.configuration;

import com.oakinvest.kiso.cli.configuration.ConfigurationLoader;
import com.oakinvest.kiso.cli.exception.ConfigurationLoadingException;
import com.oakinvest.kiso.cli.util.BaseTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("vO.2 - Configuration loader")
public class ConfigurationLoaderTest extends BaseTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    @DisplayName("Test loading Kiso-cli configuration from a bundle path")
    public void testLoadConfiguration() {
        Assertions.assertThat(ConfigurationLoader.load(getResourcePath("kb-configuration-valid")))
                .isNotEmpty().get()
                .satisfies(configuration -> {
                    // Site ============================================================================================
                    assertThat(configuration.site().baseUrl()).isEqualTo("https://knowledge.angara.finance");
                    assertThat(configuration.site().normalizedBaseUrl()).isEqualTo("https://knowledge.angara.finance/");
                    assertThat(configuration.site().language()).isEqualTo(Locale.FRENCH);
                    assertThat(configuration.site().name()).isEqualTo("Knowledge Base");
                    assertThat(configuration.site().title()).isEqualTo("My Knowledge Base");
                    assertThat(configuration.site().description()).isEqualTo("My knowledge base description");

                    // Theme ===========================================================================================
                    assertThat(configuration.theme().name()).isEqualTo("corPorate");
                    assertThat(configuration.theme().effectiveName()).isEqualTo("corporate");

                    // Content =========================================================================================
                    assertThat(configuration.content().ignorePatterns())
                            .containsExactly("drafts/**", "internal/**");
                });
    }

    @Test
    @DisplayName("Test loading empty Kiso-cli configuration from a bundle path")
    public void testLoadEmptyConfiguration() {
        assertThat(ConfigurationLoader.load(getResourcePath("kb-configuration-empty")))
                .isNotEmpty().get()
                .satisfies(configuration -> {
                    assertThat(configuration.site()).isNotNull();
                    assertThat(configuration.site().baseUrl()).isNull();
                    assertThat(configuration.site().normalizedBaseUrl()).isEmpty();
                    assertThat(configuration.theme()).isNotNull();
                    assertThat(configuration.content()).isNotNull();
                    assertThat(configuration.content().ignorePatterns()).isEmpty();
                });
    }

    @Test
    @DisplayName("Test loading Kiso-cli configuration from a bundle path when configuration file does not exist")
    public void testLoadConfigurationFileNotExist() {
        assertThat(ConfigurationLoader.load(getResourcePath("kb-configuration-absent"))).isEmpty();
    }

    @Test
    @DisplayName("Load a partial Kiso-cli configuration with safe defaults")
    public void testLoadPartialConfiguration() throws IOException {
        writeConfiguration("""
                site:
                  title: Example
                """);

        assertThat(ConfigurationLoader.load(temporaryDirectory))
                .isNotEmpty().get()
                .satisfies(configuration -> {
                    assertThat(configuration.site()).isNotNull();
                    assertThat(configuration.site().title()).isEqualTo("Example");
                    assertThat(configuration.theme()).isNotNull();
                    assertThat(configuration.content()).isNotNull();
                    assertThat(configuration.content().ignorePatterns()).isEmpty();
                });
    }

    @Test
    @DisplayName("Reject malformed Kiso-cli configuration")
    public void testRejectMalformedConfiguration() throws IOException {
        Path configurationFile = writeConfiguration("site: [invalid]");

        assertThatThrownBy(() -> ConfigurationLoader.load(temporaryDirectory))
                .isInstanceOf(ConfigurationLoadingException.class)
                .hasMessageContaining(configurationFile.toString());
    }

    @Test
    @DisplayName("Reject unknown Kiso-cli configuration property")
    public void testRejectUnknownConfigurationProperty() throws IOException {
        writeConfiguration("unknownProperty: value");

        assertThatThrownBy(() -> ConfigurationLoader.load(temporaryDirectory))
                .isInstanceOf(ConfigurationLoadingException.class)
                .hasMessageContaining("unknownProperty");
    }

    @Test
    @DisplayName("Reject duplicate Kiso-cli configuration property")
    public void testRejectDuplicateConfigurationProperty() throws IOException {
        writeConfiguration("""
                theme:
                  name: light
                  name: dark
                """);

        assertThatThrownBy(() -> ConfigurationLoader.load(temporaryDirectory))
                .isInstanceOf(ConfigurationLoadingException.class)
                .hasMessageContaining("Duplicate field");
    }

    /**
     * Write configuration.
     *
     * @param content content
     * @return Path
     * @throws IOException exception
     */
    private Path writeConfiguration(final String content) throws IOException {
        Path configurationDirectory = Files.createDirectories(temporaryDirectory.resolve(".kiso"));
        return Files.writeString(configurationDirectory.resolve("configuration.yaml"), content);
    }

}
