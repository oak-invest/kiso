package com.oakinvest.kiso.core.model;

import com.oakinvest.kiso.core.loading.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBundleTest extends BaseTest {

    @Test
    @DisplayName("Testing KnowledgeBundle.bundles()")
    void bundles() throws URISyntaxException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var rootBundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing .bundles() ==========================================================================================
        assertThat(rootBundle.bundles())
                .hasSize(6)
                .satisfiesExactly(
                        bundle -> assertThat(bundle.name()).isEqualTo("Index"),
                        bundle -> assertThat(bundle.name()).isEqualTo("datasets"),
                        bundle -> assertThat(bundle.name()).isEqualTo("references"),
                        bundle -> assertThat(bundle.name()).isEqualTo("references/joins"),
                        bundle -> assertThat(bundle.name()).isEqualTo("references/metrics"),
                        bundle -> assertThat(bundle.name()).isEqualTo("tables")
                );
    }

}
