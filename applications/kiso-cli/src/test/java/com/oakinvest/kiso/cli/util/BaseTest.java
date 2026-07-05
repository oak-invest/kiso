package com.oakinvest.kiso.cli.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseTest {

    /** Knowledge base Google example directory. */
    protected static final String KB_GOOGLE_EXAMPLE_DIRECTORY = "kb-google-example-v0.1";

    /**
     * Retrieves absolutePath from a resource fileName.
     *
     * @param resourceName resource fileName
     * @return absolutePath
     * @throws URISyntaxException syntax problem with URI
     */
    @SuppressWarnings("SameParameterValue")
    protected Path getResourcePath(final String resourceName) throws URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Missing test resource: " + resourceName);
        return Path.of(resource.toURI());
    }

}
