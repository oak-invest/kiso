package com.oakinvest.kiso.core.renderer.model;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import lombok.Builder;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Calculated package tree used by renderer templates to build navigation menus.
 *
 * @param name          package name displayed in navigation
 * @param relativePath  package path relative to the generated site root
 * @param href          package index HTML link relative to the generated site root
 * @param childPackages direct child packages
 * @param pages         direct Markdown pages in this package
 */
@Builder
@SuppressWarnings("unused")
public record PackageTree(
        String name,
        Path relativePath,
        String href,
        List<PackageTree> childPackages,
        List<PackageTreePage> pages
) {

    /** Root package name used when the bundle root has no relative path. */
    private static final String ROOT_PACKAGE_NAME = "Index";

    /**
     * Creates a package tree from a loaded bundle directory.
     *
     * @param bundle loaded bundle directory
     * @return package tree
     */
    public static PackageTree fromBundle(final Bundle bundle) {
        Objects.requireNonNull(bundle, "bundle must not be null");

        List<PackageTree> childPackages = bundle.childBundleDirectories().stream()
                .map(PackageTree::fromBundle)
                .filter(PackageTree::hasNavigationContent)
                .toList();
        List<PackageTreePage> pages = bundle.markdownFiles().stream()
                .map(PackageTreePage::fromMarkdownFile)
                .toList();

        return PackageTree.builder()
                .name(packageName(bundle))
                .relativePath(bundle.relativePath())
                .href(indexHref(bundle.relativePath()))
                .childPackages(childPackages)
                .pages(pages)
                .build();
    }

    /**
     * Returns a display name for the package.
     *
     * @param bundle loaded bundle directory
     * @return package name
     */
    private static String packageName(final Bundle bundle) {
        Path relativePath = bundle.relativePath();
        if (relativePath == null || relativePath.toString().isBlank()) {
            return ROOT_PACKAGE_NAME;
        }
        return relativePath.getFileName().toString();
    }

    /**
     * Returns the generated index link for a package.
     *
     * @param relativePath package path relative to the generated site root
     * @return HTML index link
     */
    private static String indexHref(final Path relativePath) {
        if (relativePath == null || relativePath.toString().isBlank()) {
            return "index.html";
        }
        return relativePath.resolve("index.html").toString().replace('\\', '/');
    }

    /**
     * Returns true when this node is the root package.
     *
     * @return true for the root package
     */
    public boolean root() {
        return relativePath == null || relativePath.toString().isBlank();
    }

    /**
     * Returns true when this package or one of its descendants has pages to display.
     *
     * @return true when the package should appear in navigation
     */
    public boolean hasNavigationContent() {
        return !pages.isEmpty() || !childPackages.isEmpty();
    }

    /**
     * Returns true when this package contains the given HTML path.
     *
     * @param htmlPath HTML path relative to the generated site root
     * @return true when this package contains the path
     */
    public boolean containsHtmlPath(final String htmlPath) {
        if (htmlPath == null) {
            return false;
        }
        return href.equals(htmlPath)
                || pages.stream().anyMatch(page -> page.href().equals(htmlPath))
                || childPackages.stream().anyMatch(childPackage -> childPackage.containsHtmlPath(htmlPath));
    }

}
