package com.oakinvest.kiso.mcp.server.service;

import com.oakinvest.kiso.mcp.server.util.BaseTest;
import org.apache.lucene.store.AlreadyClosedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Knowledge service tests")
public class KnowledgeServiceTest extends BaseTest {

    private KnowledgeService knowledgeService;

    @BeforeEach
    public void setUp() {
        knowledgeService = new KnowledgeService(getResourcePath(KB_ACME_V_0_2));
    }

    @AfterEach
    public void tearDown() {
        knowledgeService.close();
    }

    @Test
    @DisplayName("Concurrent searches share the reader safely")
    public void concurrentSearches() throws Exception {
        var expectedResults = knowledgeService.searchConcepts("discount_amount");
        try (var executor = Executors.newFixedThreadPool(4)) {
            List<Future<List<KnowledgeSearchResult>>> searches = new ArrayList<>();
            for (int search = 0; search < 20; search++) {
                searches.add(executor.submit(() -> knowledgeService.searchConcepts("discount_amount")));
            }
            for (var search : searches) {
                assertThat(search.get()).isEqualTo(expectedResults);
            }
        }
    }

    @Test
    @DisplayName("Closing the service closes its reader")
    public void close() {
        knowledgeService.close();
        assertThatThrownBy(() -> knowledgeService.searchConcepts("discount_amount"))
                .isInstanceOf(AlreadyClosedException.class);
    }


    @Test
    @DisplayName("searchConcepts()")
    public void search() {
        // Searching for an unknown thing should return an empty list.
        assertThat(knowledgeService.searchConcepts("ZZZ")).isEmpty();

        // Searching for a known thing should return a list of results.
        assertThat(knowledgeService.searchConcepts("discount_amount")).hasSize(2)
                .satisfiesExactly(
                        result1 -> assertThat(result1.conceptId()).isEqualTo("tables/orders"),
                        result2 -> assertThat(result2.conceptId()).isEqualTo("policies/revenue-recognition")
                );
    }


    @Test
    @DisplayName("getConceptContent()")
    public void getConceptContent() {
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
