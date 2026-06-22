package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.renderer.engine.MarkdownToHtmlRenderer;
import com.oakinvest.kiso.core.util.BaseTest;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class MarkdownToHtmlRendererTest extends BaseTest {

    @Test
    @DisplayName("Mardown to html")
    void markdownToHTML() throws URISyntaxException, IOException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var bundle = new KnowledgeBundleLoader().load(resourcePath);
        var markdownToHtmlRenderer = new MarkdownToHtmlRenderer();

        // Index file of the bundle ====================================================================================
        var markdownFiles = bundle.rootBundle().markdownFiles();
        var page = Jsoup.parse(markdownToHtmlRenderer.render(markdownFiles.getFirst()));

        // H1.
        assertThat(page.selectXpath("//h1").size()).isEqualTo(1);
        assertThat(page.selectXpath("//h1").getFirst().text()).contains("Subdirectories");

        // UL.
        assertThat(page.selectXpath("//ul").size()).isEqualTo(1);

        // IL.
        assertThat(page.selectXpath("//ul/li").size()).isEqualTo(3);
        assertThat(page.selectXpath("//ul/li").getFirst().text()).contains("datasets - A sample of obfuscated Google Analytics BigQuery event export data for three months from the Google Merchandise Store is available as a public dataset in BigQuery.");
        assertThat(page.selectXpath("//ul/li").get(1).text()).contains("references - This directory contains specifications for data joins and definitions for user activity and purchase metrics.");
        assertThat(page.selectXpath("//ul/li").get(2).text()).contains("tables - Contains Google Analytics event export data from the ga4_obfuscated_sample_ecommerce dataset.");

        // Links.
        assertThat(page.selectXpath("//ul/li/a").size()).isEqualTo(3);
        var firstLink = page.selectXpath("//ul/li/a").getFirst();
        assertThat(firstLink.attr("href")).isEqualTo("datasets/index.md");
        assertThat(firstLink.text()).isEqualTo("datasets");
        var secondLink = page.selectXpath("//ul/li/a").get(1);
        assertThat(secondLink.attr("href")).isEqualTo("references/index.md");
        assertThat(secondLink.text()).isEqualTo("references");
        var thirdLink = page.selectXpath("//ul/li/a").get(2);
        assertThat(thirdLink.attr("href")).isEqualTo("tables/index.md");
        assertThat(thirdLink.text()).isEqualTo("tables");

        // datasets/ga4_obfuscated_sample_ecommerce.md =================================================================
        markdownFiles = bundle.rootBundle().childBundleDirectories().getFirst().markdownFiles();
        page = Jsoup.parse(markdownToHtmlRenderer.render(markdownFiles.getFirst()));

        // Writing the file (console & target directory) for debugging purposes ========================================
        var targetDirectory = Path.of(MarkdownToHtmlRendererTest.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).getParent();
        FileUtils.writeStringToFile(targetDirectory.resolve("test.html").toFile(), page.html(), UTF_8);
        System.out.println(page.html());

        // Testing the content =========================================================================================

        // H1.
        assertThat(page.selectXpath("//h1").size()).isEqualTo(5);
        assertThat(page.selectXpath("//h1").getFirst().text()).contains("Overview");
        assertThat(page.selectXpath("//h1").get(1).text()).contains("Pre-requisites");
        assertThat(page.selectXpath("//h1").get(2).text()).contains("Limitations");
        assertThat(page.selectXpath("//h1").get(3).text()).contains("Using the dataset");
        assertThat(page.selectXpath("//h1").get(4).text()).contains("Citations");

        // H2.
        assertThat(page.selectXpath("//h2").size()).isEqualTo(1);
        assertThat(page.selectXpath("//h2").getFirst().text()).contains("Sample Query");

        // Text.
        assertThat(page.selectXpath("//h1[text()='Pre-requisites']/following-sibling::*[1]").text())
                .isEqualTo("To work with this dataset, you need access to a Google Cloud project with the BigQuery API enabled. You can use BigQuery Sandbox mode or the Free usage tier for exploration and sample queries.");
    }

}
