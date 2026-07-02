package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CheckCommandTest {

    /** Knowledge base Google example directory. */
    public static final String KB_GOOGLE_EXAMPLE_DIRECTORY = "kb-google-example-v0.1";

    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Check a valid OKF bundle")
    void checkValidBundle() throws Exception {
        // What we are testing - The google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // Executing the check command =================================================================================
        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        int exitCode = new CommandLine(new CheckCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toAbsolutePath().toString()
                );

        // Checking the results ========================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("Kiso-cli - Running check command")
                .contains("No errors found.");
    }

    @Test
    @DisplayName("Check an invalid OKF bundle")
    void checkInvalidBundle() throws Exception {
        // What we are testing =========================================================================================
        Path sourceDirectory = temporaryDirectory.resolve("source");
        Files.createDirectories(sourceDirectory);
        Path testDirectory = sourceDirectory.resolve("test");
        Files.createDirectories(testDirectory);

        // Invalid encoding for two files
        Path file1 = sourceDirectory.resolve("invalid-encoding-1.md");
        Files.write(file1, new byte[]{(byte) 0xC3, (byte) 0x28});
        Path file2 = testDirectory.resolve("invalid-encoding-2.md");
        Files.write(file2, new byte[]{(byte) 0xC3, (byte) 0x28});

        // Files with missing frontmatter or missing type field in frontmatter field
        Path file3 = sourceDirectory.resolve("missing-frontmatter.md");
        Files.writeString(file3, "This file has no frontmatter.");
        Path file4 = sourceDirectory.resolve("missing-frontmatter-type.md");
        Files.writeString(file4, """
                ---
                title: Missing frontmatter type
                description: This file has frontmatter but is missing the type field.
                ---
                This file has frontmatter but is missing the type field.""");

        // File with invalid date.
        Path file5 = testDirectory.resolve("invalid-timestamp.md");
        Files.writeString(file5, """
                ---
                type: Test
                title: Invalid timestamp
                description: This file has frontmatter but an invalid timestamp.
                timestamp: 2026/07/02 14:30:00
                ---
                This file has frontmatter but an invalid timestamp.""");

        // Executing the check command =================================================================================
        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        int exitCode = new CommandLine(new CheckCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", sourceDirectory.toAbsolutePath().toString()
                );

        // Checking the results ========================================================================================
        assertThat(exitCode).isNotZero();
        assertThat(output.toString())
                .contains("Kiso-cli - Running check command")
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
                .contains("ERROR - INVALID_TIMESTAMP - File test/invalid-timestamp.md has invalid 'timestamp' in frontmatter. It must be in ISO 8601 datetime format");

//        System.out.println("STDOUT:");
//        System.out.println(output);
//        System.err.println("STDERR:");
//        System.err.println(error);
    }


    /**
     * Retrieves absolutePath from a resource fileName.
     *
     * @param resourceName resource fileName
     * @return absolutePath
     * @throws URISyntaxException syntax problem with URI
     */
    @SuppressWarnings("SameParameterValue")
    private Path getResourcePath(final String resourceName) throws URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Missing test resource: " + resourceName);
        return Path.of(resource.toURI());
    }

}
