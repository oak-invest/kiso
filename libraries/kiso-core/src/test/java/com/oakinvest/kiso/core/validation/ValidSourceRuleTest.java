package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.provenance.Source;
import com.oakinvest.kiso.core.validation.rule.ValidSourceRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_SOURCE_LAST_MODIFIED;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_SOURCE_RESOURCE;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Valid source rule")
public class ValidSourceRuleTest extends BaseTest {

    final ValidSourceRule rule = new ValidSourceRule();

    @Test
    @DisplayName("Accept valid source entries")
    void validSources() {
        var markdownFilePath = Path.of("concept-with-valid-sources.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .sources(List.of(Source.builder()
                        .resource("https://example.com/source")
                        .lastModified("2026-06-30")
                        .build()))
                .build();
        var markdownFile = createMarkdownFile(markdownFilePath, CONCEPT, frontmatter);

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Report source without resource")
    void missingSourceResource() {
        var markdownFilePath = Path.of("concept-with-source-without-resource.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .sources(List.of(Source.builder()
                        .title("Source without resource")
                        .build()))
                .build();
        var markdownFile = createMarkdownFile(markdownFilePath, CONCEPT, frontmatter);

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(MISSING_SOURCE_RESOURCE);
            assertThat(issue.message()).isEqualTo("Missing sources[].resource field in frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Report invalid source last modified date")
    void invalidSourceLastModified() {
        var markdownFilePath = Path.of("concept-with-invalid-source-last-modified.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .sources(List.of(Source.builder()
                        .resource("https://example.com/source")
                        .lastModified("30-06-2026")
                        .build()))
                .build();
        var markdownFile = createMarkdownFile(markdownFilePath, CONCEPT, frontmatter);

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_SOURCE_LAST_MODIFIED);
            assertThat(issue.message()).isEqualTo("Invalid sources[].last_modified field in frontmatter - Not an ISO 8601 date");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

}
