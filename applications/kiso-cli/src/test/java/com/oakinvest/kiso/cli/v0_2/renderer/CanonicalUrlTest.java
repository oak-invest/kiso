package com.oakinvest.kiso.cli.v0_2.renderer;

import com.oakinvest.kiso.cli.model.navigation.BundleTree;
import com.oakinvest.kiso.cli.renderer.MarkdownToHtmlRenderer;
import com.oakinvest.kiso.cli.util.BaseTest;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - Canonical url test")
class CanonicalUrlTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com",
            "https://example.com/",
            "https://example.com/knowledge",
            "https://example.com/knowledge/",
            "http://example.com/knowledge"
    })
    @DisplayName("Testing different base URLs to ensure canonical and social URLs are generated correctly for every page")
    void rendersOneCanonicalMatchingSocialUrlForEveryPage(String baseUrl) {
        var bundle = KnowledgeBundleLoader.load(getResourcePath(KB_GOOGLE_V_0_2));
        var bundleTree = BundleTree.fromBundle(bundle.rootBundle());
        var configuration = SiteConfiguration.builder().baseUrl(baseUrl).build();

        bundle.bundles().flatMap(knowledgeBundle -> knowledgeBundle.markdownFiles().stream()).forEach(markdownFile -> {
            var page = Jsoup.parse(MarkdownToHtmlRenderer.render(configuration, ThemeConfiguration.empty(), markdownFile, bundleTree));
            var expectedUrl = configuration.normalizedBaseUrl() + markdownFile.htmlFilePath();
            assertThat(page.select("head link[rel=canonical]").eachAttr("href")).containsExactly(expectedUrl);
            assertThat(page.select("meta[property='og:url']").eachAttr("content")).containsExactly(expectedUrl);
        });
    }

}
