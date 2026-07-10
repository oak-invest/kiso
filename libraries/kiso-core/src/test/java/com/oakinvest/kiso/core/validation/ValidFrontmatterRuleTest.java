package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.util.BaseTest;
import com.oakinvest.kiso.core.validation.rule.ValidFrontmatterRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.LOG;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_TIMESTAMP;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_FRONTMATTER;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_FRONTMATTER_TYPE;
import static com.oakinvest.kiso.core.validation.ValidationCode.UNEXPECTED_FRONTMATTER;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

class ValidFrontmatterRuleTest extends BaseTest {

    final ValidFrontmatterRule rule = new ValidFrontmatterRule();

    @Test
    @DisplayName("Report a concept file without frontmatter")
    void conceptWithoutFrontmatter() {
        // We create a concept file without frontmatter ================================================================
        var markdownFilePath = Path.of("concept-without-frontmatter.md");
        var markdownFile = markdownFile(markdownFilePath, CONCEPT, null);

        // Run validation to check a missing mandatory frontmatter =====================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(MISSING_FRONTMATTER);
            assertThat(issue.message()).isEqualTo("File concept-without-frontmatter.md is missing mandatory frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Report a concept file without a type in its frontmatter")
    void conceptWithoutFrontmatterType() {
        // We create a concept file with frontmatter but without the type field ========================================
        var markdownFilePath = Path.of("concept-without-type.md");
        var markdownFile = markdownFile(markdownFilePath, CONCEPT, Frontmatter.empty());

        // Run validation to check a missing mandatory frontmatter type ================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(MISSING_FRONTMATTER_TYPE);
            assertThat(issue.message()).isEqualTo("File concept-without-type.md is missing mandatory 'type' in frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Ignore an index file without frontmatter")
    void indexWithoutFrontmatter() {
        // We create an index.md file without frontmatter, which is allowed ============================================
        var markdownFilePath = Path.of("index.md");
        var markdownFile = markdownFile(markdownFilePath, INDEX, null);

        // We check that there was no error at all =====================================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Timestamp is present but doesn't respect ISO 8601 datetime format")
    void invalidTimestamp() {
        // We create a concept file with frontmatter but with an invalid timestamp =====================================
        var markdownFilePath = Path.of("concept-with-invalid-timestamp.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .timestamp("02-07-2026T14:30:00Z")
                .build();
        var markdownFile = markdownFile(markdownFilePath, CONCEPT, frontmatter);

        // Run validation to check an invalid timestamp ================================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_TIMESTAMP);
            assertThat(issue.message()).isEqualTo("File concept-with-invalid-timestamp.md has invalid 'timestamp' in frontmatter. It must be in ISO 8601 datetime format");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Non concept files should not have frontmatter")
    void unexpectedFrontmatter() {
        // We create files that should not have frontmatter ============================================================
        var indexFilePath = Path.of(INDEX.getFileName());
        var frontmatter = Frontmatter.builder().build();
        var indexFile = markdownFile(indexFilePath, INDEX, frontmatter);

        // log.md
        var logFilePath = Path.of(LOG.getFileName());
        var logFile = markdownFile(logFilePath, LOG, frontmatter);

        // Run validation to check an unexpected frontmatter ============================================================
        assertThat(rule.validate(bundleWith(indexFile), indexFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(UNEXPECTED_FRONTMATTER);
            assertThat(issue.message()).isEqualTo("File index.md is not a concept file and should not contain frontmatter");
            assertThat(issue.path()).isEqualTo(indexFilePath);
        });

        assertThat(rule.validate(bundleWith(logFile), logFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(UNEXPECTED_FRONTMATTER);
            assertThat(issue.message()).isEqualTo("File log.md is not a concept file and should not contain frontmatter");
            assertThat(issue.path()).isEqualTo(logFilePath);
        });
    }

}
