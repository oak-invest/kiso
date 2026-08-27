package com.oakinvest.kiso.cli.v0_2.publisher;

import com.oakinvest.kiso.cli.publisher.TagPageGenerator;
import com.oakinvest.kiso.cli.util.BaseTest;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - Tag pages generator")
public class TagPageGeneratorTest extends BaseTest {

    @Test
    @DisplayName("Tag page generation")
    void generate() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_ACME_V_0_2);

        // Testing tag page generation for "finance" tag without base url ==============================================
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        assertThat(TagPageGenerator.generate(knowledgeBundle, "finance"))
                // Frontmatter.
                .contains("""
                        ---
                        type: tag
                        title: finance
                        ---
                        """.stripIndent())
                // Links.
                .contains("- [Gross margin for a period](../computations/gross-margin-period.md): Sanctioned SQL that produces the gross-margin figure for a period, per Acme's FY2026 Cost Allocation Standard (full COGS = product + fulfillment + shipping + payment fees).")
                .contains("- [Revenue for a fiscal year](../computations/revenue-ytd.md): Sanctioned SQL that produces the recognized-revenue figure for a given fiscal year, per Acme's FY2026 Revenue Recognition Policy.")
                .contains("- [Gross Margin (legacy, pre-FY2026)](../metrics/gross-margin-legacy.md): Retired gross-margin definition that included only product cost. Preserved for historical query reproducibility. Do not use for new analyses.")
                .contains("- [Gross Margin](../metrics/gross-margin.md): Gross margin for a period, per Acme's FY2026 Cost Allocation Standard (product cost + inbound fulfillment + outbound shipping + payment fees).")
                .contains("- [Revenue](../metrics/revenue.md): Recognized revenue for a period, per Acme's FY2026 revenue-recognition policy. Backed by an Attested Computation.")
                .contains("- [Acme Retail — Cost Allocation \\& Margin Standard (FY2026)](../policies/margin-standard.md): Finance policy defining COGS composition and the standard gross-margin formula. Introduced FY2026 (superseded a legacy definition that excluded fulfillment/shipping).")
                .contains("- [Acme Retail — Revenue Recognition Policy (FY2026)](../policies/revenue-recognition.md): Finance policy defining when a customer order is recognized as revenue. Reviewed annually.");

        // Testing tag page generation for "finance" tag with base url =================================================
        knowledgeBundle = KnowledgeBundleLoader.load(resourcePath, SiteConfiguration.builder()
                .baseUrl("https://acme.com/knowledge-base/")
                .build());
        assertThat(TagPageGenerator.generate(knowledgeBundle, "finance"))
                .contains("""
                        ---
                        type: tag
                        title: finance
                        ---
                        """.stripIndent())
                // Links.
                .contains("- [Gross margin for a period](https://acme.com/knowledge-base/computations/gross-margin-period.md): Sanctioned SQL that produces the gross-margin figure for a period, per Acme's FY2026 Cost Allocation Standard (full COGS = product + fulfillment + shipping + payment fees).")
                .contains("- [Revenue for a fiscal year](https://acme.com/knowledge-base/computations/revenue-ytd.md): Sanctioned SQL that produces the recognized-revenue figure for a given fiscal year, per Acme's FY2026 Revenue Recognition Policy.")
                .contains("- [Gross Margin (legacy, pre-FY2026)](https://acme.com/knowledge-base/metrics/gross-margin-legacy.md): Retired gross-margin definition that included only product cost. Preserved for historical query reproducibility. Do not use for new analyses.")
                .contains("- [Gross Margin](https://acme.com/knowledge-base/metrics/gross-margin.md): Gross margin for a period, per Acme's FY2026 Cost Allocation Standard (product cost + inbound fulfillment + outbound shipping + payment fees).")
                .contains("- [Revenue](https://acme.com/knowledge-base/metrics/revenue.md): Recognized revenue for a period, per Acme's FY2026 revenue-recognition policy. Backed by an Attested Computation.")
                .contains("- [Acme Retail — Cost Allocation \\& Margin Standard (FY2026)](https://acme.com/knowledge-base/policies/margin-standard.md): Finance policy defining COGS composition and the standard gross-margin formula. Introduced FY2026 (superseded a legacy definition that excluded fulfillment/shipping).")
                .contains("- [Acme Retail — Revenue Recognition Policy (FY2026)](https://acme.com/knowledge-base/policies/revenue-recognition.md): Finance policy defining when a customer order is recognized as revenue. Reviewed annually.");
    }

}
