package com.oakinvest.kiso.mcp.server.service;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.mcp.server.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Knowledge service tests")
public class KnowledgeServiceTest extends BaseTest {

    @Test
    @DisplayName("search()")
    public void search() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_ACME_V_0_2);
        var knowledgeService = new KnowledgeService(KnowledgeBundleLoader.load(resourcePath));

        // Searching for an unknown thing should return an empty list.
        assertThat(knowledgeService.search("ZZZ")).isEmpty();

        // Searching for a known thing should return a list of results.
        assertThat(knowledgeService.search("discount_amount")).hasSize(2)
                .satisfiesExactly(
                        result1 -> assertThat(result1.conceptId()).isEqualTo("tables/orders"),
                        result2 -> assertThat(result2.conceptId()).isEqualTo("policies/revenue-recognition")
                );
    }


    @Test
    @DisplayName("getConceptContent()")
    public void getConceptContent() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_ACME_V_0_2);
        var knowledgeService = new KnowledgeService(KnowledgeBundleLoader.load(resourcePath));

        // Calling an unknown concept should return an empty optional.
        assertThat(knowledgeService.getConceptContent("unknown-concept")).isEmpty();

        // Calling a known concept should return the content of the concept.
        assertThat(knowledgeService.getConceptContent("computations/revenue-ytd")).isPresent()
                .hasValueSatisfying(content ->
                        assertThat(content).containsSubsequence(
                                "---",
                                "type: Attested Computation",
                                "---",
                                "# Computation",
                                "This computation implements the four rules of the FY2026 Revenue Recognition Policy",
                                "# Freshness",
                                "[^revenue-policy]: Revenue Recognition Policy (FY2026)"
                        )
                );
    }

}
