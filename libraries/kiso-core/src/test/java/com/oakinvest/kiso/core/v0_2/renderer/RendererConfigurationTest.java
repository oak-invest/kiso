package com.oakinvest.kiso.core.v0_2.renderer;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.html.navigation.BundleTree;
import com.oakinvest.kiso.core.renderer.MarkdownToHtmlRenderer;
import com.oakinvest.kiso.core.util.BaseTest;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - Markdown to HTML with configuration")
public class RendererConfigurationTest extends BaseTest {

    @Test
    @DisplayName("Markdown to html with configuration")
    void markdownToHTML(@TempDir Path temporaryDirectory) throws IOException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2_WITH_CONFIGURATION);
        var bundle = KnowledgeBundleLoader.load(resourcePath);
        var bundleTree = BundleTree.fromBundle(bundle.rootBundle());

        // Configuration ===============================================================================================
        var siteConfiguration = new SiteConfiguration(
                "https://knowledge.angara.finance/",
                Locale.FRENCH,
                "Nom du site",
                "Titre du site",
                "Description du site");
        var themeConfiguration = new ThemeConfiguration(
                "Corporate ");

        // Index file of the bundle ====================================================================================
        var markdownFiles = bundle.rootBundle().markdownFiles();
        var page = Jsoup.parse(MarkdownToHtmlRenderer.render(siteConfiguration, themeConfiguration, markdownFiles.getFirst(), bundleTree));
        FileUtils.writeStringToFile(temporaryDirectory.resolve("test-index.html").toFile(), page.html(), UTF_8);

        // Assertions ==================================================================================================
        Element html = page.selectFirst("html");
        assertThat(html).isNotNull();
        assertThat(html.attr("lang")).isEqualTo("fr");
        assertThat(html.attr("data-theme")).isEqualTo("corporate");

        assertThat(page.title()).isEqualTo("Titre du site");
        assertThat(page.select("meta[name=description]").eachAttr("content"))
                .isNotNull()
                .containsExactly("Description du site");

        assertThat(page.select("link[rel=stylesheet]").eachAttr("href"))
                .allMatch(href -> href.contains("?build="))
                .extracting(href -> href.substring(0, href.indexOf('?')))
                .containsExactly(
                        "https://knowledge.angara.finance/assets/css/daisyui.css",
                        "https://knowledge.angara.finance/assets/css/themes.css",
                        "https://knowledge.angara.finance/assets/css/application.css"
                );

        // Open Graph meta tags =======================================================================================
        assertThat(page.select("meta[property=og:locale]").eachAttr("content"))
                .containsExactly("fr");
        assertThat(page.select("meta[property=og:site_name]").eachAttr("content"))
                .containsExactly("Nom du site");
        assertThat(page.select("meta[property=og:title]").eachAttr("content"))
                .containsExactly("Titre du site");
        assertThat(page.select("meta[property=og:description]").eachAttr("content"))
                .containsExactly("Description du site");
        assertThat(page.select("meta[property=og:type]").eachAttr("content"))
                .containsExactly("website");
        assertThat(page.select("meta[property=og:image]").eachAttr("content"))
                .containsExactly("https://knowledge.angara.finance/index.png");
        assertThat(page.select("meta[property=og:image:width]").eachAttr("content"))
                .containsExactly("1200");
        assertThat(page.select("meta[property=og:image:height]").eachAttr("content"))
                .containsExactly("630");

        // Twitter Card meta tags ====================================================================================
        assertThat(page.select("meta[name=twitter:card]").eachAttr("content"))
                .containsExactly("summary_large_image");
        assertThat(page.select("meta[name=twitter:title]").eachAttr("content"))
                .containsExactly("Titre du site");
        assertThat(page.select("meta[name=twitter:description]").eachAttr("content"))
                .containsExactly("Description du site");
        assertThat(page.select("meta[name=twitter:image]").eachAttr("content"))
                .containsExactly("https://knowledge.angara.finance/index.png");
    }

}
