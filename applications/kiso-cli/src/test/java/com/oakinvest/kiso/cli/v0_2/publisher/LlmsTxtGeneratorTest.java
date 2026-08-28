package com.oakinvest.kiso.cli.v0_2.publisher;

import com.oakinvest.kiso.cli.publisher.LlmsTxtGenerator;
import com.oakinvest.kiso.cli.util.BaseTest;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - llms.txt generator")
class LlmsTxtGeneratorTest extends BaseTest {

    @Test
    @DisplayName("llms.txt generation")
    void generate() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        var content = LlmsTxtGenerator.generate(knowledgeBundle, SiteConfiguration.empty());

        // Testing structure ===========================================================================================
        assertThat(content)
                .startsWith("""
                        # Knowledge Bundle
                        
                        ## index
                        
                        - [index.md](index.md): Knowledge bundle index
                        """.stripIndent())
                .contains("""
                        
                        ## datasets
                        
                        - [index.md](datasets/index.md): Index of datasets
                        - [GA4 Obfuscated Sample Ecommerce Dataset](datasets/ga4_obfuscated_sample_ecommerce.md): Obfuscated Google Analytics 4 dataset emulating a web ecommerce implementation of the Google Merchandise Store.
                        """.stripIndent())
                .contains("""
                        
                        ## references/metrics
                        
                        - [index.md](references/metrics/index.md): Index of references/metrics
                        - [Acquired Users Metric](references/metrics/acquired_users.md): Builds an audience of users acquired via a specific Source, Medium, and Campaign name.
                        """.stripIndent())
                .contains("""
                        
                        ## tables
                        
                        - [index.md](tables/index.md): Index of tables
                        - [GA4 Events Export](tables/events_.md): Google Analytics 4 event-level daily sharded export tables containing user interaction logs.
                        """.stripIndent())
                .endsWith("\n");
        assertThat(content).containsOnlyOnce("# Knowledge Bundle");
        assertThat(content.lines().filter(line -> line.startsWith("- [")).count()).isEqualTo(14);

        // We should not have the assets ===============================================================================
        assertThat(content).doesNotContain("## assets")
                .doesNotContain("## assets/css")
                .doesNotContain("## assets/js");
    }

    @Test
    @DisplayName("Generating llms.txt with a base URL")
    void generateWithBaseUrl() {
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var siteConfiguration = new SiteConfiguration(
                "https://knowledge.angara.finance",
                null,
                null,
                null,
                null);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        var content = LlmsTxtGenerator.generate(knowledgeBundle, siteConfiguration);

        assertThat(content)
                .contains("[index.md](https://knowledge.angara.finance/index.md)")
                .contains("[index.md](https://knowledge.angara.finance/datasets/index.md)")
                .contains("[Acquired Users Metric](https://knowledge.angara.finance/references/metrics/acquired_users.md)");
    }

}
