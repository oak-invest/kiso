package com.oakinvest.kiso.core.v0_1.renderer;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.html.navigation.BundleTree;
import com.oakinvest.kiso.core.renderer.MarkdownToHtmlRenderer;
import com.oakinvest.kiso.core.util.BaseTest;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static com.oakinvest.kiso.core.util.OKFConstants.ROOT_BUNDLE_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.1 - Markdown to HTML renderer")
public class MarkdownToHtmlRendererTest extends BaseTest {

    @BeforeEach
    void setup() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    @DisplayName("Markdown to html")
    void markdownToHTML(@TempDir Path temporaryDirectory) throws IOException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_1);
        var bundle = KnowledgeBundleLoader.load(resourcePath);
        var bundleTree = BundleTree.fromBundle(bundle.rootBundle());

        // =============================================================================================================
        // Testing index.md
        // =============================================================================================================

        // Writing the file of the bundle ==============================================================================
        var markdownFiles = bundle.rootBundle().markdownFiles();
        var page = Jsoup.parse(MarkdownToHtmlRenderer.render(SiteConfiguration.empty(), ThemeConfiguration.empty(), markdownFiles.getFirst(), bundleTree));
        FileUtils.writeStringToFile(temporaryDirectory.resolve("test-index.html").toFile(), page.html(), UTF_8);

        // Document head ===============================================================================================
        Element html = page.selectFirst("html");
        assertThat(html).isNotNull();
        assertThat(html.attr("lang")).isEqualTo("en");
        assertThat(html.attr("data-theme")).isEqualTo("light");

        assertThat(page.title()).isEqualTo("index.md");
        assertThat(page.select("link[rel=stylesheet]").eachAttr("href"))
                .allMatch(href -> href.contains("?build="))
                .extracting(href -> href.substring(0, href.indexOf('?')))
                .containsExactly(
                        "assets/css/daisyui.css",
                        "assets/css/themes.css",
                        "assets/css/application.css"
                );
        assertThat(page.select("script[src]").eachAttr("src"))
                .allMatch(href -> href.contains("?build="))
                .extracting(href -> href.substring(0, href.indexOf('?')))
                .containsExactly(
                        "assets/js/browser.js",
                        "assets/js/i18next.js",
                        "assets/js/minisearch.js",
                        "assets/js/kiso-i18n.js",
                        "assets/js/kiso-search.js",
                        "assets/js/kiso-back-to-top.js"
                );
        assertThat(page.select("script[src^='assets/js/kiso-i18n.js?build=']").eachAttr("data-i18n-base-url"))
                .containsExactly("assets/i18n/");
        assertThat(page.select("script[src^='assets/js/kiso-i18n.js?build=']").eachAttr("data-i18n-language")).isEmpty();
        assertThat(page.select("script[src^='assets/js/kiso-i18n.js?build=']").eachAttr("data-i18n-languages"))
                .containsExactly("en,fr,de,es,it,pt,nl,pl,ru,zh,ja,ko,ar,hi");

        // Navigation bar - Home index =================================================================================
        Element homeLink = page.selectFirst("a[aria-label='Home'][data-i18n-aria-label='navigation.home']");
        assertThat(homeLink).isNotNull();
        assertThat(homeLink.hasClass("btn-square")).isTrue();
        assertThat(homeLink.selectFirst("svg")).isNotNull();
        assertThat(homeLink.text()).isBlank();

        Element backToTopButton = page.selectFirst("button.kiso-back-to-top[aria-label='Back to top'][data-i18n-aria-label='navigation.backToTop']");
        assertThat(backToTopButton).isNotNull();
        assertThat(backToTopButton.hasClass("btn-square")).isTrue();
        assertThat(backToTopButton.hasClass("fixed")).isTrue();
        assertThat(backToTopButton.selectFirst("svg")).isNotNull();
        assertThat(backToTopButton.text()).isBlank();

        // Navigation bar - Drawer =====================================================================================
        assertThat(page.selectFirst("input#kiso-navigation-drawer.drawer-toggle")).isNotNull();
        assertThat(page.selectFirst("label[for=kiso-navigation-drawer][aria-label='Open navigation']")).isNotNull();
        assertThat(page.selectFirst(".drawer-side ul.menu.menu-sm")).isNotNull();

        assertElementText(page.selectFirst(".drawer-side [data-i18n='navigation.bundleContent']"), "Bundle content");
        assertThat(page.select(".drawer-side details[open]")).isEmpty();
        var indexLink = page.selectFirst(".drawer-side a[href='index.html']");
        assertThat(indexLink).isNotNull();
        assertThat(indexLink.text()).isEqualTo(ROOT_BUNDLE_NAME);
        assertThat(indexLink.className()).isEqualTo("font-semibold");
        assertElementText(page.selectFirst(".drawer-side details summary"), "datasets");
        assertElementText(page.selectFirst(".drawer-side a[href='datasets/ga4_obfuscated_sample_ecommerce.html']"),
                "BigQuery sample dataset for Google Analytics ecommerce web implementation");

        // Link ========================================================================================================
        var kisoLink = page.selectFirst(".drawer-side a[href='https://oak-invest.github.io/kiso']");
        assertThat(kisoLink).isNotNull();
        assertThat(kisoLink.text()).isEqualTo("Kiso");

        // Index header.
        var indexHeader = page.selectFirst("main > section");
        assertThat(indexHeader).isNotNull();
        assertThat(indexHeader.select(".badge").eachText()).containsExactly("Index");

        // Article source file.
        var indexArticle = page.selectFirst("article.kiso-content");
        assertThat(indexArticle).isNotNull();

        // Content =====================================================================================================
        // H1.
        assertThat(indexArticle.select("h1")).hasSize(1);
        assertThat(indexArticle.selectFirst("h1"))
                .isNotNull()
                .extracting(Element::text)
                .containsExactly("Subdirectories");

        // UL.
        assertThat(indexArticle.select("ul")).hasSize(1);

        // IL.
        assertThat(indexArticle.select("ul li")).hasSize(3);
        assertThat(indexArticle.select("ul li").getFirst().text()).contains("datasets - A sample of obfuscated Google Analytics BigQuery event export data for three months from the Google Merchandise Store is available as a public dataset in BigQuery.");
        assertThat(indexArticle.select("ul li").get(1).text()).contains("references - This directory contains specifications for data joins and definitions for user activity and purchase metrics.");
        assertThat(indexArticle.select("ul li").get(2).text()).contains("tables - Contains Google Analytics event export data from the ga4_obfuscated_sample_ecommerce dataset.");

        // Links.
        assertThat(indexArticle.select("ul li a")).hasSize(3);
        var firstLink = indexArticle.select("ul li a").getFirst();
        assertThat(firstLink.attr("href")).isEqualTo("datasets/index.html");
        assertThat(firstLink.text()).isEqualTo("datasets");
        var secondLink = indexArticle.select("ul li a").get(1);
        assertThat(secondLink.attr("href")).isEqualTo("references/index.html");
        assertThat(secondLink.text()).isEqualTo("references");
        var thirdLink = indexArticle.select("ul li a").get(2);
        assertThat(thirdLink.attr("href")).isEqualTo("tables/index.html");
        assertThat(thirdLink.text()).isEqualTo("tables");

        // =============================================================================================================
        // Testing datasets/ga4_obfuscated_sample_ecommerce.md
        // =============================================================================================================

        // datasets/ga4_obfuscated_sample_ecommerce.md =================================================================
        markdownFiles = bundle.rootBundle().childBundles().getFirst().markdownFiles();
        page = Jsoup.parse(MarkdownToHtmlRenderer.render(SiteConfiguration.empty(), ThemeConfiguration.empty(), markdownFiles.getFirst(), bundleTree));

        // Document head ===============================================================================================
        assertThat(page.title()).isEqualTo("BigQuery sample dataset for Google Analytics ecommerce web implementation");
        assertThat(page.select("meta[name=description]").eachAttr("content"))
                .containsExactly("A sample of obfuscated Google Analytics BigQuery event export data for three months from the Google Merchandise Store is available as a public dataset in BigQuery.");
        assertThat(page.select("link[rel=stylesheet]").eachAttr("href"))
                .allMatch(href -> href.contains("?build="))
                .extracting(href -> href.substring(0, href.indexOf('?')))
                .containsExactly(
                        "../assets/css/daisyui.css",
                        "../assets/css/themes.css",
                        "../assets/css/application.css"
                );
        assertThat(page.select("script[src]").eachAttr("src"))
                .allMatch(href -> href.contains("?build="))
                .extracting(href -> href.substring(0, href.indexOf('?')))
                .containsExactly(
                        "../assets/js/browser.js",
                        "../assets/js/i18next.js",
                        "../assets/js/minisearch.js",
                        "../assets/js/kiso-i18n.js",
                        "../assets/js/kiso-search.js",
                        "../assets/js/kiso-back-to-top.js"
                );
        assertThat(page.select("script[src^='../assets/js/kiso-i18n.js?build=']").eachAttr("data-i18n-base-url"))
                .containsExactly("../assets/i18n/");
        assertThat(page.selectFirst(".kiso-search[data-search-index-url='../search-index.json']")).isNotNull();

        // Navigation bar - Home index =================================================================================
        homeLink = page.selectFirst("a[aria-label='Home'][data-i18n-aria-label='navigation.home']");
        assertThat(homeLink).isNotNull();
        assertThat(homeLink.hasClass("btn-square")).isTrue();
        assertThat(homeLink.selectFirst("svg")).isNotNull();
        assertThat(homeLink.text()).isBlank();

        backToTopButton = page.selectFirst("button.kiso-back-to-top[aria-label='Back to top'][data-i18n-aria-label='navigation.backToTop']");
        assertThat(backToTopButton).isNotNull();

        // Drawer ======================================================================================================
        assertThat(page.selectFirst("input#kiso-navigation-drawer.drawer-toggle")).isNotNull();
        assertThat(page.selectFirst("label[for=kiso-navigation-drawer][aria-label='Open navigation']")).isNotNull();
        assertThat(page.selectFirst(".drawer-side ul.menu.menu-sm")).isNotNull();
        assertThat(page.select(".drawer-side details[open]")).hasSize(1);
        assertElementText(page.selectFirst(".drawer-side details[open] > summary"), "datasets");
        assertElementText(page.selectFirst(".drawer-side a[href='../index.html']"), ROOT_BUNDLE_NAME);
        assertElementClassName(page.selectFirst(".drawer-side summary"), "menu-active");
        assertElementClassName(page.selectFirst(".drawer-side a[href='../datasets/ga4_obfuscated_sample_ecommerce.html']"), "font-semibold");
        assertThat(page.selectFirst(".drawer-side a[href='../datasets/ga4_obfuscated_sample_ecommerce.html']").className())
                .doesNotContain("menu-active");
        assertElementText(page.selectFirst(".drawer-side a[href='../references/metrics/avg_pageviews.html']"), "Average Pageviews");

        // Concept header ==============================================================================================
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

        // Title =======================================================================================================
        assertThat(header.selectFirst("div.text-4xl"))
                .isNotNull()
                .extracting(Element::text)
                .containsExactly("BigQuery sample dataset for Google Analytics ecommerce web implementation");
        assertThat(header.selectFirst("p"))
                .isNotNull()
                .extracting(Element::text)
                .containsExactly("A sample of obfuscated Google Analytics BigQuery event export data for three months from the Google Merchandise Store is available as a public dataset in BigQuery.");

        // Ressource ===================================================================================================
        var resourceLink = header.selectFirst("a[href='https://bigquery.googleapis.com/v2/projects/bigquery-public-data/datasets/ga4_obfuscated_sample_ecommerce']");
        assertThat(resourceLink).isNotNull();
        assertThat(resourceLink.text()).isEqualTo("https://bigquery.googleapis.com/v2/projects/bigquery-public-data/datasets/ga4_obfuscated_sample_ecommerce");

        // Content =====================================================================================================

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

    @Test
    @DisplayName("Do not display a bundle index link when index.md does not exist")
    void doNotDisplayMissingBundleIndexInNavigation(@TempDir Path temporaryDirectory) throws IOException {
        Files.writeString(temporaryDirectory.resolve("concept.md"), """
                ---
                type: Concept
                title: Example
                ---
                # Example
                """);
        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);
        var bundleTree = BundleTree.fromBundle(bundle.rootBundle());

        var page = Jsoup.parse(MarkdownToHtmlRenderer.render(
                SiteConfiguration.empty(),
                ThemeConfiguration.empty(),
                bundle.rootBundle().markdownFiles().getFirst(),
                bundleTree));

        assertThat(bundleTree.hasIndexPage()).isFalse();
        assertThat(page.select(".drawer-side a[href='index.html']")).isEmpty();
        var conceptLink = page.selectFirst(".drawer-side a[href='concept.html']");
        assertThat(conceptLink).isNotNull();
        assertThat(conceptLink.text()).isEqualTo("Example");
    }

}
