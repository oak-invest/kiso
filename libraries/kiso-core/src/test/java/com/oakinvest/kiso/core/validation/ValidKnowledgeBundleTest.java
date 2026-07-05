package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidKnowledgeBundleTest extends BaseTest {

    @Test
    @DisplayName("Google example bundle validation")
    void googleExamplesValidation() throws URISyntaxException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // We check that Google examples don't raise any error =========================================================
        ValidationReport report = new ValidationRunner().runValidation(bundle);
        assertThat(report.hasErrors()).isFalse();
    }


}
