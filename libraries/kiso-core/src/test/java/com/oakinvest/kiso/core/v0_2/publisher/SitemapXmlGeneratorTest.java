package com.oakinvest.kiso.core.v0_2.publisher;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.publisher.SitemapXmlGenerator;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - sitemap.xml generator")
class SitemapXmlGeneratorTest extends BaseTest {

    @Test
    @DisplayName("sitemap.xml generation")
    void generate() throws Exception {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        var content = SitemapXmlGenerator.generate(knowledgeBundle);
        var document = parseXml(content);

        // Testing content =============================================================================================
        assertThat(content)
                .startsWith("""
                        <?xml version="1.0" encoding="UTF-8"?>
                        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                        """.stripIndent())
                // TODO Uncomment this when the generated.at field is correctly managed.
//                .contains("<lastmod>2026-05-28T22:49:59Z</lastmod>")
//                .contains("<lastmod>2026-05-28T22:51:46Z</lastmod>")
//                .contains("<lastmod>2026-05-28T22:53:05Z</lastmod>")
                .endsWith("</urlset>\n");
        assertThat(document.getDocumentElement().getNodeName()).isEqualTo("urlset");
        assertThat(document.getElementsByTagName("url").getLength()).isEqualTo(14);

        var locations = document.getElementsByTagName("loc");
        assertThat(locations.getLength()).isEqualTo(14);
        assertThat(locations.item(0).getTextContent()).isEqualTo("index.html");
        // datasets.
        assertThat(locations.item(1).getTextContent()).isEqualTo("datasets/index.html");
        assertThat(locations.item(2).getTextContent()).isEqualTo("datasets/ga4_obfuscated_sample_ecommerce.html");
        // references.
        assertThat(locations.item(3).getTextContent()).isEqualTo("references/index.html");
        // references/metrics.
        assertThat(locations.item(4).getTextContent()).isEqualTo("references/metrics/index.html");
        assertThat(locations.item(5).getTextContent()).isEqualTo("references/metrics/acquired_users.html");
        assertThat(locations.item(6).getTextContent()).isEqualTo("references/metrics/frequently_active_users.html");
        assertThat(locations.item(7).getTextContent()).isEqualTo("references/metrics/google_acquired_cohorts.html");
        assertThat(locations.item(8).getTextContent()).isEqualTo("references/metrics/highly_active_users.html");
        assertThat(locations.item(9).getTextContent()).isEqualTo("references/metrics/n_day_active_users.html");
        assertThat(locations.item(10).getTextContent()).isEqualTo("references/metrics/n_day_inactive_users.html");
        assertThat(locations.item(11).getTextContent()).isEqualTo("references/metrics/purchasers.html");
        // tables.
        assertThat(locations.item(12).getTextContent()).isEqualTo("tables/index.html");
        assertThat(locations.item(13).getTextContent()).isEqualTo("tables/events_.html");
        // Last check.
        assertThat(locations.item(14)).isNull();
    }

    @Test
    @DisplayName("Generating sitemap.xml with a base URL")
    void generateWithBaseUrl() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2_WITH_CONFIGURATION);
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
