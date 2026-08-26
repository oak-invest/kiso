package com.oakinvest.kiso.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.types.MarkdownFileKind;
import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Base test class.
 */
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


    protected MarkdownFile createMarkdownFile(
            final Path temporaryDirectory,
            final String frontmatter
    ) throws IOException {
        Files.writeString(temporaryDirectory.resolve("concept.md"), """
                ---
                %s
                ---
                
                # Concept
                
                """.formatted(frontmatter));

        return KnowledgeBundleLoader.load(temporaryDirectory)
                .rootBundle()
                .markdownFiles()
                .getFirst();
    }

    protected MarkdownFile markdownFile(
            final Path markdownFilePath,
            final MarkdownFileKind kind,
            final Frontmatter frontmatter
    ) {
        return MarkdownFile.builder()
                .fileName(markdownFilePath.getFileName().toString())
                .kind(kind)
                .absolutePath(markdownFilePath)
                .relativePath(markdownFilePath)
                .frontmatter(frontmatter)
                .frontmatterPresent(frontmatter != null)
                .build();
    }

    protected Bundle bundleWith(final MarkdownFile markdownFile) {
        return bundleWith(List.of(markdownFile));
    }

    protected Bundle bundleWith(final List<MarkdownFile> markdownFiles) {
        return Bundle.builder()
                .childBundles(List.of())
                .markdownFiles(markdownFiles)
                .build();
    }

}
