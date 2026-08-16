package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.okf.markdown.Actor;
import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.trust.TrustEvent;
import com.oakinvest.kiso.core.util.BaseTest;
import com.oakinvest.kiso.core.validation.rule.ValidFrontmatterRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_OKF_VERSION;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_TIMESTAMP;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_FRONTMATTER;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_FRONTMATTER_TYPE;
import static com.oakinvest.kiso.core.validation.ValidationCode.UNEXPECTED_FRONTMATTER;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Valid frontmatter rule")
class ValidFrontmatterRuleTest extends BaseTest {

    final ValidFrontmatterRule rule = new ValidFrontmatterRule();

    @Test
    @DisplayName("Report a concept file without frontmatter")
    void conceptWithoutFrontmatter() {
        // We create a concept file without frontmatter ================================================================
        var markdownFilePath = Path.of("concept-without-frontmatter.md");
        var markdownFile = markdownFile(markdownFilePath, CONCEPT, null);

        // Run validation to check a missing mandatory frontmatter =====================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(MISSING_FRONTMATTER);
            assertThat(issue.message()).isEqualTo("File concept-without-frontmatter.md is missing mandatory frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Report a concept file without a type in its frontmatter")
    void conceptWithoutFrontmatterType() {
        // We create a concept file with frontmatter but without the type field ========================================
        var markdownFilePath = Path.of("concept-without-type.md");
        var markdownFile = markdownFile(markdownFilePath, CONCEPT, Frontmatter.empty());

        // Run validation to check a missing mandatory frontmatter type ================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(MISSING_FRONTMATTER_TYPE);
            assertThat(issue.message()).isEqualTo("File concept-without-type.md is missing mandatory 'type' in frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Ignore an index file without frontmatter")
    void indexWithoutFrontmatter() {
        // We create an index.md file without frontmatter, which is allowed ============================================
        var markdownFilePath = Path.of("index.md");
        var markdownFile = markdownFile(markdownFilePath, INDEX, null);

        // We check that there was no error at all =====================================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Index with a valid frontmatter is allowed")
    void indexWithFrontmatter(@TempDir Path temporaryDirectory) throws Exception {
        // What we are testing =========================================================================================
        var sourceDirectory = temporaryDirectory.resolve("bundle");
        Files.createDirectories(sourceDirectory);

        // index.md with invalid frontmatter ===========================================================================
        var rootIndex = sourceDirectory.resolve("index.md");
        Files.createDirectories(rootIndex.getParent());
        Files.writeString(rootIndex, """
                ---
                okf_version: "v0.2"
                ---
                Example content""");

        // index.md with invalid frontmatter in a subdirectory =========================================================
        var indexWithInvalidFrontmatter = sourceDirectory.resolve("test1/index.md");
        Files.createDirectories(indexWithInvalidFrontmatter.getParent());
        Files.writeString(indexWithInvalidFrontmatter, """
                ---
                title: Invalid frontmatter title
                ---
                Example content""");

        // index.md with valid frontmatter in a subdirectory ===========================================================
        var indexWithFrontmatterNotRoot = sourceDirectory.resolve("test2/index.md");
        Files.createDirectories(indexWithFrontmatterNotRoot.getParent());
        Files.writeString(indexWithFrontmatterNotRoot, """
                ---
                okf_version: "v0.2"
                ---
                Example content""");

        // We get the files from the bundle ============================================================================
        var bundle = KnowledgeBundleLoader.load(sourceDirectory);
        var indexRoot = bundle.markdownFiles()
                .filter(markdownFile -> markdownFile.bundleName().equals("index"))
                .findAny().orElseThrow(() -> new Exception("index.md not found"));
        var index1 = bundle.markdownFiles()
                .filter(markdownFile -> markdownFile.bundleName().equals("test1"))
                .findAny().orElseThrow(() -> new Exception("test1/index.md not found"));
        var index2 = bundle.markdownFiles()
                .filter(markdownFile -> markdownFile.bundleName().equals("test2"))
                .findAny().orElseThrow(() -> new Exception("test2/index.md not found"));

        // We check the errors =========================================================================================
        assertThat(rule.validate(bundle.rootBundle(), indexRoot)).isEmpty();
        assertThat(rule.validate(bundle.rootBundle(), index1)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(UNEXPECTED_FRONTMATTER);
            assertThat(issue.message()).isEqualTo("File test1/index.md is not a concept file and should not contain frontmatter");
            assertThat(issue.path()).isEqualTo(Path.of("test1/index.md"));
        });
        assertThat(rule.validate(bundle.rootBundle(), index2)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(UNEXPECTED_FRONTMATTER);
            assertThat(issue.message()).isEqualTo("File test2/index.md is not a concept file and should not contain frontmatter");
            assertThat(issue.path()).isEqualTo(Path.of("test2/index.md"));
        });
    }

    @Test
    @DisplayName("Index with a valid frontmatter but invalid version")
    void indexWithFrontmatterButInvalidVersion(@TempDir Path temporaryDirectory) throws Exception {
        // What we are testing =========================================================================================
        var sourceDirectory = temporaryDirectory.resolve("bundle");
        Files.createDirectories(sourceDirectory);

        // index.md with invalid frontmatter ===========================================================================
        var rootIndex = sourceDirectory.resolve("index.md");
        Files.createDirectories(rootIndex.getParent());
        Files.writeString(rootIndex, """
                ---
                okf_version: "v0.0"
                ---
                Example content""");

        // We get the files from the bundle ============================================================================
        var bundle = KnowledgeBundleLoader.load(sourceDirectory);
        var indexRoot = bundle.markdownFiles()
                .filter(markdownFile -> markdownFile.bundleName().equals("index"))
                .findAny().orElseThrow(() -> new Exception("index.md not found"));

        // We check the errors =========================================================================================
        assertThat(rule.validate(bundle.rootBundle(), indexRoot)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_OKF_VERSION);
            assertThat(issue.message()).isEqualTo("File index.md has invalid 'okf_version' in frontmatter:v0.0");
            assertThat(issue.path()).isEqualTo(Path.of("index.md"));
        });
    }

    @Test
    @DisplayName("Timestamp is present but doesn't respect ISO 8601 datetime format")
    void invalidTimestamp() {
        // We create a concept file with frontmatter but with an invalid timestamp =====================================
        var markdownFilePath = Path.of("concept-with-invalid-timestamp.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .timestamp("02-07-2026T14:30:00Z")
                .build();
        var markdownFile = markdownFile(markdownFilePath, CONCEPT, frontmatter);

        // Run validation to check an invalid timestamp ================================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_TIMESTAMP);
            assertThat(issue.message()).isEqualTo("File concept-with-invalid-timestamp.md has invalid 'timestamp' in frontmatter. It must be in ISO 8601 datetime format");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Generated at is present but doesn't respect ISO 8601 datetime format")
    void invalidGeneratedAt() {
        // We create a concept file with frontmatter but with an invalid generated.at ==================================
        var markdownFilePath = Path.of("concept-with-invalid-generated-at.md");
        var frontmatter = Frontmatter.builder()
                .type("Concept")
                .generated(TrustEvent.builder()
                        .by(Actor.of("reference_agent/gemini-2.5-pro"))
                        .at("20-06-2026T22:53:05Z")
                        .build())
                .build();
        var markdownFile = markdownFile(markdownFilePath, CONCEPT, frontmatter);

        // Run validation to check an invalid generated.at =============================================================
        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_TIMESTAMP);
            assertThat(issue.message()).isEqualTo("File concept-with-invalid-generated-at.md has invalid 'generated.at' in frontmatter. It must be in ISO 8601 datetime format");
            assertThat(issue.path()).isEqualTo(markdownFilePath);
        });
    }

    @Test
    @DisplayName("Non concept files should not have frontmatter")
    void unexpectedFrontmatter() {
        // We create files that should not have frontmatter ============================================================
        var indexFilePath = Path.of(INDEX.getFileName());
        var frontmatter = Frontmatter.builder().build();
        var indexFile = markdownFile(indexFilePath, INDEX, frontmatter);

        // log.md
        // var logFilePath = Path.of(LOG.getFileName());
        // var logFile = markdownFile(logFilePath, LOG, frontmatter);

        // Run validation to check an unexpected frontmatter ============================================================
        assertThat(rule.validate(bundleWith(indexFile), indexFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(UNEXPECTED_FRONTMATTER);
            assertThat(issue.message()).isEqualTo("File index.md is not a concept file and should not contain frontmatter");
            assertThat(issue.path()).isEqualTo(indexFilePath);
        });
    }

}
