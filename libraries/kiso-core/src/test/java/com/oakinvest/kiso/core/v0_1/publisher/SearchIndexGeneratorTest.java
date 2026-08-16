package com.oakinvest.kiso.core.v0_1.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.publisher.SearchIndexGenerator;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.1 - search-index.json generator")
class SearchIndexGeneratorTest extends BaseTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    @DisplayName("search-index.json generation")
    void generate() throws Exception {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_1);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        var content = SearchIndexGenerator.generate(knowledgeBundle);
        var searchIndex = OBJECT_MAPPER.readTree(content);
        var documents = searchIndex.get("documents");

        // Testing structure ==========================================================================================
        assertThat(searchIndex.has("documents")).isTrue();
        assertThat(documents.isArray()).isTrue();
        assertThat(documents.size()).isEqualTo(11);
        assertThat(documentIds(documents))
                .containsExactlyInAnyOrder(
                        "datasets/ga4_obfuscated_sample_ecommerce",
                        "references/joins/events___ads_clickstats",
                        "references/metrics/avg_pageviews",
                        "references/metrics/avg_spend_per_purchase_session_by_user",
                        "references/metrics/avg_transactions_per_purchaser",
                        "references/metrics/day_count",
                        "references/metrics/event_count",
                        "references/metrics/new_user_count",
                        "references/metrics/overall_avg_spend_per_purchase_session",
                        "references/metrics/user_count",
                        "tables/events_"
                );

        // Testing document content ===================================================================================
        var userCount = documentWithId(documents, "references/metrics/user_count");
        assertThat(userCount).isNotNull();
        assertThat(userCount.get("url").asText()).isEqualTo("references/metrics/user_count.html");
        assertThat(userCount.get("title").asText()).isEqualTo("User Count");
        assertThat(userCount.get("description").asText()).isEqualTo("Total number of unique users.");
        assertThat(userCount.get("tags").get(0).asText()).isEqualTo("metric");
        assertThat(userCount.get("body").asText())
                .contains("Total number of unique users.")
                .contains("COUNT(DISTINCT user_pseudo_id)");

        var eventsTable = documentWithId(documents, "tables/events_");
        assertThat(eventsTable).isNotNull();
        assertThat(eventsTable.get("url").asText()).isEqualTo("tables/events_.html");
        assertThat(eventsTable.get("title").asText()).isEqualTo("Events table (Google Analytics BigQuery Export)");
        assertThat(eventsTable.get("description").asText())
                .isEqualTo("Contains Google Analytics event export data from the `ga4_obfuscated_sample_ecommerce` dataset.");
        assertThat(eventsTable.get("tags").get(0).asText()).isEqualTo("events");
        assertThat(eventsTable.get("body").asText()).contains("Google Analytics event export data");
    }

}
