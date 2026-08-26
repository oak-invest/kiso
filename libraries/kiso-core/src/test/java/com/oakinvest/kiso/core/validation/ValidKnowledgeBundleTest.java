package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Valid knowledge bundle")
public class ValidKnowledgeBundleTest extends BaseTest {

    @Test
    @DisplayName("Google example bundle validation")
    void googleExamplesValidation() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // We check that Google examples don't raise any error =========================================================
        assertThat(ValidationRunner.runValidation(bundle).hasErrors()).isFalse();
    }

    @Test
    @DisplayName("Acme example bundle validation")
    void acmeExamplesValidation() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_ACME_V_0_2);
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // We check that Google examples don't raise any error =========================================================
        assertThat(ValidationRunner.runValidation(bundle).hasErrors()).isFalse();
    }

}
