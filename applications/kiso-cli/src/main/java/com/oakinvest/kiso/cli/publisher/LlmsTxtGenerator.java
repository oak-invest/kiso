package com.oakinvest.kiso.cli.publisher;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import lombok.experimental.UtilityClass;
import org.commonmark.node.BulletList;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Text;
import org.commonmark.renderer.markdown.MarkdownRenderer;

import java.util.Comparator;
import java.util.Objects;

import static com.oakinvest.kiso.core.tools.MarkdownFactory.markdownFileListItem;
import static com.oakinvest.kiso.core.util.contants.FileConstants.TAGS_DIRECTORY_NAME;
import static com.oakinvest.kiso.core.util.contants.MarkdownConstants.HEADING_LEVEL_1;
import static com.oakinvest.kiso.core.util.contants.MarkdownConstants.HEADING_LEVEL_2;
import static com.oakinvest.kiso.core.util.contants.OKFConstants.DEFAULT_TITLE;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX;

/**
 * Generator for the llms.txt file.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class LlmsTxtGenerator {

    /**
     * Generates llms.txt content for a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return llms.txt content
     */
    public static String generate(final KnowledgeBundle knowledgeBundle) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");

        // Document creation ===========================================================================================
        final Document llmsTxt = new Document();
        llmsTxt.appendChild(heading(HEADING_LEVEL_1, DEFAULT_TITLE));

        // Each bundle section =========================================================================================
        knowledgeBundle.bundles()
                // Do not add a bundle with no child and no file.
                .filter(bundle -> !bundle.isEmpty())
                // Do not add tags.
                .filter(bundle -> !bundle.name().equalsIgnoreCase(TAGS_DIRECTORY_NAME))
                .forEach(bundle -> {

                    // Bundle name =====================================================================================
                    llmsTxt.appendChild(heading(HEADING_LEVEL_2, bundle.name()));

                    // Pages inside the bundle =========================================================================
                    BulletList list = new BulletList();
                    list.setMarker("-");
                    list.setTight(true);
                    bundle.markdownFiles().stream()
                            .sorted(Comparator.comparing(markdownFile -> markdownFile.kind() != INDEX))
                            .forEach(markdownFile -> list.appendChild(markdownFileListItem(
                                    knowledgeBundle.siteConfiguration().normalizedBaseUrl(),
                                    markdownFile)));
                    llmsTxt.appendChild(list);

                });

        // Return generated file =======================================================================================
        return MarkdownRenderer.builder()
                .build()
                .render(llmsTxt);
    }

    /**
     * Creates a heading.
     *
     * @param level heading level
     * @param text  heading text
     * @return heading
     */
    private static Heading heading(final int level, final String text) {
        Heading heading = new Heading();
        heading.setLevel(level);
        heading.appendChild(new Text(text));
        return heading;
    }

}
