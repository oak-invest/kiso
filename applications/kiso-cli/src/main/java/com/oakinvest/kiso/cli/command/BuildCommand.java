package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.util.command.DestinationOption;
import com.oakinvest.kiso.cli.util.command.SourceOption;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.renderer.engine.MarkdownToHtmlRenderer;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Build: Generates a static website from .md files, outputting HTML files alongside the original Markdown files.
 */
@CommandLine.Command(
        name = "build",
        mixinStandardHelpOptions = true,
        description = "Generates a static website from .md files"
)
public class BuildCommand implements Runnable {

    /** Static assets copied to the generated website root. */
    private static final String[] ASSET_PATHS = {
            "assets/css/daisyui@5.css",
            "assets/css/themes.css",
            "assets/css/application.css",
            "assets/js/browser@4.js"
    };

    /** Shared source directory option. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** Shared destination directory option. */
    @CommandLine.Mixin
    private final DestinationOption destinationOption = new DestinationOption();

    /** Command spec. */
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    /**
     * Run the build command.
     */
    @Override
    public void run() {
        try {
            // Displaying information about the process ================================================================
            final File sourceDirectory = sourceOption.sourceDirectory().toFile();
            final File destinationDirectory = destinationOption.destinationDirectory().toFile();
            commandSpec.commandLine().getOut().println("Kiso-cli - Build command");
            commandSpec.commandLine().getOut().println("Using sources in " + sourceDirectory.getAbsolutePath());
            commandSpec.commandLine().getOut().println("Building website in " + destinationDirectory.getAbsolutePath());

            // Copying files ===========================================================================================
            FileUtils.deleteDirectory(destinationDirectory);
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
            copyAssets(destinationDirectory);

            // Generating HTML =========================================================================================
            final MarkdownToHtmlRenderer markdownToHtmlRenderer = new MarkdownToHtmlRenderer();
            new KnowledgeBundleLoader().load(destinationDirectory.toPath()).bundles()
                    .forEach(bundle -> {
                        // We generate the HTML version of every Markdown file =========================================
                        bundle.markdownFiles().forEach(markdownFile -> {
                            try {
                                FileUtils.writeStringToFile(
                                        new File(bundle.path().toString(), markdownFile.htmlFileName()),
                                        markdownToHtmlRenderer.render(markdownFile),
                                        StandardCharsets.UTF_8
                                );
                                commandSpec.commandLine().getOut().println("HTML Generated for " + markdownFile.relativePath());
                            } catch (IOException e) {
                                commandSpec.commandLine().getErr().println("Error generating HTML for " + markdownFile.path() + ": " + e.getMessage());
                            }
                        });

                    });

            // Job done!
            commandSpec.commandLine().getOut().println("Done!");
        } catch (IOException e) {
            commandSpec.commandLine().getErr().println("Error: " + e.getMessage());
            commandSpec.exitCodeOnInvalidInput();
        }

    }

    /**
     * Copies Kiso static assets to the generated website root.
     *
     * @param destinationDirectory generated website root
     * @throws IOException if an asset cannot be copied
     */
    private void copyAssets(final File destinationDirectory) throws IOException {
        ClassLoader classLoader = MarkdownToHtmlRenderer.class.getClassLoader();
        for (String assetPath : ASSET_PATHS) {
            try (InputStream inputStream = classLoader.getResourceAsStream(assetPath)) {
                if (inputStream == null) {
                    throw new IOException("Missing asset: " + assetPath);
                }
                FileUtils.copyInputStreamToFile(inputStream, new File(destinationDirectory, assetPath));
            }
        }
    }

}
