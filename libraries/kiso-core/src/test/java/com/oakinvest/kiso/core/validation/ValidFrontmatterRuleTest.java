package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.BaseTest;
import com.oakinvest.kiso.core.validation.rule.ValidFrontmatterRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_FRONTMATTER;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_FRONTMATTER_TYPE;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

class ValidFrontmatterRuleTest extends BaseTest {

    ValidFrontmatterRule rule = new ValidFrontmatterRule();

    @Test
    @DisplayName("Report a concept file without frontmatter")
    void conceptWithoutFrontmatter() {
        // We create a concept file without frontmatter ================================================================
        Path markdownFilePath = Path.of("concept-without-frontmatter.md");
        MarkdownFile markdownFile = markdownFile(markdownFilePath, CONCEPT, null);

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
        Path markdownFilePath = Path.of("concept-without-type.md");
        MarkdownFile markdownFile = markdownFile(markdownFilePath, CONCEPT, Frontmatter.empty());

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
        Path markdownFilePath = Path.of("index.md");
        MarkdownFile markdownFile = markdownFile(markdownFilePath, INDEX, null);

        // We check that there was no error at all =====================================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).isEmpty();
    }

}
