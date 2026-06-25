package com.oakinvest.kiso.core.publishing;

import com.oakinvest.kiso.core.loading.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class SitemapXmlGeneratorTest extends BaseTest {

    @Test
    @DisplayName("Generating sitemap.xml content")
    void generate() throws Exception {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        String content = SitemapXmlGenerator.generate(knowledgeBundle);
        Document document = parseXml(content);
        // Testing structure ==========================================================================================
        assertThat(content)
                .startsWith("""
                        <?xml version="1.0" encoding="UTF-8"?>
                        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                        """.stripIndent())
                .contains("<lastmod>2026-05-28T22:49:59Z</lastmod>")
                .contains("<lastmod>2026-05-28T22:51:46Z</lastmod>")
                .contains("<lastmod>2026-05-28T22:53:05Z</lastmod>")
                .endsWith("</urlset>\n");
        assertThat(document.getDocumentElement().getNodeName()).isEqualTo("urlset");
        assertThat(document.getElementsByTagName("url").getLength()).isEqualTo(17);
        var locations = document.getElementsByTagName("loc");
        assertThat(locations.getLength()).isEqualTo(17);
        assertThat(locations.item(0).getTextContent()).isEqualTo("index.html");
        assertThat(locations.item(1).getTextContent()).isEqualTo("datasets/index.html");
        assertThat(locations.item(2).getTextContent()).isEqualTo("datasets/ga4_obfuscated_sample_ecommerce.html");
        assertThat(locations.item(4).getTextContent()).isEqualTo("references/joins/index.html");
        assertThat(locations.item(5).getTextContent()).isEqualTo("references/joins/events___ads_clickstats.html");
        assertThat(locations.item(15).getTextContent()).isEqualTo("tables/index.html");
        assertThat(locations.item(16).getTextContent()).isEqualTo("tables/events_.html");
    }

    @Test
    @DisplayName("Rejecting null knowledge bundle")
    void rejectNullKnowledgeBundle() {
        assertThatNullPointerException()
                .isThrownBy(() -> SitemapXmlGenerator.generate(null))
                .withMessage("knowledgeBundle must not be null");
    }

    /**
     * Parses XML content.
     *
     * @param content XML content
     * @return parsed document
     * @throws Exception if XML parsing fails
     */
    private Document parseXml(final String content) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setExpandEntityReferences(false);
        return documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(content)));
    }

}
