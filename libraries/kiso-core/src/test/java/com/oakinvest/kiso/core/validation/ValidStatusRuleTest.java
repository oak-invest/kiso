package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.validation.rule.ValidStatusRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_LIFE_CYCLE_STATUS;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Valid status rule")
public class ValidStatusRuleTest extends BaseTest {

    private final ValidStatusRule rule = new ValidStatusRule();

    @ParameterizedTest(name = "{0} is a valid lifecycle status")
    @ValueSource(strings = {"draft", "stable", "deprecated"})
    void validLifecycleStatus(final String status, @TempDir final Path temporaryDirectory) throws IOException {
        final var markdownFile = createMarkdownFile(temporaryDirectory, "status: " + status);

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Missing lifecycle status defaults to stable")
    void missingLifecycleStatus(@TempDir final Path temporaryDirectory) throws IOException {
        final var markdownFile = createMarkdownFile(temporaryDirectory, "type: Concept");

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Invalid lifecycle status")
    void invalidLifecycleStatus(@TempDir final Path temporaryDirectory) throws IOException {
        final var markdownFile = createMarkdownFile(temporaryDirectory, "status: invalid");

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(WARNING);
            assertThat(issue.code()).isEqualTo(INVALID_LIFE_CYCLE_STATUS);
            assertThat(issue.message()).isEqualTo("The status is not one of: draft, stable, deprecated");
            assertThat(issue.path()).isEqualTo(Path.of("concept.md"));
        });
    }

}
