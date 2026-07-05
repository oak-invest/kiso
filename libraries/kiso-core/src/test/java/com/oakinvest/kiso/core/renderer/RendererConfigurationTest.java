package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.renderer.model.navigation.BundleTree;
import com.oakinvest.kiso.core.util.BaseTest;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class RendererConfigurationTest extends BaseTest {

    @TempDir
    private Path temporaryDirectory;

    @BeforeEach
    void setup() {
        // Waiting to implement i18n.
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    @DisplayName("Markdown to html with configuration")
    void markdownToHTML() throws URISyntaxException, IOException {
        // What we are testing =========================================================================================
        var targetDirectory = Path.of(RendererConfigurationTest.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).getParent();
        var resourcePath = getResourcePath("kb-google-example-v0.1-with-configuration");
        var bundle = KnowledgeBundleLoader.load(resourcePath);
        var bundleTree = BundleTree.fromBundle(bundle.rootBundle());

        // Configuration ===============================================================================================
        SiteConfiguration siteConfiguration = new SiteConfiguration(
                Locale.FRENCH,
                "Titre du site",
                "Description du site");
        ThemeConfiguration themeConfiguration = new ThemeConfiguration(
                "corporate");

        // Index file of the bundle ====================================================================================
        var markdownFiles = bundle.rootBundle().markdownFiles();
        var page = Jsoup.parse(MarkdownToHtmlRenderer.render(siteConfiguration, themeConfiguration, markdownFiles.getFirst(), bundleTree));

        // Writing the file (console & target directory) for debugging purposes ========================================
        FileUtils.writeStringToFile(targetDirectory.resolve("test-index.html").toFile(), page.html(), UTF_8);

        // Assertions ==================================================================================================
        assertThat(page.selectFirst("html")).isNotNull();
        assertThat(page.selectFirst("html").attr("lang")).isEqualTo("fr");
        assertThat(page.selectFirst("html").attr("data-theme"))
                .isEqualTo("corporate");

        assertThat(page.title()).isEqualTo("Titre du site");

        assertThat(page.selectFirst("meta[name=description]")).isNotNull();
        assertThat(page.selectFirst("meta[name=description]").attr("content"))
                .isEqualTo("Description du site");
    }

}
