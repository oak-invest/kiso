package com.oakinvest.kiso.cli.v0_2.renderer;

import com.oakinvest.kiso.cli.model.navigation.BundleTree;
import com.oakinvest.kiso.cli.model.navigation.BundleTreePage;
import com.oakinvest.kiso.cli.util.BaseTest;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.types.MarkdownFileKind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.contants.OKFConstants.ROOT_BUNDLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - Bundle tree")
public class BundleTreeTest extends BaseTest {

    @Test
    @DisplayName("Create a bundle tree from bundle")
    void fromBundle() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var bundle = KnowledgeBundleLoader.load(resourcePath);
        var bundleTree = BundleTree.fromBundle(bundle.rootBundle());

        // Root bundle =================================================================================================
        assertThat(bundleTree)
                .returns(ROOT_BUNDLE_NAME, BundleTree::name)
                .returns(Path.of(""), BundleTree::relativePath)
                .returns(true, BundleTree::isRoot);
        assertThat(bundleTree.indexPage()).map(BundleTreePage::htmlPath).hasValue("index.html");
        assertThat(bundleTree.childBundles())
                .extracting(BundleTree::name)
                .containsExactly("datasets", "references", "tables");
        assertThat(bundleTree.pages())
                .hasSize(1)
                .first()
                .returns("index.md", BundleTreePage::title)
                .returns("index.md", BundleTreePage::fileName)
                .returns(Path.of("index.md"), BundleTreePage::relativePath)
                .returns("index.html", BundleTreePage::htmlPath)
                .returns(MarkdownFileKind.INDEX, BundleTreePage::kind);

        // Direct child bundle =========================================================================================
        var datasetsBundle = bundleTree.childBundles().getFirst();
        assertThat(datasetsBundle)
                .returns("datasets", BundleTree::name)
                .returns(Path.of("datasets"), BundleTree::relativePath)
                .returns(false, BundleTree::isRoot);
        assertThat(datasetsBundle.indexPage()).map(BundleTreePage::htmlPath).hasValue("datasets/index.html");
        assertThat(datasetsBundle.pages())
                .extracting(BundleTreePage::htmlPath)
                .containsExactly("datasets/ga4_obfuscated_sample_ecommerce.html", "datasets/index.html");

        // Nested bundle ===============================================================================================
        var referencesBundle = bundleTree.childBundles().get(1);
        assertThat(referencesBundle.childBundles())
                .extracting(bundleNode -> bundleNode.indexPage().orElseThrow().htmlPath())
                .containsExactly("references/metrics/index.html");
        var metricsBundle = referencesBundle.childBundles().getFirst();
        assertThat(metricsBundle)
                .returns("metrics", BundleTree::name)
                .returns(Path.of("references/metrics"), BundleTree::relativePath);
        assertThat(metricsBundle.pages())
                .extracting(BundleTreePage::htmlPath)
                .contains("references/metrics/acquired_users.html",
                        "references/metrics/frequently_active_users.html",
                        "references/metrics/google_acquired_cohorts.html",
                        "references/metrics/highly_active_users.html",
                        "references/metrics/index.html",
                        "references/metrics/n_day_active_users.html",
                        "references/metrics/n_day_inactive_users.html",
                        "references/metrics/purchasers.html");
    }

}
