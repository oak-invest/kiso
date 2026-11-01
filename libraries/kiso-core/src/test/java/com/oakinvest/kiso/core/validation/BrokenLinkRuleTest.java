package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.validation.rule.BrokenLinkRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.validation.ValidationCode.BROKEN_LINK;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Broken link rule")
public class BrokenLinkRuleTest extends BaseTest {

    @Test
    @DisplayName("Accept existing bundle-root and relative links to concepts and artifacts")
    void acceptExistingLocalFiles(@TempDir final Path temporaryDirectory) throws IOException {
        createMarkdownFile(temporaryDirectory, "index.md", "");
        createMarkdownFile(temporaryDirectory, "overview.md", """
                [Bundle-root concept](/notes/detail.md)
                [Relative concept](notes/detail.md)
                [Relative JSON artifact](references/snapshot.json)
                [Bundle-root JSON artifact](/references/snapshot.json)
                [Actually missing concept](notes/missing.md)
                """);
        createMarkdownFile(temporaryDirectory, "notes/detail.md", """
                [Root concept](/overview.md#overview)
                [Relative concept](../overview.md?view=full#overview)
                [Root artifact](/references/snapshot.json?download=true#example)
                [Relative artifact](../references/./snapshot.json)
                [Normalized concept](/notes/../overview.md)
                [Web](https://example.com/missing.md)
                [Protocol-relative](//example.com/missing.json)
                [Email](mailto:hello@example.com)
                [Anchor](#detail)
                [Query](?view=full)
                """);
        Files.createDirectories(temporaryDirectory.resolve("references"));
        Files.writeString(temporaryDirectory.resolve("references/snapshot.json"), "{\"example\": true}");

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);
        var rule = new BrokenLinkRule();
        var issues = bundle.markdownFiles()
                .flatMap(markdownFile -> rule.validate(bundle, markdownFile).stream())
                .toList();

        assertThat(issues).singleElement().satisfies(issue -> {
            assertThat(issue.code()).isEqualTo(BROKEN_LINK);
            assertThat(issue.severity()).isEqualTo(WARNING);
            assertThat(issue.message()).isEqualTo("File overview.md contains a broken link: notes/missing.md");
        });
    }

    @Test
    @DisplayName("Report missing files and paths outside the bundle")
    void reportMissingAndOutsideFiles(@TempDir final Path temporaryDirectory) throws IOException {
        Path rootDirectory = Files.createDirectory(temporaryDirectory.resolve("bundle"));
        Files.writeString(temporaryDirectory.resolve("outside.json"), "{}");
        createMarkdownFile(rootDirectory, "index.md", "");
        createMarkdownFile(rootDirectory, "notes/detail.md", """
                [Missing root concept](/missing.md)
                [Missing relative artifact](../missing.json)
                [Missing root artifact](/missing.json)
                [Outside relative artifact](../../outside.json)
                [Outside root artifact](/../outside.json)
                """);

        var bundle = KnowledgeBundleLoader.load(rootDirectory);
        var rule = new BrokenLinkRule();
        var issues = bundle.markdownFiles()
                .flatMap(markdownFile -> rule.validate(bundle, markdownFile).stream())
                .toList();

        assertThat(issues).extracting(ValidationIssue::message).containsExactly(
                "File notes/detail.md contains a broken link: /missing.md",
                "File notes/detail.md contains a broken link: ../missing.json",
                "File notes/detail.md contains a broken link: /missing.json",
                "File notes/detail.md contains a broken link: ../../outside.json",
                "File notes/detail.md contains a broken link: /../outside.json"
        );
    }

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
                        .flatMap(markdownFile -> rule.validate(bundle, markdownFile).stream()))
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
