package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_ENCODING;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

class EncodingRuleTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Report a concept file that is not valid UTF-8")
    void encodingRule() throws IOException {
        // Create markdown files with invalid UTF-8 encoding ==========================================================

        // First file
        Path invalidMarkdownFilePath1 = temporaryDirectory.resolve("invalid-encoding-1.md");
        Files.write(invalidMarkdownFilePath1, new byte[]{(byte) 0xC3, (byte) 0x28});

        // Second file in a subdirectory
        Path testSubDirectory = temporaryDirectory.resolve("test");
        Files.createDirectories(testSubDirectory);
        Path invalidMarkdownFilePath2 = testSubDirectory.resolve("invalid-encoding-2.md");
        Files.write(invalidMarkdownFilePath2, new byte[]{(byte) 0xC3, (byte) 0x28});

        // Create a knowledge bundle with the invalid file =============================================================
        MarkdownFile invalidMarkdownFile1 = MarkdownFile.builder()
                .fileName(invalidMarkdownFilePath1.getFileName().toString())
                .kind(CONCEPT)
                .absolutePath(invalidMarkdownFilePath1)
                .relativePath(invalidMarkdownFilePath1)
                .build();
        MarkdownFile invalidMarkdownFile2 = MarkdownFile.builder()
                .fileName(invalidMarkdownFilePath2.getFileName().toString())
                .kind(CONCEPT)
                .absolutePath(invalidMarkdownFilePath2)
                .relativePath(invalidMarkdownFilePath2)
                .build();
        Bundle rootBundle = Bundle.builder()
                .childBundles(List.of())
                .markdownFiles(List.of(invalidMarkdownFile1, invalidMarkdownFile2))
                .build();
        KnowledgeBundle knowledgeBundle = KnowledgeBundle.builder()
                .rootBundle(rootBundle)
                .build();

        // Run validation and check that the invalid file is reported ==================================================
        ValidationReport report = new ValidationRunner().runValidation(knowledgeBundle);
        assertThat(report.hasErrors()).isTrue();
        assertThat(report.issues()).hasSize(2);
        assertThat(report.issues()).satisfiesExactly(
                issue1 -> {
                    assertThat(issue1.severity()).isEqualTo(ERROR);
                    assertThat(issue1.code()).isEqualTo(INVALID_ENCODING);
                    assertThat(issue1.message()).endsWith("invalid-encoding-1.md is not valid UTF-8 encoded");
                    assertThat(issue1.path()).isEqualTo(invalidMarkdownFilePath1);
                },
                issue2 -> {
                    assertThat(issue2.severity()).isEqualTo(ERROR);
                    assertThat(issue2.code()).isEqualTo(INVALID_ENCODING);
                    assertThat(issue2.message()).endsWith("test/invalid-encoding-2.md is not valid UTF-8 encoded");
                    assertThat(issue2.path()).isEqualTo(invalidMarkdownFilePath2);
                });
    }

}
