package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import com.oakinvest.kiso.core.validation.rule.BrokenLinkRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.oakinvest.kiso.core.validation.ValidationCode.BROKEN_LINK;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Broken link rule")
public class BrokenLinkRuleTest extends BaseTest {

    @Test
    @DisplayName("Report broken links")
    void reportBrokenLinks() {
        // Errors ======================================================================================================
        // dir1/content1.md                 --> Failed link to /uknownContent.md
        // dir1/dir1subdir1/content11.md    --> Failed link to /dir2/uknownContent.md
        // dir1/dir1subdir1/index.md        --> No error
        // dir1/dir1subdir2/content12.md    --> No error
        // --
        // dir2/dir2subdir1/content21.md    --> Failed link to /dir2/dir2subdir2/uknownContent.md
        // dir2/dir2subdir2/content22.md    --> Failed link to /uknownContent.md
        // dir2/dir2subdir2/index.md        --> Failed link to /dir2/dir2subdir3/uknownContent.md
        // dir2/dir2subdir3/content23.md    --> No error
        // --
        // index.md                         --> Failed link to /uknownContent.md & /dir2/uknownContent.md
        // content.md                       --> Failed link to /dir2/dir2subdir3/uknownContent.md

        // Loading bundle ==============================================================================================
        var resourcePath = getResourcePath("kb-with-broken-links");
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // Run validation and check that the warnings for broken links =================================================
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
                        "File content.md contains broken link: dir2/dir2subdir3/uknownContent.md",
                        "File index.md contains broken link: ./uknownContent.md",
                        "File index.md contains broken link: ./dir2/uknownContent.md",
                        "File dir1/content1.md contains broken link: ../uknownContent.md",
                        "File dir1/dir1subdir1/content11.md contains broken link: ../../dir2/uknownContent.md",
                        "File dir1/dir1subdir2/content12.md contains broken link: ../../dir2/dir2subdir2/uknownContent.md",
                        "File dir2/dir2subdir2/content22.md contains broken link: ../../dir2/dir2subdir3/uknownContent.md"
                );
    }

}
