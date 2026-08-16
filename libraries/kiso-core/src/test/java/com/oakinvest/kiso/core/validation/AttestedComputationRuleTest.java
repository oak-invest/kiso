package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.okf.markdown.computation.ComputationAttester;
import com.oakinvest.kiso.core.model.okf.markdown.computation.ComputationExecutor;
import com.oakinvest.kiso.core.model.okf.markdown.computation.ComputationParameter;
import com.oakinvest.kiso.core.util.BaseTest;
import com.oakinvest.kiso.core.validation.rule.AttestedComputationRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.oakinvest.kiso.core.util.contants.ConceptTypeConstants.ATTESTED_COMPUTATION;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.PARAMETERS_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.PARAMETER_REQUIRED_KEY;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.DUPLICATE_COMPUTATION_DEFINITION;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_COMPUTATION_PARAMETER_REQUIRED;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_COMPUTATION_PATH;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_COMPUTATION_ATTESTER_RESOURCE;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_COMPUTATION_DEFINITION;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_COMPUTATION_EXECUTOR_RECEIPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_COMPUTATION_EXECUTOR_RESOURCE;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_COMPUTATION_PARAMETER_NAME;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_COMPUTATION_PARAMETER_REQUIRED;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_COMPUTATION_PARAMETER_TYPE;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_COMPUTATION_RUNTIME;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Attested computation rule")
public class AttestedComputationRuleTest extends BaseTest {

    final AttestedComputationRule rule = new AttestedComputationRule();

    @Test
    @DisplayName("Accept a valid attested computation with a frontmatter computation path")
    void validFrontmatterComputation() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter().build(),
                "# Definition\n\nRevenue computation.\n"
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Accept a valid attested computation with a body computation code block")
    void validBodyComputation() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter()
                        .computation(null)
                        .build(),
                """
                        # Computation
                        
                        ```sql
                        SELECT SUM(amount) AS revenue FROM finance.orders
                        ```
                        """
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).isEmpty();
    }

    @Test
    @DisplayName("Report missing runtime")
    void missingRuntime() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter()
                        .runtime(null)
                        .build(),
                ""
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(MISSING_COMPUTATION_RUNTIME);
            assertThat(issue.message()).isEqualTo("Missing runtime field in Attested Computation frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFile.relativePath());
        });
    }

    @Test
    @DisplayName("Report missing parameter fields")
    void missingParameterFields() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter()
                        .parameters(List.of(ComputationParameter.builder().build()))
                        .build(),
                ""
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile))
                .extracting(ValidationIssue::code)
                .containsExactlyInAnyOrder(
                        MISSING_COMPUTATION_PARAMETER_NAME,
                        MISSING_COMPUTATION_PARAMETER_TYPE,
                        MISSING_COMPUTATION_PARAMETER_REQUIRED
                );
    }

    @Test
    @DisplayName("Report invalid parameter required value")
    void invalidParameterRequired() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter()
                        .parameters(List.of(ComputationParameter.builder()
                                .name("year")
                                .type("integer")
                                .required(false)
                                .build()))
                        .extraFields(Map.of(
                                PARAMETERS_KEY,
                                List.of(Map.of(PARAMETER_REQUIRED_KEY, "not-a-boolean"))
                        ))
                        .build(),
                ""
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_COMPUTATION_PARAMETER_REQUIRED);
            assertThat(issue.message()).isEqualTo("Invalid parameters[].required field in Attested Computation frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFile.relativePath());
        });
    }

    @Test
    @DisplayName("Report invalid computation path")
    void invalidComputationPath() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter()
                        .computation("references/computations/revenue query.sql")
                        .build(),
                ""
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(INVALID_COMPUTATION_PATH);
            assertThat(issue.message()).isEqualTo("Invalid computation field in Attested Computation frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFile.relativePath());
        });
    }

    @Test
    @DisplayName("Report missing executor fields")
    void missingExecutorFields() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter()
                        .executor(ComputationExecutor.builder().build())
                        .build(),
                ""
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile))
                .extracting(ValidationIssue::code)
                .containsExactlyInAnyOrder(
                        MISSING_COMPUTATION_EXECUTOR_RESOURCE,
                        MISSING_COMPUTATION_EXECUTOR_RECEIPT
                );
    }

    @Test
    @DisplayName("Report missing attester resource")
    void missingAttesterResource() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter()
                        .attester(ComputationAttester.builder().build())
                        .build(),
                ""
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(MISSING_COMPUTATION_ATTESTER_RESOURCE);
            assertThat(issue.message()).isEqualTo("Missing attester.resource field in Attested Computation frontmatter");
            assertThat(issue.path()).isEqualTo(markdownFile.relativePath());
        });
    }

    @Test
    @DisplayName("Report missing computation definition")
    void missingComputationDefinition() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter()
                        .computation(null)
                        .build(),
                "# Definition\n\nNo computation here.\n"
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(MISSING_COMPUTATION_DEFINITION);
            assertThat(issue.message()).isEqualTo("Missing computation definition in Attested Computation");
            assertThat(issue.path()).isEqualTo(markdownFile.relativePath());
        });
    }

    @Test
    @DisplayName("Report duplicate computation definition")
    void duplicateComputationDefinition() {
        var markdownFile = markdownFile(
                Path.of("computations/revenue.md"),
                validFrontmatter().build(),
                """
                        # Computation
                        
                        ```sql
                        SELECT SUM(amount) AS revenue FROM finance.orders
                        ```
                        """
        );

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile)).satisfiesOnlyOnce(issue -> {
            assertThat(issue.severity()).isEqualTo(ERROR);
            assertThat(issue.code()).isEqualTo(DUPLICATE_COMPUTATION_DEFINITION);
            assertThat(issue.message()).isEqualTo("Computation is declared both in frontmatter and body");
            assertThat(issue.path()).isEqualTo(markdownFile.relativePath());
        });
    }

    private Frontmatter.FrontmatterBuilder validFrontmatter() {
        return Frontmatter.builder()
                .type(ATTESTED_COMPUTATION)
                .runtime("bigquery")
                .parameters(List.of(ComputationParameter.builder()
                        .name("year")
                        .type("integer")
                        .required(true)
                        .build()))
                .computation("references/computations/revenue.sql")
                .executor(ComputationExecutor.builder()
                        .resource("references/skills/run-on-bq.md")
                        .receipt(List.of("job_id", "executed_sql", "result"))
                        .build())
                .attester(ComputationAttester.builder()
                        .resource("references/attesters/revenue.py")
                        .build());
    }

    private MarkdownFile markdownFile(final Path markdownFilePath,
                                      final Frontmatter frontmatter,
                                      final String body) {
        return MarkdownFile.builder()
                .fileName(markdownFilePath.getFileName().toString())
                .kind(CONCEPT)
                .absolutePath(markdownFilePath)
                .relativePath(markdownFilePath)
                .frontmatter(frontmatter)
                .frontmatterPresent(true)
                .body(body)
                .build();
    }

}
