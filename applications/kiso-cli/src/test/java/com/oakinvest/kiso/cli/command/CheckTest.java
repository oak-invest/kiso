package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Check command")
public class CheckTest extends BaseTest {

    @Test
    @DisplayName("Check a valid OKF bundle")
    void checkValidBundle() {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE);

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new CheckCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toAbsolutePath().toString()
                );

        // Checking the results ========================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .doesNotContain("WARNING")
                .contains("Running check command")
                .contains("No errors found.");
    }

    @Test
    @DisplayName("Check an invalid OKF bundle")
    void checkInvalidBundle(@TempDir Path temporaryDirectory) throws Exception {
        // What we are testing =========================================================================================
        var sourceDirectory = temporaryDirectory.resolve("source");
        Files.createDirectories(sourceDirectory);
        var testDirectory = sourceDirectory.resolve("test");
        Files.createDirectories(testDirectory);

        // Invalid encoding for two files
        var file1 = sourceDirectory.resolve("invalid-encoding-1.md");
        Files.write(file1, new byte[]{(byte) 0xC3, (byte) 0x28});
        var file2 = testDirectory.resolve("invalid-encoding-2.md");
        Files.write(file2, new byte[]{(byte) 0xC3, (byte) 0x28});

        // Files with missing frontmatter or missing type field in frontmatter field
        var file3 = sourceDirectory.resolve("missing-frontmatter.md");
        Files.writeString(file3, "This file has no frontmatter.");
        var file4 = sourceDirectory.resolve("missing-frontmatter-type.md");
        Files.writeString(file4, """
                ---
                title: Missing frontmatter type
                description: This file has frontmatter but is missing the type field.
                ---
                This file has frontmatter but is missing the type field.""");

        // File with invalid date.
        var file5 = testDirectory.resolve("invalid-timestamp.md");
        Files.writeString(file5, """
                ---
                type: Test
                title: Invalid timestamp
                description: This file has frontmatter but an invalid timestamp.
                timestamp: 2026/07/02 14:30:00
                ---
                This file has frontmatter but an invalid timestamp.""");

        // index.md with frontmatter.
        var file6 = testDirectory.resolve("index.md");
        Files.writeString(file6, """
                ---
                type: Index
                ---
                This index file has frontmatter, which is not allowed.""");

        // log.md with frontmatter
        var file7 = testDirectory.resolve("log.md");
        Files.writeString(file7, """
                ---
                type: Log
                ---
                This index file has frontmatter, which is not allowed.""");

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new CheckCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute("--source", sourceDirectory.toAbsolutePath().toString());

        // Checking the results ========================================================================================
        assertThat(exitCode).isNotZero();
        assertThat(output.toString())
                .contains("Running check command")
                .doesNotContain("No errors found.");
        assertThat(error.toString())
                // invalid-encoding-1.md
                .contains("ERROR - INVALID_ENCODING - File invalid-encoding-1.md is not valid UTF-8 encoded")
                .contains("ERROR - MISSING_FRONTMATTER_TYPE - File invalid-encoding-1.md is missing mandatory 'type' in frontmatter")
                // test/invalid-encoding-2.md
                .contains("ERROR - INVALID_ENCODING - File test/invalid-encoding-2.md is not valid UTF-8 encoded")
                .contains("ERROR - MISSING_FRONTMATTER_TYPE - File test/invalid-encoding-2.md is missing mandatory 'type' in frontmatter")
                // missing-frontmatter.md
                .contains("ERROR - MISSING_FRONTMATTER - File missing-frontmatter.md is missing mandatory frontmatter")
                // missing-frontmatter-type.md
                .contains("ERROR - MISSING_FRONTMATTER_TYPE - File missing-frontmatter-type.md is missing mandatory 'type' in frontmatter")
                // test/invalid-timestamp.md
                .contains("ERROR - INVALID_TIMESTAMP - File test/invalid-timestamp.md has invalid 'timestamp' in frontmatter. It must be in ISO 8601 datetime format")
                // test/index.md
                .contains("ERROR - UNEXPECTED_FRONTMATTER - File test/index.md is not a concept file and should not contain frontmatter")
                // test/log.md
                .contains("ERROR - UNEXPECTED_FRONTMATTER - File test/log.md is not a concept file and should not contain frontmatter");
    }


    @Test
    @DisplayName("Check a valid OKF bundle with ignore patterns")
    void checkValidBundleWithIgnorePatterns() {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath("kb-with-ignore-patterns");

        // We execute the command without profile ======================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new CheckCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toAbsolutePath().toString()
                );

        // Checking the results ========================================================================================
        assertThat(exitCode).isNotZero();
        assertThat(output.toString())
                .contains("Running check command")
                .doesNotContain("No errors found.");
        assertThat(error.toString())
                .contains("ERROR - MISSING_FRONTMATTER_TYPE - File directoryWithError/test2.md is missing mandatory 'type' in frontmatter");

        // We execute the command without profile including ignore patterns ============================================
        output = new StringWriter();
        error = new StringWriter();
        exitCode = new CommandLine(new CheckCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toAbsolutePath().toString(),
                        "--profile", "profile-with-ignore-patterns"
                );

        // Checking the results ========================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("Running check command")
                .contains("No errors found.");
    }

    @Test
    @DisplayName("Check a bundle with broken links")
    void checkWithBrokenLinks() {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath("kb-with-broken-links");

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new CheckCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toAbsolutePath().toString()
                );

        // Checking the results ========================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("WARNING - BROKEN_LINK - File index.md contains broken link: uknownContent.md")
                .contains("Running check command")
                .contains("No errors found.");
    }

}
