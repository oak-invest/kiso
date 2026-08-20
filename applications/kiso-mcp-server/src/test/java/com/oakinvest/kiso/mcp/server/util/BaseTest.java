package com.oakinvest.kiso.mcp.server.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseTest {

    /** Google example knowledge base v0.1. */
    public static final String KB_GOOGLE_V_0_1 = "kb-google-example-v0.1";

    /** Google example knowledge base with configuration v0.1. */
    public static final String KB_GOOGLE_V_0_1_WITH_CONFIGURATION = "kb-google-example-v0.1-with-configuration";

    /** Google example knowledge base v0.2. */
    public static final String KB_GOOGLE_V_0_2 = "kb-google-example-v0.2";

    /** Google example knowledge base with configuration v0.2. */
    public static final String KB_GOOGLE_V_0_2_WITH_CONFIGURATION = "kb-google-example-v0.2-with-configuration";

    /** Acme example knowledge base v0.2. */
    public static final String KB_ACME_V_0_2 = "kb-acme-example-v0.2";

    /**
     * Retrieves absolutePath from a resource fileName.
     *
     * @param resourceName resource fileName
     * @return absolutePath
     */
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
