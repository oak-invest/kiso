package com.oakinvest.kiso.core.publisher;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("index.md generator")
public class IndexGeneratorTest extends BaseTest {

    @Test
    @DisplayName("index.md generation")
    void generate() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing google OFK example root =============================================================================
        assertThat(IndexGenerator.generate(knowledgeBundle.rootBundle()))
                .isNotBlank()
                // No content.
                .doesNotContain("## Content")
                .doesNotContain("- [index.md](index.md): Knowledge bundle index")
                // Subdirectories.
                .contains("## Subdirectories")
                .contains("- [datasets](datasets/index.md)")
                .contains("- [references](references/index.md)")
                .contains("- [tables](tables/index.md)");

        // Testing google OFK example references/joins =================================================================
        var joinsBundle = knowledgeBundle.bundles()
                .filter(bundle -> bundle.name().equals("references/joins"))
                .findFirst()
                .orElseThrow();

        assertThat(IndexGenerator.generate(joinsBundle))
                // Content.
                .contains("## Content")
                .contains("- [Join Google Analytics Events to Google Ads Clicks](events___ads_clickstats.md): Join Google Analytics event data with Google Ads click data.")
                // no subdirectories.
                .doesNotContain("## Subdirectories");
    }

}
