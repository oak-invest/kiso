package com.oakinvest.kiso.core.publishing;

import com.oakinvest.kiso.core.loading.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class LlmsTxtGeneratorTest extends BaseTest {

    @Test
    @DisplayName("Generating llms.txt content")
    void generate() throws URISyntaxException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        String content = LlmsTxtGenerator.generate(knowledgeBundle);


        // Testing structure ==========================================================================================
        assertThat(content)
                .startsWith("""
                        # Knowledge Bundle
                        
                        ## Index
                        
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
    }

    @Test
    @DisplayName("Rejecting null knowledge bundle")
    void rejectNullKnowledgeBundle() {
        assertThatNullPointerException()
                .isThrownBy(() -> LlmsTxtGenerator.generate(null))
                .withMessage("knowledgeBundle must not be null");
    }

}
