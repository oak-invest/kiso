package com.oakinvest.kiso.cli.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class BaseTest {

    /** Google example knowledge base v0.1. */
    public static final String KB_GOOGLE_V_0_1 = "v0.1/kb-google-example";

    /** Google example knowledge base with configuration v0.1. */
    public static final String KB_GOOGLE_V_0_1_WITH_CONFIGURATION = "v0.1/kb-google-example-with-configuration";

    /** Google example knowledge base v0.2. */
    public static final String KB_GOOGLE_V_0_2 = "v0.2/kb-google-example";

    /** Google example knowledge base with configuration v0.2. */
    public static final String KB_GOOGLE_V_0_2_WITH_CONFIGURATION = "v0.2/kb-google-example-with-configuration";

    /** Acme example knowledge base v0.2. */
    public static final String KB_ACME_V_0_2 = "v0.2/kb-acme-example";

    /**
     * Returns the document ids from search documents.
     *
     * @param documents search documents JSON array
     * @return document ids
     */
    protected static java.util.List<String> documentIds(final JsonNode documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(document -> document.get("id").asText())
                .toList();
    }

    /**
     * Finds a document by id.
     *
     * @param documents search documents JSON array
     * @param id        document id
     * @return matching document
     */
    protected static JsonNode documentWithId(final JsonNode documents, final String id) {
        for (JsonNode document : documents) {
            if (document.get("id").asText().equals(id)) {
                return document;
            }
        }
        fail("Missing search document: " + id);
        return null;
    }

    protected static void assertElementText(Element element, String expectedText) {
        assertThat(element).isNotNull();
        assertThat(element.text()).isEqualTo(expectedText);
    }

    protected static void assertElementClassName(Element element, String expectedClassName) {
        assertThat(element).isNotNull();
        assertThat(element.className()).isEqualTo(expectedClassName);
    }

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

    /**
     * Parses XML content.
     *
     * @param content XML content
     * @return parsed document
     * @throws Exception if XML parsing fails
     */
    protected Document parseXml(final String content) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setExpandEntityReferences(false);
        return documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(content)));
    }

}
