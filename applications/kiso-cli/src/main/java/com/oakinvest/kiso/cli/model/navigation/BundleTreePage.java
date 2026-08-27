package com.oakinvest.kiso.cli.model.navigation;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.types.MarkdownFileKind;
import lombok.Builder;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A direct Markdown page inside a bundle tree node.
 * Example for the bundle tree page 'datasets/ga4_obfuscated_sample_ecommerce.md':
 * - bundleTreePage.fileName = ga4_obfuscated_sample_ecommerce.md
 * - bundleTreePage.relativePath = datasets/ga4_obfuscated_sample_ecommerce.md
 * - bundleTreePage.htmlPath = datasets/ga4_obfuscated_sample_ecommerce.html
 *
 * @param title        page title displayed in navigation
 * @param fileName     Markdown file name
 * @param relativePath relative path to the Markdown file relative to the generated site root
 * @param htmlPath     relative path to the generated HTML file relative to the generated site root
 * @param kind         semantic kind of the Markdown file
 */
@Builder
@SuppressWarnings("unused")
public record BundleTreePage(
        String title,
        String fileName,
        Path relativePath,
        String htmlPath,
        MarkdownFileKind kind
) {

    /**
     * Creates a navigation page from a Markdown file.
     *
     * @param markdownFile Markdown file
     * @return bundle tree page
     */
    public static BundleTreePage fromMarkdownFile(final MarkdownFile markdownFile) {
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");
        return BundleTreePage.builder()
                .title(markdownFile.title())
                .fileName(markdownFile.fileName())
                .relativePath(markdownFile.relativePath())
                .htmlPath(markdownFile.htmlFilePath())
                .kind(markdownFile.kind())
                .build();
    }

}
