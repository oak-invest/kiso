package com.oakinvest.kiso.core.model.html.navigation;

import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Calculated bundle tree used to build navigation menus.
 *
 * @param name          bundle name displayed in navigation
 * @param relativePath  bundle relative path to the generated site root
 * @param indexHtmlPath generated HTML path of the bundle index page, relative to the generated site root
 * @param childBundles  direct child bundles
 * @param pages         direct Markdown pages in this bundle
 */
@Builder
@SuppressWarnings("unused")
public record BundleTree(
        String name,
        Path relativePath,
        String indexHtmlPath,
        List<BundleTree> childBundles,
        List<BundleTreePage> pages
) {

    /**
     * Creates a bundle tree from a loaded bundle directory.
     *
     * @param bundle loaded bundle directory
     * @return bundle tree
     */
    public static BundleTree fromBundle(final Bundle bundle) {
        Objects.requireNonNull(bundle, "Bundle must not be null");

        // Loading objects =============================================================================================
        List<BundleTree> childBundles = bundle.childBundles().stream()
                .map(BundleTree::fromBundle)
                .filter(BundleTree::hasContent)
                .toList();
        List<BundleTreePage> pages = bundle.markdownFiles().stream()
                .map(BundleTreePage::fromMarkdownFile)
                .toList();

        // Bundle name in the tree =====================================================================================
        String bundleNameInTree = bundle.name();
        if (bundle.relativePath() != null && StringUtils.isNotBlank(bundle.relativePath().toString())) {
            bundleNameInTree = bundle.relativePath().getFileName().toString();
        }

        // Return the bundle tree ======================================================================================
        return BundleTree.builder()
                .name(bundleNameInTree)
                .relativePath(bundle.relativePath())
                .indexHtmlPath(indexHref(bundle.relativePath()))
                .childBundles(childBundles)
                .pages(pages)
                .build();
    }

    /**
     * Returns the generated index link for a bundle.
     *
     * @param relativePath bundle absolutePath relative to the generated site isRoot
     * @return HTML index link
     */
    private static String indexHref(final Path relativePath) {
        if (relativePath == null || relativePath.toString().isBlank()) {
            return "index.html";
        }
        return relativePath.resolve("index.html").toString().replace('\\', '/');
    }

    /**
     * Returns true when this node is the root bundle.
     *
     * @return {@code true} for the root bundle
     */
    public boolean isRoot() {
        return relativePath == null || StringUtils.isBlank(relativePath().toString());
    }

    /**
     * Returns true when this bundle or one of its descendants has pages to display.
     *
     * @return {@code true} when the bundle should appear in navigation
     */
    public boolean hasContent() {
        return !pages.isEmpty() || !childBundles.isEmpty();
    }

    /**
     * Returns true when this bundle contains an index page.
     *
     * @return {@code true} when an index page exists
     */
    public boolean hasIndexPage() {
        return pages.stream().anyMatch(page -> page.kind() == MarkdownFileKind.INDEX);
    }

    /**
     * Returns true when this bundle contains the given HTML absolutePath.
     *
     * @param htmlPath HTML absolutePath relative to the generated site isRoot
     * @return {@code true} when this bundle contains the absolutePath
     */
    public boolean containsHtmlPath(final String htmlPath) {
        if (htmlPath == null) {
            return false;
        }
        return indexHtmlPath.equals(htmlPath)
                || pages.stream().anyMatch(page -> page.href().equals(htmlPath))
                || childBundles.stream().anyMatch(childBundle -> childBundle.containsHtmlPath(htmlPath));
    }

}
