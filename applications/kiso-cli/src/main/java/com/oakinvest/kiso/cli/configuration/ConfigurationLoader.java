package com.oakinvest.kiso.cli.configuration;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.experimental.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

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
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder(YAMLFactory.builder()
                    .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
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

        // We get the content ==========================================================================================
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
        if (profileName.isBlank()) {
            throw new ConfigurationLoadingException("Profile name must not be blank");
        }

        // We treath the profile name ==================================================================================
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

        // We get the content ==========================================================================================
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
            String yaml = Files.readString(configurationFilePath);
            if (yaml.isBlank()) {
                return Optional.of(Configuration.empty());
            }

            Configuration configuration = OBJECT_MAPPER.readValue(yaml, Configuration.class);
            if (configuration == null) {
                configuration = Configuration.empty();
            }
            return Optional.of(configuration);
        } catch (Exception exception) {
            throw new ConfigurationLoadingException(
                    "Unable to load configuration file " + configurationFilePath + ": " + exception.getMessage(),
                    exception);
        }
    }

}
