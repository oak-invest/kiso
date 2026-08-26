package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.okf.markdown.provenance.Source;
import com.oakinvest.kiso.core.util.BaseTest;
import com.oakinvest.kiso.core.validation.rule.DuplicateSourceIdRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.DUPLICATE_SOURCE_ID;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Duplicate source id rule")
public class DuplicateSourceIdRuleTest extends BaseTest {

    final DuplicateSourceIdRule rule = new DuplicateSourceIdRule();

    @Test
    @DisplayName("Accept unique source ids")
    void uniqueSourceIds() {
        var markdownFile = markdownFileWithSources(
                Source.builder().id("first-source").build(),
                Source.builder().id("second-source").build(),
                Source.builder().build()
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Warn once for each duplicate source id")
    void duplicateSourceIds() {
        var markdownFile = markdownFileWithSources(
                Source.builder().id("first-source").build(),
                Source.builder().id("duplicate-source").build(),
                Source.builder().id("duplicate-source").build(),
                Source.builder().id("duplicate-source").build()
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(WARNING);
            assertThat(issue.code()).isEqualTo(DUPLICATE_SOURCE_ID);
            assertThat(issue.message()).isEqualTo("Multiple sources[] entries use the same id: duplicate-source");
            assertThat(issue.path()).isEqualTo(Path.of("concept.md"));
        });
    }

    private MarkdownFile markdownFileWithSources(final Source... sources) {
        final Path markdownFilePath = Path.of("concept.md");
        final Frontmatter frontmatter = Frontmatter.builder()
                .type("Concept")
                .sources(List.of(sources))
                .build();
        return markdownFile(markdownFilePath, CONCEPT, frontmatter);
    }

}
