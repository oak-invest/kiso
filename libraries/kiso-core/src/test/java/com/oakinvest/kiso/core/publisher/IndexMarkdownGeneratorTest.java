package com.oakinvest.kiso.core.publisher;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexMarkdownGeneratorTest extends BaseTest {

    @Test
    @DisplayName("Index.md generation")
    void indexMarkdownGenerator() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing google OFK example root =============================================================================
        assertThat(IndexMarkdownGenerator.generate(knowledgeBundle.rootBundle()))
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
        final Optional<Bundle> joinsBundle = knowledgeBundle.bundles()
                .filter(bundle -> bundle.name().equals("references/joins"))
                .findFirst();
        assertThat(joinsBundle).isPresent();

        assertThat(IndexMarkdownGenerator.generate(joinsBundle.get()))
                // Content.
                .contains("## Content")
                .contains("- [Join Google Analytics Events to Google Ads Clicks](events___ads_clickstats.md): Join Google Analytics event data with Google Ads click data.")
                // no subdirectories.
                .doesNotContain("## Subdirectories");

    }

}
