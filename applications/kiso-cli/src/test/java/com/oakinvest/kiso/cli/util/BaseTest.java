package com.oakinvest.kiso.cli.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseTest {

    /** Google example knowledge base. */
    public static final String KB_GOOGLE = "kb-google-example-v0.1";

    /** Google example knowledge base with configuration. */
    public static final String KB_GOOGLE_WITH_CONFIGURATION = "kb-google-example-v0.1-with-configuration";

    /**
     * Retrieves absolutePath from a resource fileName.
     *
     * @param resourceName resource fileName
     * @return absolutePath
     */
    @SuppressWarnings("SameParameterValue")
    protected Path getResourcePath(final String resourceName) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Missing test resource: " + resourceName);
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
