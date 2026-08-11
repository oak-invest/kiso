package com.oakinvest.kiso.cli.v0_2.command;

import com.oakinvest.kiso.cli.command.BuildCommand;
import com.oakinvest.kiso.cli.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.FileConstants.DEFAULT_DESTINATION_DIRECTORY_NAME;
import static com.oakinvest.kiso.core.util.MarkdownFileKind.INDEX;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - Build Command with index generation")
public class BuildWithIndexTest extends BaseTest {

    @Test
    @DisplayName("Index generation test")
    void indexGenerationTest(@TempDir Path temporaryDirectory) throws IOException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath("kb-without-index");
        var destinationDirectory = temporaryDirectory.resolve(DEFAULT_DESTINATION_DIRECTORY_NAME);

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString()
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .doesNotContain("WARNING")
                .contains("Running build command")
                .contains("Done!");

        // index.md.
        assertThat(Files.readString(destinationDirectory.resolve(INDEX.getFileName()), UTF_8))
                .contains("## Content")
                .contains("- [test1.md](test1.md): test1.md")
                .contains("- [test2.md](test2.md): test2.md")
                // Subdirectories.
                .contains("## Subdirectories")
                .contains("[directory1](directory1/index.md)")
                .contains("[directory2](directory2/index.md)");

        // directory1/index.md.
        assertThat(Files.readString(destinationDirectory.resolve("directory1/" + INDEX.getFileName()), UTF_8))
                // Content.
                .contains("## Content")
                .contains("- [My title](test10.md): My description")
                // no subdirectories.
                .doesNotContain("## Subdirectories");

        // directory2/index.md.
        assertThat(Files.readString(destinationDirectory.resolve("directory2/" + INDEX.getFileName()), UTF_8))
                // Content.
                .contains("## Content")
                .contains("- [subtest1.md](subtest1.md): directory2/subtest1.md")
                .contains("- [My title for subtest2](subtest2.md): My description for subtest2")
                .contains("- [subtest3.md](subtest3.md): directory2/subtest3.md")
                .contains("- [test20.md](test20.md): directory2/test20.md")
                // Subdirectories.
                .contains("## Subdirectories")
                .contains("[directory2/subdirectory1](subdirectory1/index.md)")
                .contains("[directory2/subdirectory2](subdirectory2/index.md)")
                .contains("[directory2/subdirectory3](subdirectory3/index.md)")
                .doesNotContain("[directory2/subdirectory3](subdirectory4/index.md)");

        // directory2/subdirectory1/index.md.
        assertThat(Files.readString(destinationDirectory.resolve("directory2/subdirectory1/" + INDEX.getFileName()), UTF_8))
                .contains("Specific")
                // Content.
                .doesNotContain("## Content")
                // no subdirectories.
                .doesNotContain("## Subdirectories");

        // directory2/subdirectory2/index.md.
        assertThat(Files.readString(destinationDirectory.resolve("directory2/subdirectory2/" + INDEX.getFileName()), UTF_8))
                // Content.
                .contains("## Content")
                .contains("- [test22.md](test22.md): directory2/subdirectory2/test22.md")
                // no subdirectories.
                .doesNotContain("## Subdirectories");
    }

    @Test
    @DisplayName("Index with profile without Index")
    void indexWithProfileWithoutIndex(@TempDir Path temporaryDirectory) throws IOException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath("kb-without-index");
        var destinationDirectory = temporaryDirectory.resolve("public");

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString(),
                        "--profile", "profile-without-index"
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("WARNING: Theme 'unknown!' is not a valid DaisyUI theme.")
                .contains("Running build command")
                .contains("Done!");

        // index.md.
        assertThat(Files.readString(destinationDirectory.resolve(INDEX.getFileName()), UTF_8))
                .contains("## Content")
                .contains("- [test1.md](test1.md): test1.md")
                .contains("- [test2.md](test2.md): test2.md")
                // Subdirectories.
                .contains("## Subdirectories")
                .contains("[directory1](directory1/index.md)")
                .contains("[directory2](directory2/index.md)");
    }

    @Test
    @DisplayName("Index with profile with Index")
    void indexWithProfileWithIndex(@TempDir Path temporaryDirectory) throws IOException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath("kb-without-index");
        var destinationDirectory = temporaryDirectory.resolve("public");

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString(),
                        "--profile", "profile-with-index"
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("WARNING: Theme 'unknown!' is not a valid DaisyUI theme.")
                .contains("Running build command")
                .contains("Done!");

        // index.md.
        assertThat(Files.readString(destinationDirectory.resolve(INDEX.getFileName()), UTF_8))
                .contains("This is the specific index!")
                .doesNotContain("## Content")
                .doesNotContain("- [test1.md](test1.md): test1.md")
                .doesNotContain("- [test2.md](test2.md): test2.md")
                // Subdirectories.
                .doesNotContain("## Subdirectories")
                .doesNotContain("[directory1](directory1/index.md)")
                .doesNotContain("[directory2](directory2/index.md)");
    }

}
