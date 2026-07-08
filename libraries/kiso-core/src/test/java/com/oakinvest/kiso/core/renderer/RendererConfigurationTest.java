package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.renderer.model.navigation.BundleTree;
import com.oakinvest.kiso.core.util.BaseTest;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class RendererConfigurationTest extends BaseTest {

    @Test
    @DisplayName("Markdown to html with configuration")
    void markdownToHTML() throws URISyntaxException, IOException {
        // What we are testing =========================================================================================
        var targetDirectory = Path.of(RendererConfigurationTest.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).getParent();
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        var bundle = KnowledgeBundleLoader.load(resourcePath);
        var bundleTree = BundleTree.fromBundle(bundle.rootBundle());

        // Configuration ===============================================================================================
        var siteConfiguration = new SiteConfiguration(
                "https://knowledge.angara.finance/",
                Locale.FRENCH,
                "Titre du site",
                "Description du site");
        var themeConfiguration = new ThemeConfiguration(
                "corporate");

        // Index file of the bundle ====================================================================================
        var markdownFiles = bundle.rootBundle().markdownFiles();
        var page = Jsoup.parse(MarkdownToHtmlRenderer.render(siteConfiguration, themeConfiguration, markdownFiles.getFirst(), bundleTree));

        // Writing the file (console & target directory) for debugging purposes ========================================
        FileUtils.writeStringToFile(targetDirectory.resolve("test-index.html").toFile(), page.html(), UTF_8);

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
                .containsExactly(
                        "https://knowledge.angara.finance/assets/css/daisyui@5.css",
                        "https://knowledge.angara.finance/assets/css/themes.css",
                        "https://knowledge.angara.finance/assets/css/application.css"
                );
    }

}
