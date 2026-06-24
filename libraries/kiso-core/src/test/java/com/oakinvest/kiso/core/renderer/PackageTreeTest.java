package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;
import com.oakinvest.kiso.core.renderer.model.PackageTree;
import com.oakinvest.kiso.core.renderer.model.PackageTreePage;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class PackageTreeTest extends BaseTest {

    @Test
    @DisplayName("Create package tree from bundle")
    void fromBundle() throws URISyntaxException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var bundle = new KnowledgeBundleLoader().load(resourcePath);
        var packageTree = PackageTree.fromBundle(bundle.rootBundle());

        // Root package ================================================================================================
        assertThat(packageTree)
                .returns("Index", PackageTree::name)
                .returns(Path.of(""), PackageTree::relativePath)
                .returns("index.html", PackageTree::href)
                .returns(true, PackageTree::root);
        assertThat(packageTree.childPackages())
                .extracting(PackageTree::name)
                .containsExactly("datasets", "references", "tables");
        assertThat(packageTree.pages())
                .hasSize(1)
                .first()
                .returns("index.md", PackageTreePage::title)
                .returns("index.md", PackageTreePage::fileName)
                .returns(Path.of("index.md"), PackageTreePage::relativePath)
                .returns("index.html", PackageTreePage::href)
                .returns(MarkdownFileKind.INDEX, PackageTreePage::kind);

        // Direct child package ========================================================================================
        var datasetsPackage = packageTree.childPackages().getFirst();
        assertThat(datasetsPackage)
                .returns("datasets", PackageTree::name)
                .returns(Path.of("datasets"), PackageTree::relativePath)
                .returns("datasets/index.html", PackageTree::href)
                .returns(false, PackageTree::root);
        assertThat(datasetsPackage.pages())
                .extracting(PackageTreePage::href)
                .containsExactly("datasets/ga4_obfuscated_sample_ecommerce.html", "datasets/index.html");

        // Nested package ==============================================================================================
        var referencesPackage = packageTree.childPackages().get(1);
        assertThat(referencesPackage.childPackages())
                .extracting(PackageTree::href)
                .containsExactly("references/joins/index.html", "references/metrics/index.html");
        var metricsPackage = referencesPackage.childPackages().get(1);
        assertThat(metricsPackage)
                .returns("metrics", PackageTree::name)
                .returns(Path.of("references/metrics"), PackageTree::relativePath)
                .returns("references/metrics/index.html", PackageTree::href);
        assertThat(metricsPackage.pages())
                .extracting(PackageTreePage::href)
                .contains("references/metrics/avg_pageviews.html", "references/metrics/index.html");
    }

}
