package com.oakinvest.kiso.core.model;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
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
        var bundle = new KnowledgeBundleLoader().load(resourcePath);

        // Testing .bundles() ==========================================================================================
        assertThat(bundle.bundles())
                .hasSize(6)
                .satisfiesExactly(
                        b -> assertThat(b.name()).isEqualTo(""),
                        b -> assertThat(b.name()).isEqualTo("datasets"),
                        b -> assertThat(b.name()).isEqualTo("references"),
                        b -> assertThat(b.name()).isEqualTo("references/joins"),
                        b -> assertThat(b.name()).isEqualTo("references/metrics"),
                        b -> assertThat(b.name()).isEqualTo("tables")
                );
    }

}
