package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.BaseTest;
import com.oakinvest.kiso.core.validation.rule.EncodingRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_ENCODING;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Encoding rule")
class EncodingRuleTest extends BaseTest {

    @Test
    @DisplayName("Report a concept file that is not valid UTF-8")
    void encodingRule(@TempDir Path temporaryDirectory) throws IOException {
        // Create markdown files with invalid UTF-8 encoding ===========================================================

        // First file
        Path invalidMarkdownFilePath1 = temporaryDirectory.resolve("invalid-encoding-1.md");
        Files.write(invalidMarkdownFilePath1, new byte[]{(byte) 0xC3, (byte) 0x28});

        // Second file in a subdirectory
        Path testSubDirectory = temporaryDirectory.resolve("test");
        Files.createDirectories(testSubDirectory);
        Path invalidMarkdownFilePath2 = testSubDirectory.resolve("invalid-encoding-2.md");
        Files.write(invalidMarkdownFilePath2, new byte[]{(byte) 0xC3, (byte) 0x28});

        // Create a knowledge bundle with the invalid file =============================================================
        var invalidMarkdownFile1 = MarkdownFile.builder()
                .fileName(invalidMarkdownFilePath1.getFileName().toString())
                .kind(CONCEPT)
                .absolutePath(invalidMarkdownFilePath1)
                .relativePath(invalidMarkdownFilePath1)
                .build();
        var invalidMarkdownFile2 = MarkdownFile.builder()
                .fileName(invalidMarkdownFilePath2.getFileName().toString())
                .kind(CONCEPT)
                .absolutePath(invalidMarkdownFilePath2)
                .relativePath(invalidMarkdownFilePath2)
                .build();
        var rootBundle = bundleWith(List.of(invalidMarkdownFile1, invalidMarkdownFile2));

        // Run validation and check that the invalid file is reported ==================================================
        var rule = new EncodingRule();
        assertThat(rule.validate(rootBundle, invalidMarkdownFile1)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_ENCODING);
            assertThat(issue.message()).endsWith("invalid-encoding-1.md is not valid UTF-8 encoded");
            assertThat(issue.path()).isEqualTo(invalidMarkdownFilePath1);
        });

        assertThat(rule.validate(rootBundle, invalidMarkdownFile2)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_ENCODING);
            assertThat(issue.message()).endsWith("test/invalid-encoding-2.md is not valid UTF-8 encoded");
            assertThat(issue.path()).isEqualTo(invalidMarkdownFilePath2);
        });
    }

}
