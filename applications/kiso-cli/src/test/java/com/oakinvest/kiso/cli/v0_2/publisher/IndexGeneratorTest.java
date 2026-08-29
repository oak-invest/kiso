package com.oakinvest.kiso.cli.v0_2.publisher;

import com.oakinvest.kiso.cli.publisher.IndexGenerator;
import com.oakinvest.kiso.cli.util.BaseTest;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - index.md generator")
public class IndexGeneratorTest extends BaseTest {

    @Test
    @DisplayName("index.md generation")
    void generate() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing google OFK example root =============================================================================
        Assertions.assertThat(IndexGenerator.generate(knowledgeBundle.rootBundle()))
                .isNotBlank()
                // No content.
                .doesNotContain("## Content")
                .doesNotContain("- [index.md](index.md): Knowledge bundle index")
                // Subdirectories.
                .contains("## Subdirectories")
                .contains("- [datasets](datasets/index.md)")
                .contains("- [references](references/index.md)")
                .contains("- [tables](tables/index.md)");

        // Testing google OFK example references/metrics================================================================
        var metricsBundle = knowledgeBundle.bundles()
                .filter(bundle -> bundle.name().equals("references/metrics"))
                .findFirst()
                .orElseThrow();

        assertThat(IndexGenerator.generate(metricsBundle))
                // Content.
                .contains("## Content")
                .contains("- [Acquired Users Metric](acquired_users.md): Builds an audience of users acquired via a specific Source, Medium, and Campaign name.")
                .contains("- [Frequently Active Users Metric](frequently_active_users.md): Builds an audience of users active on at least N of the last M days.")
                .contains("- [Google Acquired Cohorts Metric](google_acquired_cohorts.md): Builds an audience of users acquired in a specific time-window cohort filtered by Google campaign source.")
                .contains("- [Highly Active Users Metric](highly_active_users.md): Builds an audience of users active for more than N minutes in the last M days.")
                .contains("- [N-Day Active Users Metric](n_day_active_users.md): Builds an audience of users active in the last N days based on engagement\\_time\\_msec.")
                .contains("- [N-Day Inactive Users Metric](n_day_inactive_users.md): Builds an audience of users active in the last M days who have not been active in the last N days.")
                .contains("- [Purchasers Audience Metric](purchasers.md): Computes the count or list of users who have completed a purchase or in-app purchase.")
                // no subdirectories.
                .doesNotContain("## Subdirectories");
    }

}
