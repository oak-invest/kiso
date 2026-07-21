package com.oakinvest.kiso.core.publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class SearchIndexGeneratorTest extends BaseTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    @DisplayName("Generating search index content")
    void generate() throws Exception {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE);
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
        assertThat(userCount.get("url").asText()).isEqualTo("references/metrics/user_count.html");
        assertThat(userCount.get("title").asText()).isEqualTo("User Count");
        assertThat(userCount.get("description").asText()).isEqualTo("Total number of unique users.");
        assertThat(userCount.get("tags").get(0).asText()).isEqualTo("metric");
        assertThat(userCount.get("body").asText())
                .contains("Total number of unique users.")
                .contains("COUNT(DISTINCT user_pseudo_id)");

        var eventsTable = documentWithId(documents, "tables/events_");
        assertThat(eventsTable.get("url").asText()).isEqualTo("tables/events_.html");
        assertThat(eventsTable.get("title").asText()).isEqualTo("Events table (Google Analytics BigQuery Export)");
        assertThat(eventsTable.get("description").asText())
                .isEqualTo("Contains Google Analytics event export data from the `ga4_obfuscated_sample_ecommerce` dataset.");
        assertThat(eventsTable.get("tags").get(0).asText()).isEqualTo("events");
        assertThat(eventsTable.get("body").asText()).contains("Google Analytics event export data");
    }

    /**
     * Returns the document ids from search documents.
     *
     * @param documents search documents JSON array
     * @return document ids
     */
    private static java.util.List<String> documentIds(final JsonNode documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(document -> document.get("id").asText())
                .toList();
    }

    /**
     * Finds a document by id.
     *
     * @param documents search documents JSON array
     * @param id        document id
     * @return matching document
     */
    private static JsonNode documentWithId(final JsonNode documents, final String id) {
        for (JsonNode document : documents) {
            if (document.get("id").asText().equals(id)) {
                return document;
            }
        }
        fail("Missing search document: " + id);
        return null;
    }

}
