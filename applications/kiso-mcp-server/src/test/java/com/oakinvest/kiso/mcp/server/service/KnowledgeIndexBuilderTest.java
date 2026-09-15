package com.oakinvest.kiso.mcp.server.service;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.mcp.server.util.BaseTest;
import org.apache.lucene.index.DirectoryReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Knowledge index builder tests")
public class KnowledgeIndexBuilderTest extends BaseTest {

    @Test
    @DisplayName("build() returns independent, readable indexes")
    public void buildIndependentIndexes() throws IOException {
        var knowledgeBundle = KnowledgeBundleLoader.load(getResourcePath(KB_ACME_V_0_2));

        try (var firstIndex = KnowledgeIndexBuilder.build(knowledgeBundle)) {
            int conceptCount;
            try (var firstReader = DirectoryReader.open(firstIndex)) {
                conceptCount = firstReader.numDocs();
                assertThat(conceptCount).isPositive();
            }

            try (var secondIndex = KnowledgeIndexBuilder.build(knowledgeBundle);
                 var secondReader = DirectoryReader.open(secondIndex)) {
                assertThat(secondIndex).isNotSameAs(firstIndex);
                assertThat(secondReader.numDocs()).isEqualTo(conceptCount);
            }

            try (var firstReader = DirectoryReader.open(firstIndex)) {
                assertThat(firstReader.numDocs()).isEqualTo(conceptCount);
            }
        }
    }
}
