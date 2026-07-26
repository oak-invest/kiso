package com.oakinvest.kiso.cli.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static com.fasterxml.jackson.core.StreamReadFeature.STRICT_DUPLICATE_DETECTION;
import static com.oakinvest.kiso.core.util.FileConstants.CONFIGURATION_DIRECTORY;
import static com.oakinvest.kiso.core.util.FileConstants.CONFIGURATION_FILE;
import static com.oakinvest.kiso.core.util.FileConstants.CONFIGURATION_FILE_NAME;

/**
 * Kiso-cli configuration loader.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class ConfigurationLoader {

    /** Mapper. */
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder(
                    YAMLFactory.builder()
                            .enable(STRICT_DUPLICATE_DETECTION)
                            .build())
            .build();

    /**
     * Loads Kiso-cli configuration from the given bundle path.
     *
     * @param bundlePath bundle path
     * @return Kiso-cli configuration
     */
    public static Optional<Configuration> load(final Path bundlePath) {
        // We check if we have the file ================================================================================
        Objects.requireNonNull(bundlePath, "Bundle path must not be null");
        final Path configurationFilePath = bundlePath.resolve(CONFIGURATION_FILE);
        if (Files.notExists(configurationFilePath)) {
            return Optional.empty();
        }
        if (!Files.isRegularFile(configurationFilePath)) {
            throw new ConfigurationLoadingException("Configuration path is not a regular file: " + configurationFilePath);
        }

        // We get the configuration ====================================================================================
        return getConfiguration(configurationFilePath);
    }

    /**
     * Loads Kiso-cli configuration from the given bundle path and profile.
     *
     * @param bundlePath  bundle path
     * @param profileName profile name
     * @return Kiso-cli configuration
     */
    public static Optional<Configuration> load(final Path bundlePath, final String profileName) {
        // We check if we have the bundle ==============================================================================
        Objects.requireNonNull(bundlePath, "Bundle path must not be null");
        if (StringUtils.isBlank(profileName)) {
            // If profile name is blank, we load the default configuration file.
            return load(bundlePath);
        }

        // We treat the profile name ===================================================================================
        final Path configurationDirectory = bundlePath.resolve(CONFIGURATION_DIRECTORY).normalize();
        final Path profileDirectory = configurationDirectory.resolve(profileName).normalize();
        final Path profileFilePath = profileDirectory.resolve(CONFIGURATION_FILE_NAME);
        if (!configurationDirectory.equals(profileDirectory.getParent())) {
            throw new ConfigurationLoadingException("Invalid profile name: " + profileName);
        }
        if (Files.notExists(profileFilePath)) {
            throw new ConfigurationLoadingException("Profile does not exist: " + profileFilePath);
        }
        if (!Files.isRegularFile(profileFilePath)) {
            throw new ConfigurationLoadingException("Profile path is not a regular file: " + profileFilePath);
        }

        // We get the configuration ====================================================================================
        return getConfiguration(profileFilePath);
    }

    /**
     * Retrieve configuration.
     *
     * @param configurationFilePath configuration file path
     * @return configuration
     */
    private static Optional<Configuration> getConfiguration(final Path configurationFilePath) {
        try {
            // Loading the content.
            final String yaml = Files.readString(configurationFilePath);

            // If there is some content in the configuration file, we load it.
            if (StringUtils.isNotBlank(yaml)) {
                return Optional.of(OBJECT_MAPPER.readValue(yaml, Configuration.class));
            }

            return Optional.of(Configuration.empty());
        } catch (Exception exception) {
            throw new ConfigurationLoadingException(
                    "Unable to load configuration file " + configurationFilePath + ": " + exception.getMessage(),
                    exception);
        }
    }

}
