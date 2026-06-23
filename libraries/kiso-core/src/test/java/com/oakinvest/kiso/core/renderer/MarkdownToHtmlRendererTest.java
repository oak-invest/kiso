package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.renderer.engine.MarkdownToHtmlRenderer;
import com.oakinvest.kiso.core.util.BaseTest;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class MarkdownToHtmlRendererTest extends BaseTest {

    @BeforeEach
    void setup() {
        // Waiting to implement i18n.
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    @DisplayName("Markdown to html")
    void markdownToHTML() throws URISyntaxException, IOException {
        // What we are testing =========================================================================================
        var targetDirectory = Path.of(MarkdownToHtmlRendererTest.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).getParent();
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var bundle = new KnowledgeBundleLoader().load(resourcePath);
        var markdownToHtmlRenderer = new MarkdownToHtmlRenderer();

        // Index file of the bundle ====================================================================================
        var markdownFiles = bundle.rootBundle().markdownFiles();
        var page = Jsoup.parse(markdownToHtmlRenderer.render(markdownFiles.getFirst()));

        // Writing the file (console & target directory) for debugging purposes ========================================
        FileUtils.writeStringToFile(targetDirectory.resolve("test-index.html").toFile(), page.html(), UTF_8);

        // Document head.
        assertThat(page.title()).isEqualTo("Index");
        assertThat(page.select("link[rel=stylesheet]").eachAttr("href"))
                .containsExactly(
                        "assets/css/daisyui@5.css",
                        "assets/css/themes.css",
                        "assets/css/application.css"
                );
        assertThat(page.selectFirst("script[src]").attr("src")).isEqualTo("assets/js/browser@4.js");

        // Index header.
        var indexHeader = page.selectFirst("main > section");
        assertThat(indexHeader).isNotNull();
        assertThat(indexHeader.select(".badge").eachText()).containsExactly("Index");

        // Article source file.
        var indexArticle = page.selectFirst("article.kiso-content");
        assertThat(indexArticle).isNotNull();
        assertThat(indexArticle.selectFirst(".kiso-source-file-name").text()).isEqualTo("index.md");

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
        assertThat(firstLink.attr("href")).isEqualTo("datasets/index.html");
        assertThat(firstLink.text()).isEqualTo("datasets");
        var secondLink = page.selectXpath("//ul/li/a").get(1);
        assertThat(secondLink.attr("href")).isEqualTo("references/index.html");
        assertThat(secondLink.text()).isEqualTo("references");
        var thirdLink = page.selectXpath("//ul/li/a").get(2);
        assertThat(thirdLink.attr("href")).isEqualTo("tables/index.html");
        assertThat(thirdLink.text()).isEqualTo("tables");

        // datasets/ga4_obfuscated_sample_ecommerce.md =================================================================
        markdownFiles = bundle.rootBundle().childBundleDirectories().getFirst().markdownFiles();
        page = Jsoup.parse(markdownToHtmlRenderer.render(markdownFiles.getFirst()));

        // Writing the file (console & target directory) for debugging purposes ========================================
        FileUtils.writeStringToFile(targetDirectory.resolve("datasets/test-concept.html").toFile(), page.html(), UTF_8);

        // Testing the content =========================================================================================

        // Document head.
        assertThat(page.title()).isEqualTo("BigQuery sample dataset for Google Analytics ecommerce web implementation");
        assertThat(page.selectFirst("meta[name=description]").attr("content")).startsWith("A sample of obfuscated Google Analytics");
        assertThat(page.select("link[rel=stylesheet]").eachAttr("href"))
                .containsExactly(
                        "../assets/css/daisyui@5.css",
                        "../assets/css/themes.css",
                        "../assets/css/application.css"
                );
        assertThat(page.selectFirst("script[src]").attr("src")).isEqualTo("../assets/js/browser@4.js");

        // Concept header.
        var header = page.selectFirst("main > section");
        assertThat(header).isNotNull();
        assertThat(header.select(".badge").eachText())
                .containsExactly(
                        "Concept",
                        "BigQuery Dataset",
                        "ecommerce",
                        "web analytics",
                        "Google Analytics",
                        "BigQuery",
                        "public dataset"
                );

        // Title.
        assertThat(header.selectFirst("div.text-4xl").text()).isEqualTo("BigQuery sample dataset for Google Analytics ecommerce web implementation");
        assertThat(header.selectFirst("p").text()).startsWith("A sample of obfuscated Google Analytics");

        // Ressource
        var resourceLink = header.selectFirst("a[href='https://bigquery.googleapis.com/v2/projects/bigquery-public-data/datasets/ga4_obfuscated_sample_ecommerce']");
        assertThat(resourceLink).isNotNull();
        assertThat(resourceLink.text()).isEqualTo("https://bigquery.googleapis.com/v2/projects/bigquery-public-data/datasets/ga4_obfuscated_sample_ecommerce");

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

        // Article source file.
        var article = page.selectFirst("article.kiso-content");
        assertThat(article).isNotNull();
        assertThat(article.children().first().hasClass("kiso-source-file")).isTrue();
        var sourceFile = article.selectFirst(".kiso-source-file");
        assertThat(sourceFile.selectFirst(".kiso-source-file-label").text()).isEqualTo("Markdown file");
        assertThat(sourceFile.selectFirst(".kiso-source-file-name").text())
                .isEqualTo("ga4_obfuscated_sample_ecommerce.md");

        // Code block.
        var codeBlock = article.selectFirst("pre > code.language-sql");
        assertThat(codeBlock).isNotNull();
        assertThat(codeBlock.text()).contains("COUNT(*) AS event_count");
        assertThat(codeBlock.text()).contains("bigquery-public-data.ga4_obfuscated_sample_ecommerce.events_*");

        // Autolinks.
        var citationLink = page.selectXpath("//h1[text()='Citations']/following-sibling::ul[1]/li[1]/a").getFirst();
        assertThat(citationLink.attr("href"))
                .isEqualTo("https://developers.google.com/analytics/bigquery/web-ecommerce-demo-dataset");
        assertThat(citationLink.text())
                .isEqualTo("https://developers.google.com/analytics/bigquery/web-ecommerce-demo-dataset");
    }

}
