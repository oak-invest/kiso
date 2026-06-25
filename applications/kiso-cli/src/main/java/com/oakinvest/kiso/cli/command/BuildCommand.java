package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.options.DestinationOption;
import com.oakinvest.kiso.cli.options.SourceOption;
import com.oakinvest.kiso.core.loading.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.publishing.LlmsTxtGenerator;
import com.oakinvest.kiso.core.publishing.SitemapXmlGenerator;
import com.oakinvest.kiso.core.rendering.MarkdownToHtmlRenderer;
import com.oakinvest.kiso.core.rendering.model.navigation.BundleTree;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.oakinvest.kiso.core.util.FileNamesConstants.LLMS_TXT_FILENAME;
import static com.oakinvest.kiso.core.util.FileNamesConstants.SITEMAP_XML_FILENAME;

/**
 * Build: Generates a static website from an OKF bundle, including the original Markdown files, generated HTML pages, llms.txt, and sitemap.xml.
 */
@CommandLine.Command(
        name = "build",
        mixinStandardHelpOptions = true,
        description = "Generates a static website from an OKF bundle, including the original Markdown files, generated HTML pages, llms.txt, and sitemap.xml"
)
public class BuildCommand implements Runnable {

    /** Static assets copied to the generated website isRoot. */
    private static final String[] ASSET_PATHS = {
            "assets/css/daisyui@5.css",
            "assets/css/themes.css",
            "assets/css/application.css",
            "assets/js/browser@4.js"
    };

    /** Source directory. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** Destination directory. */
    @CommandLine.Mixin
    private final DestinationOption destinationOption = new DestinationOption();

    /** Command spec. */
    @CommandLine.Spec
    @SuppressWarnings("unused")
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
            print("Kiso-cli - Running build command");
            print("Sources in " + sourceDirectory.getAbsolutePath());
            print("Building in " + destinationDirectory.getAbsolutePath());
            blankLine();

            // Copying files ===========================================================================================
            FileUtils.deleteDirectory(destinationDirectory);
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);

            // HTML generation =========================================================================================
            final KnowledgeBundle knowledgeBundle = KnowledgeBundleLoader.load(destinationDirectory.toPath());
            final BundleTree bundleTree = BundleTree.fromBundle(knowledgeBundle.rootBundle());
            knowledgeBundle.bundles()
                    .forEach(bundle -> {
                        // We generate the HTML version of every Markdown file =========================================
                        bundle.markdownFiles().forEach(markdownFile -> {
                            try {
                                FileUtils.writeStringToFile(
                                        new File(bundle.absolutePath().toString(), markdownFile.htmlFileName()),
                                        MarkdownToHtmlRenderer.render(markdownFile, bundleTree),
                                        StandardCharsets.UTF_8
                                );
                                print("HTML Generated for " + markdownFile.relativePath());
                            } catch (IOException e) {
                                printError("Error generating HTML for " + markdownFile.absolutePath() + ": " + e.getMessage());
                            }
                        });

                    });

            // llms.txt generation =====================================================================================
            FileUtils.writeStringToFile(
                    new File(knowledgeBundle.rootBundle().absolutePath().toString(), LLMS_TXT_FILENAME),
                    LlmsTxtGenerator.generate(knowledgeBundle),
                    StandardCharsets.UTF_8
            );
            print("File llms.txt generated");

            // sitemap.xml generation ==================================================================================
            FileUtils.writeStringToFile(
                    new File(knowledgeBundle.rootBundle().absolutePath().toString(), SITEMAP_XML_FILENAME),
                    SitemapXmlGenerator.generate(knowledgeBundle),
                    StandardCharsets.UTF_8
            );
            print("File sitemap.xml generated");

            // Add HTML assets =========================================================================================
            copyKisoAssets(destinationDirectory);

            // Job done ================================================================================================
            print("Done!");
        } catch (IOException e) {
            printError("Error: " + e.getMessage());
            commandSpec.exitCodeOnInvalidInput();
        }
    }

    /**
     * Print message in console.
     *
     * @param message message to print
     */
    private void print(final String message) {
        commandSpec.commandLine().getOut().println(message);
    }

    /**
     * Blank line in console.
     */
    private void blankLine() {
        commandSpec.commandLine().getOut().println();
    }

    /**
     * Print error message in console.
     *
     * @param message message to print
     */
    private void printError(final String message) {
        commandSpec.commandLine().getErr().println(message);
    }

    /**
     * Copies Kiso static assets to the generated website isRoot.
     *
     * @param destinationDirectory generated website isRoot
     * @throws IOException if an asset cannot be copied
     */
    private void copyKisoAssets(final File destinationDirectory) throws IOException {
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
