package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.provenance.UsageWindow;
import com.oakinvest.kiso.core.validation.rule.ValidUsageWindowRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_USAGE_WINDOW_FROM;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_USAGE_WINDOW_TO;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Valid usage window rule")
public class ValidUsageWindowRuleTest extends BaseTest {

    final ValidUsageWindowRule rule = new ValidUsageWindowRule();

    @Test
    @DisplayName("Accept valid usage window dates")
    void validUsageWindowDates() {
        var markdownFilePath = Path.of("concept-with-valid-usage-window.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .usageWindow(UsageWindow.builder()
                        .from("2026-06-01")
                        .to("2026-06-30")
                        .build())
                .build();
        var markdownFile = createMarkdownFile(markdownFilePath, CONCEPT, frontmatter);

        assertThat(rule.validate(createBundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Report invalid usage window from date")
    void invalidUsageWindowFrom() {
        var markdownFilePath = Path.of("concept-with-invalid-usage-window-from.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .usageWindow(UsageWindow.builder()
                        .from("01-06-2026")
                        .to("2026-06-30")
                        .build())
                .build();
        var markdownFile = createMarkdownFile(markdownFilePath, CONCEPT, frontmatter);

        assertThat(rule.validate(createBundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_USAGE_WINDOW_FROM);
            assertThat(issue.message()).isEqualTo("Invalid usage_window.from field in frontmatter - Not an ISO 8601 date");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Report invalid usage window to date")
    void invalidUsageWindowTo() {
        var markdownFilePath = Path.of("concept-with-invalid-usage-window-to.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .usageWindow(UsageWindow.builder()
                        .from("2026-06-01")
                        .to("30-06-2026")
                        .build())
                .build();
        var markdownFile = createMarkdownFile(markdownFilePath, CONCEPT, frontmatter);

        assertThat(rule.validate(createBundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_USAGE_WINDOW_TO);
            assertThat(issue.message()).isEqualTo("Invalid usage_window.to field in frontmatter - Not an ISO 8601 date");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

}
