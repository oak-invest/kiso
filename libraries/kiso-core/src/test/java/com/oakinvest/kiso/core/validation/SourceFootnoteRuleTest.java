package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.provenance.Source;
import com.oakinvest.kiso.core.validation.rule.SourceFootnoteRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_SOURCE_FOR_FOOTNOTE;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Source footnote rule")
class SourceFootnoteRuleTest extends BaseTest {

    private final SourceFootnoteRule rule = new SourceFootnoteRule();

    @Test
    @DisplayName("Accept footnotes matching source identifiers")
    void matchingSourceIdentifiers() {
        final MarkdownFile markdownFile = markdownFile(
                """
                        A supported claim.[^source-id]
                        [^source-id]: Source description
                        """,
                List.of(Source.builder().id("source-id").build())
        );

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Report each unknown footnote identifier once")
    void unknownSourceIdentifiers() {
        final MarkdownFile markdownFile = markdownFile(
                """
                        First claim.[^unknown] Second claim.[^unknown]
                        [^unknown]: Unknown source
                        """,
                List.of(Source.builder().id("known").build())
        );

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(WARNING);
            assertThat(issue.code()).isEqualTo(MISSING_SOURCE_FOR_FOOTNOTE);
            assertThat(issue.message()).isEqualTo("Footnote [^unknown] does not match any sources[].id");
            assertThat(issue.path()).isEqualTo(Path.of("concept.md"));
        });
    }

    @Test
    @DisplayName("Accept a body without footnotes")
    void bodyWithoutFootnotes() {
        final MarkdownFile markdownFile = markdownFile("A claim without a footnote.", List.of());

        assertThat(rule.validate(createKnowledgeBundleWith(markdownFile), markdownFile)).isEmpty();
    }

    private MarkdownFile markdownFile(final String body, final List<Source> sources) {
        final Path path = Path.of("concept.md");
        final Frontmatter frontmatter = Frontmatter.builder()
                .type("Concept")
                .sources(sources)
                .build();

        return MarkdownFile.builder()
                .fileName(path.getFileName().toString())
                .kind(CONCEPT)
                .absolutePath(path)
                .relativePath(path)
                .frontmatter(frontmatter)
                .frontmatterPresent(true)
                .body(body)
                .build();
    }

}
