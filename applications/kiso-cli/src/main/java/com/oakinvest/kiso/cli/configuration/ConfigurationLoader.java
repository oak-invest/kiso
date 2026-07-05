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

import static com.oakinvest.kiso.core.util.FileConstants.CONFIGURATION_FILE;

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
