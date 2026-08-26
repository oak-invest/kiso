package com.oakinvest.kiso.cli.v0_1.publisher;

import com.oakinvest.kiso.cli.publisher.LlmsTxtGenerator;
import com.oakinvest.kiso.cli.util.BaseTest;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.1 - llms.txt generator")
class LlmsTxtGeneratorTest extends BaseTest {

    @Test
    @DisplayName("llms.txt generation")
    void generate() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_1);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        var content = LlmsTxtGenerator.generate(knowledgeBundle);

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
                        - [BigQuery sample dataset for Google Analytics ecommerce web implementation](datasets/ga4_obfuscated_sample_ecommerce.md): A sample of obfuscated Google Analytics BigQuery event export data for three months from the Google Merchandise Store is available as a public dataset in BigQuery.
                        """.stripIndent())
                .contains("""
                        
                        ## references/joins
                        
                        - [index.md](references/joins/index.md): Index of references/joins
                        - [Join Google Analytics Events to Google Ads Clicks](references/joins/events___ads_clickstats.md): Join Google Analytics event data with Google Ads click data.
                        """.stripIndent())
                .contains("""
                        
                        ## tables
                        
                        - [index.md](tables/index.md): Index of tables
                        - [Events table (Google Analytics BigQuery Export)](tables/events_.md): Contains Google Analytics event export data from the \\`ga4\\_obfuscated\\_sample\\_ecommerce\\` dataset.
                        """.stripIndent())
                .endsWith("\n");
        assertThat(content).containsOnlyOnce("# Knowledge Bundle");
        assertThat(content.lines().filter(line -> line.startsWith("- [")).count()).isEqualTo(17);

        // We should not have the assets ===============================================================================
        assertThat(content).doesNotContain("## assets")
                .doesNotContain("## assets/css")
                .doesNotContain("## assets/js");
    }

    @Test
    @DisplayName("Generating llms.txt with a base URL")
    void generateWithBaseUrl() {
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_1);
        var siteConfiguration = new SiteConfiguration(
                "https://knowledge.angara.finance",
                null,
                null,
                null,
                null);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath, siteConfiguration);
        var content = LlmsTxtGenerator.generate(knowledgeBundle);

        assertThat(content)
                .contains("[index.md](https://knowledge.angara.finance/index.md)")
                .contains("[index.md](https://knowledge.angara.finance/datasets/index.md)")
                .contains("[Join Google Analytics Events to Google Ads Clicks]"
                        + "(https://knowledge.angara.finance/references/joins/events___ads_clickstats.md)");
    }

}
