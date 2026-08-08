package com.oakinvest.kiso.core.v0_2.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.publisher.SearchIndexGenerator;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - search-index.json generator")
class SearchIndexGeneratorTest extends BaseTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    @DisplayName("search-index.json generation")
    void generate() throws Exception {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var knowledgeBundle = KnowledgeBundleLoader.load(resourcePath);
        var content = SearchIndexGenerator.generate(knowledgeBundle);
        var searchIndex = OBJECT_MAPPER.readTree(content);
        var documents = searchIndex.get("documents");

        // Testing structure ==========================================================================================
        assertThat(searchIndex.has("documents")).isTrue();
        assertThat(documents.isArray()).isTrue();
        assertThat(documents.size()).isEqualTo(9);
        assertThat(documentIds(documents))
                .containsExactlyInAnyOrder(
                        "datasets/ga4_obfuscated_sample_ecommerce",
                        "references/metrics/acquired_users",
                        "references/metrics/frequently_active_users",
                        "references/metrics/google_acquired_cohorts",
                        "references/metrics/highly_active_users",
                        "references/metrics/n_day_active_users",
                        "references/metrics/n_day_inactive_users",
                        "references/metrics/purchasers",
                        "tables/events_"
                );

        // Testing document content ===================================================================================
        var userCount = documentWithId(documents, "references/metrics/acquired_users");
        assertThat(userCount.get("url").asText()).isEqualTo("references/metrics/acquired_users.html");
        assertThat(userCount.get("title").asText()).isEqualTo("Acquired Users Metric");
        assertThat(userCount.get("description").asText()).isEqualTo("Builds an audience of users acquired via a specific Source, Medium, and Campaign name.");
        assertThat(userCount.get("tags").get(0).asText()).isEqualTo("metric");
        assertThat(userCount.get("body").asText())
                .contains("This reference describes a query pattern and does not map to a single database schema.")
                .contains("COUNT(DISTINCT user_id) AS acquired_users_count");

        var eventsTable = documentWithId(documents, "tables/events_");
        assertThat(eventsTable.get("url").asText()).isEqualTo("tables/events_.html");
        assertThat(eventsTable.get("title").asText()).isEqualTo("GA4 Events Export");
        assertThat(eventsTable.get("description").asText())
                .isEqualTo("Google Analytics 4 event-level daily sharded export tables containing user interaction logs.");
        assertThat(eventsTable.get("tags").get(0).asText()).isEqualTo("analytics");
        assertThat(eventsTable.get("tags").get(1).asText()).isEqualTo("e-commerce");
        assertThat(eventsTable.get("tags").get(2).asText()).isEqualTo("ga4");
        assertThat(eventsTable.get("tags").get(3).asText()).isEqualTo("sharded-tables");
        assertThat(eventsTable.get("body").asText()).contains("The data is useful for behavioral analysis, funnel conversion mapping, and e-commerce tracking. ");
    }

}
