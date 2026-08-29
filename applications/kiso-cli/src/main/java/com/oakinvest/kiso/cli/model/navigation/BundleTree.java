package com.oakinvest.kiso.cli.model.navigation;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX;

/**
 * Calculated bundle tree used to build navigation menus.
 * Example for the bundle tree 'references/metrics':
 * - bundleTree.Name = metrics
 * - bundleTree.relativePath = references/metrics
 *
 * @param name         bundle name displayed in navigation
 * @param relativePath relative path to the bundle directory relative to the generated site root
 * @param childBundles direct child bundles
 * @param pages        direct Markdown pages
 */
@Builder
@SuppressWarnings("unused")
public record BundleTree(
        String name,
        Path relativePath,
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
        // For a child bundle such as references/metrics, it uses the last directory name, metrics. This ensures that
        // each child bundle is displayed with its local directory name in the navigation menu.
        // getFileName() returns the last element of the path
        String bundleNameInTree = bundle.name();
        if (bundle.relativePath() != null && StringUtils.isNotBlank(bundle.relativePath().toString())) {
            bundleNameInTree = bundle.relativePath().getFileName().toString();
        }

        // Return the bundle tree ======================================================================================
        return BundleTree.builder()
                .name(bundleNameInTree)
                .relativePath(bundle.relativePath())
                .childBundles(childBundles)
                .pages(pages)
                .build();
    }

    /**
     * Returns an empty bundle tree.
     *
     * @return empty bundle tree
     */
    public static BundleTree empty() {
        return BundleTree.builder()
                .childBundles(List.of())
                .pages(List.of())
                .build();
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
     * Returns the bundle index page when it exists.
     *
     * @return bundle index page
     */
    public Optional<BundleTreePage> indexPage() {
        return pages.stream()
                .filter(page -> page.kind() == INDEX)
                .findFirst();
    }

    /**
     * Checks whether an HTML page belongs to the bundle or any of its child bundles.
     * For example, if the current page is references/metrics/active-users.html, the method returns true for both
     * references and metrics, but false for unrelated bundles such as datasets. This allows the navigation menu to
     * automatically open the branch containing the current page
     *
     * @param htmlPath HTML absolutePath relative to the generated site isRoot
     * @return {@code true} when this bundle exists the absolutePath
     */
    public boolean containsHtmlPath(@Nullable final String htmlPath) {
        if (htmlPath == null) {
            return false;
        }
        return pages.stream().anyMatch(page -> page.htmlPath().equalsIgnoreCase(htmlPath))
                || childBundles.stream().anyMatch(childBundle -> childBundle.containsHtmlPath(htmlPath));
    }

}
