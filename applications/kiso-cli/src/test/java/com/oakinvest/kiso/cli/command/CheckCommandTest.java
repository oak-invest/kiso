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

        // Invalid encoding for two files.
        Path invalidMarkdownFilePath1 = sourceDirectory.resolve("invalid-encoding-1.md");
        Files.write(invalidMarkdownFilePath1, new byte[]{(byte) 0xC3, (byte) 0x28});

        Path invalidMarkdownFilePath2 = testDirectory.resolve("invalid-encoding-2.md");
        Files.write(invalidMarkdownFilePath2, new byte[]{(byte) 0xC3, (byte) 0x28});

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
                .contains("ERROR - INVALID_ENCODING - File invalid-encoding-1.md is not valid UTF-8 encoded")
                .contains("ERROR - INVALID_ENCODING - File test/invalid-encoding-2.md is not valid UTF-8 encoded");

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
