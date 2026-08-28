package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.validation.rule.BrokenLinkRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.validation.ValidationCode.BROKEN_LINK;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Broken link rule")
public class BrokenLinkRuleTest extends BaseTest {

    @Test
    @DisplayName("Report a broken links")
    void reportBrokenLinks(@TempDir final Path temporaryDirectory) throws IOException {
        // Errors ======================================================================================================
        // dir1/content1.md                 --> Failed link to /uknownContent.md
        // dir1/dir1subdir1/content11.md    --> Failed link to /dir2/uknownContent.md
        // dir1/dir1subdir1/index.md        --> No error
        // dir1/dir1subdir2/content12.md    --> Failed link to /dir2/dir2subdir2/uknownContent.md
        // --
        // dir2/dir2subdir1/content21.md    --> Failed link to /dir2/dir2subdir2/uknownContent.md
        // dir2/dir2subdir2/content22.md    --> Failed link to /uknownContent.md
        // dir2/dir2subdir2/index.md        --> No error
        // dir2/dir2subdir3/content23.md    --> No error
        // --
        // index.md                         --> Failed link to /uknownContent.md & /dir2/uknownContent.md
        // content.md                       --> Failed link to /dir2/dir2subdir3/uknownContent.md

        // Create and load bundle =======================================================================================
        createMarkdownFile(temporaryDirectory, "index.md", """
                [index.md](./index.md)
                [content.md](./content.md)
                [/uknownContent.md](./uknownContent.md)
                [/dir2/uknownContent.md](./dir2/uknownContent.md)
                """);
        createMarkdownFile(temporaryDirectory, "content.md", """
                [/dir2/dir2subdir3/uknownContent.md](dir2/dir2subdir3/uknownContent.md)
                """);
        createMarkdownFile(temporaryDirectory, "dir1/content1.md", """
                [/index.md](../index.md)
                [/uknownContent.md](../uknownContent.md)
                """);
        createMarkdownFile(temporaryDirectory, "dir1/dir1subdir1/content11.md", """
                [/dir2/uknownContent.md](../../dir2/uknownContent.md)
                [/index.md](../../index.md)
                [/content.md](../../index.md)
                """);
        createMarkdownFile(temporaryDirectory, "dir1/dir1subdir1/index.md", """
                [/index.md](../../index.md)
                [/content.md](../../index.md)
                """);
        createMarkdownFile(temporaryDirectory, "dir1/dir1subdir2/content12.md", """
                [/index.md](../../index.md)
                [/content.md](../../index.md)
                [/dir2/dir2subdir2/uknownContent.md](../../dir2/dir2subdir2/uknownContent.md)
                """);
        createMarkdownFile(temporaryDirectory, "dir2/dir2subdir1/content21.md", "");
        createMarkdownFile(temporaryDirectory, "dir2/dir2subdir2/content22.md", """
                [/index.md](../../index.md)
                [/content.md](../../index.md)
                [/dir2/dir2subdir3/uknownContent.md](../../dir2/dir2subdir3/uknownContent.md)
                """);
        createMarkdownFile(temporaryDirectory, "dir2/dir2subdir2/index.md", "");
        createMarkdownFile(temporaryDirectory, "dir2/dir2subdir3/content23.md", "");

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);

        // Run validation and check that the warnings for a broken links =================================================
        var rule = new BrokenLinkRule();
        var issues = bundle.bundles()
                .flatMap(currentBundle -> currentBundle.markdownFiles()
                        .stream()
                        .flatMap(markdownFile -> rule.validate(bundle.rootBundle(), markdownFile).stream()))
                .toList();
        assertThat(issues).hasSize(7);
        assertThat(issues)
                .allSatisfy(issue -> {
                    assertThat(issue.severity()).isEqualTo(WARNING);
                    assertThat(issue.code()).isEqualTo(BROKEN_LINK);
                });
        assertThat(issues)
                .extracting(ValidationIssue::message)
                .containsExactlyInAnyOrder(
                        "File content.md contains a broken link: dir2/dir2subdir3/uknownContent.md",
                        "File index.md contains a broken link: ./uknownContent.md",
                        "File index.md contains a broken link: ./dir2/uknownContent.md",
                        "File dir1/content1.md contains a broken link: ../uknownContent.md",
                        "File dir1/dir1subdir1/content11.md contains a broken link: ../../dir2/uknownContent.md",
                        "File dir1/dir1subdir2/content12.md contains a broken link: ../../dir2/dir2subdir2/uknownContent.md",
                        "File dir2/dir2subdir2/content22.md contains a broken link: ../../dir2/dir2subdir3/uknownContent.md"
                );
    }

}
