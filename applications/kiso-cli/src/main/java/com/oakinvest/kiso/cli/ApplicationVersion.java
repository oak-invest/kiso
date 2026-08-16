package com.oakinvest.kiso.cli;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class used to retrieve the application version.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public final class ApplicationVersion {

    /** Version file. */
    private static final String VERSION_FILE = "/version.properties";

    /** Version property. */
    private static final String VERSION_PROPERTY = "version";

    /** Unknown version constant. */
    private static final String UNKNOWN_VERSION = "unknown";

    /** Application version. */
    private static final String VERSION = loadVersion();

    /**
     * Returns the application version.
     *
     * @return the application version
     */
    public static String get() {
        return VERSION;
    }

    private static String loadVersion() {
        try (InputStream inputStream = ApplicationVersion.class.getResourceAsStream(VERSION_FILE)) {
            // No file found.
            if (inputStream == null) {
                return UNKNOWN_VERSION;
            }

            // Getting the version from the file.
            Properties properties = new Properties();
            properties.load(inputStream);
            String version = properties.getProperty(VERSION_PROPERTY);

            // The version is not set.
            if (StringUtils.isBlank(version)) {
                return UNKNOWN_VERSION;
            }

            // Valid version.
            return version;
        } catch (IOException exception) {
            return UNKNOWN_VERSION;
        }
    }

}
