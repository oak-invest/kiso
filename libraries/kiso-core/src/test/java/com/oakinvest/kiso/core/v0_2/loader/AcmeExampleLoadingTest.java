package com.oakinvest.kiso.core.v0_2.loader;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.okf.markdown.Actor;
import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.okf.markdown.provenance.Source;
import com.oakinvest.kiso.core.model.okf.markdown.provenance.UsageWindow;
import com.oakinvest.kiso.core.model.okf.markdown.trust.Generated;
import com.oakinvest.kiso.core.model.okf.markdown.trust.Verification;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.oakinvest.kiso.core.util.ActorType.AGENT;
import static com.oakinvest.kiso.core.util.ActorType.HUMAN;
import static com.oakinvest.kiso.core.util.LifecycleStatus.STABLE;
import static com.oakinvest.kiso.core.util.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.util.TrustLevel.HUMAN_REVIEWED;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - Loading acme example bundle")
public class AcmeExampleLoadingTest extends BaseTest {

    @Test
    @DisplayName("Loading acme example bundle without configuration")
    void acmeExamplesLoadingWithoutConfiguration() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_ACME_V_0_2);
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing computations/revenue-ytd file =======================================================================
        var revenue = bundle.markdownFiles()
                .filter(markdownFile -> "computations/revenue-ytd".equalsIgnoreCase(markdownFile.conceptId()))
                .findAny().orElseThrow(() -> new IllegalStateException("computations/revenue-ytd file not found"));

        // Fields in markdown file.
        assertThat(revenue).isNotNull()
                .returns("revenue-ytd.md", MarkdownFile::fileName)
                .returns(CONCEPT, MarkdownFile::kind)
                .returns(resourcePath.resolve("computations/revenue-ytd.md"), MarkdownFile::absolutePath)
                .returns(Path.of("computations/revenue-ytd.md"), MarkdownFile::relativePath)
                .returns(true, MarkdownFile::frontmatterPresent)
                .returns("computations", MarkdownFile::bundlePath)
                .returns("computations", MarkdownFile::bundleName)
                .returns("computations/revenue-ytd", MarkdownFile::conceptId)
                .returns("Revenue for a fiscal year", MarkdownFile::title)
                .returns("Sanctioned SQL that produces the recognized-revenue figure for a given fiscal year, per Acme's FY2026 Revenue Recognition Policy.", MarkdownFile::description)
                .returns(OffsetDateTime.parse("2026-06-30T14:00Z"), MarkdownFile::timestamp)
                .returns("revenue-ytd.html", MarkdownFile::htmlFilename)
                .returns("computations/revenue-ytd.html", MarkdownFile::htmlFilePath);

        // Frontmatter.
        assertThat(revenue.frontmatter()).isNotNull()
                .returns("Attested Computation", Frontmatter::type)
                .returns("Revenue for a fiscal year", Frontmatter::title)
                .returns("Sanctioned SQL that produces the recognized-revenue figure for a given fiscal year, per Acme's FY2026 Revenue Recognition Policy.", Frontmatter::description)
                .returns(List.of("finance", "revenue", "attested"), Frontmatter::tags)
                .returns("reference_agent/gemini-2.5-pro", Frontmatter::generatedBy)
                .returns(OffsetDateTime.parse("2026-06-30T14:00:00Z"), Frontmatter::generatedAt)
                .returns(null, Frontmatter::resource)
                .returns(null, Frontmatter::timestamp)
                .returns(STABLE, Frontmatter::status)
                .returns(STABLE, Frontmatter::lifecycleStatus)
                .returns("2026-12-31", Frontmatter::staleAfter)
                .returns(LocalDate.parse("2026-12-31"), Frontmatter::parsedStaleAfter)
                .returns(HUMAN_REVIEWED, Frontmatter::trustTier);

        // Frontmatter::generated.
        assertThat(revenue.frontmatter().generated()).isNotNull()
                .returns(Actor.of("reference_agent/gemini-2.5-pro"), Generated::by)
                .returns("2026-06-30T14:00:00Z", Generated::at)
                .returns(OffsetDateTime.parse("2026-06-30T14:00:00Z"), Generated::parsedAt);
        assertThat(revenue.frontmatter().generated().by().isAgent()).isTrue();
        assertThat(revenue.frontmatter().generated().by().type()).isEqualTo(AGENT);

        // Frontmatter::verified.
        assertThat(revenue.frontmatter().verified())
                .singleElement()
                .returns(Actor.of("human:jsmith@acme"), Verification::by)
                .returns("2026-07-01T09:00:00Z", Verification::at)
                .returns(OffsetDateTime.parse("2026-07-01T09:00:00Z"), Verification::parsedAt);
        assertThat(revenue.frontmatter().verified().getFirst().by().isHuman()).isTrue();
        assertThat(revenue.frontmatter().verified().getFirst().by().type()).isEqualTo(HUMAN);

        // Frontmatter::sources.
        assertThat(revenue.frontmatter().sources())
                .satisfiesExactly(
                        source1 -> assertThat(source1)
                                .returns("revenue-policy", Source::id)
                                .returns("policies/revenue-recognition.md", Source::resource)
                                .returns("Revenue Recognition Policy (FY2026)", Source::title)
                                .returns(true, Source::hasAuthor)
                                .returns(Actor.of("human:jsmith@acme"), Source::author)
                                .returns("2026-06-15", Source::lastModified)
                                .returns(LocalDate.parse("2026-06-15"), Source::parsedLastModified),
                        source2 -> assertThat(source2)
                                .returns("orders-table", Source::id)
                                .returns("tables/orders.md", Source::resource)
                                .returns("Customer Orders (BigQuery table)", Source::title)
                                .returns(true, Source::hasAuthor)
                                .returns(Actor.of("team:data-platform"), Source::author)
                                .returns("2026-07-01", Source::lastModified)
                                .returns(LocalDate.parse("2026-07-01"), Source::parsedLastModified)
                );

        // Testing tables/orders file ==================================================================================
        var orders = bundle.markdownFiles()
                .filter(markdownFile -> "tables/orders".equalsIgnoreCase(markdownFile.conceptId()))
                .findAny().orElseThrow(() -> new IllegalStateException("tables/orders file not found"));

        // Frontmatter::usageWindow.
        assertThat(orders.frontmatter().usageWindow()).isNotNull()
                .returns("2026-04-01", UsageWindow::from)
                .returns(LocalDate.parse("2026-04-01"), UsageWindow::parsedFrom)
                .returns("2026-06-30", UsageWindow::to)
                .returns(LocalDate.parse("2026-06-30"), UsageWindow::parsedTo);
    }

}
