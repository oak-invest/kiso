package com.oakinvest.kiso.core.publisher;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("sitemap.xml generator")
class SitemapXmlGeneratorTest extends BaseTest {

    @Test
    @DisplayName("sitemap.xml generation")
    void generate() throws Exception {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        var content = SitemapXmlGenerator.generate(knowledgeBundle);
        var document = parseXml(content);

        // Testing content =============================================================================================
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
    @DisplayName("Generating sitemap.xml with a base URL")
    void generateWithBaseUrl() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        var siteConfiguration = new SiteConfiguration(
                "https://knowledge.angara.finance/",
                Locale.FRENCH,
                "Site name",
                "Knowledge",
                "Description");
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath, siteConfiguration);
        var content = SitemapXmlGenerator.generate(knowledgeBundle);

        // Testing content =============================================================================================
        assertThat(content)
                .contains("<loc>https://knowledge.angara.finance/index.html</loc>")
                .contains("<loc>https://knowledge.angara.finance/datasets/index.html</loc>");
    }

}
