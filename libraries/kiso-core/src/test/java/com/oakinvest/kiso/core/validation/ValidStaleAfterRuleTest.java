package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.validation.rule.ValidStaleAfterRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_STALE_AFTER;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Valid stale after rule")
public class ValidStaleAfterRuleTest extends BaseTest {

    private final ValidStaleAfterRule rule = new ValidStaleAfterRule();

    @Test
    @DisplayName("Accept a valid stale after date")
    void validStaleAfter(@TempDir final Path temporaryDirectory) throws IOException {
        final var markdownFile = createMarkdownFile(temporaryDirectory, "stale_after: 2026-08-26");

        assertThat(rule.validate(createBundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Accept a missing stale after date")
    void missingStaleAfter(@TempDir final Path temporaryDirectory) throws IOException {
        final var markdownFile = createMarkdownFile(temporaryDirectory, "type: Concept");

        assertThat(rule.validate(createBundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @ParameterizedTest(name = "{0} is not a valid stale after date")
    @ValueSource(strings = {"26-08-2026", "2026-8-26", "2026-02-30", ""})
    void invalidStaleAfter(final String staleAfter, @TempDir final Path temporaryDirectory) throws IOException {
        final var markdownFile = createMarkdownFile(temporaryDirectory, "stale_after: " + staleAfter);

        assertThat(rule.validate(createBundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(WARNING);
            assertThat(issue.code()).isEqualTo(INVALID_STALE_AFTER);
            assertThat(issue.message()).isEqualTo("The stale_after field is not a valid YYYY-MM-DD date");
            assertThat(issue.path()).isEqualTo(Path.of("concept.md"));
        });
    }

}
