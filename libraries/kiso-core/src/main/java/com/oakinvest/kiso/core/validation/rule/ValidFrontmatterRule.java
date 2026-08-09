package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_TIMESTAMP;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_FRONTMATTER;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_FRONTMATTER_TYPE;
import static com.oakinvest.kiso.core.validation.ValidationCode.UNEXPECTED_FRONTMATTER;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;

/**
 * Valid frontmatter rule.
 * <a href="https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md">OKF Spec</a>
 * 4. Concept Documents
 * Every concept has two parts:
 * - A YAML frontmatter block.
 * - A markdown body.
 * Type: <Type name> # REQUIRED
 */
public class ValidFrontmatterRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final Bundle bundle, final MarkdownFile markdownFile) {
        Objects.requireNonNull(bundle, "bundle must not be null");
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        // Only on concept files =======================================================================================
        if (markdownFile.kind().equals(CONCEPT)) {
            // If there is not frontmatter =============================================================================
            if (!markdownFile.frontmatterPresent()) {
                return List.of(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(MISSING_FRONTMATTER)
                        .message("File " + markdownFile.relativePath() + " is missing mandatory frontmatter")
                        .path(markdownFile.relativePath())
                        .build());
            } else {
                Frontmatter frontmatter = markdownFile.frontmatter();

                // If there is a frontmatter and type is missing =======================================================
                if (StringUtils.isBlank(frontmatter.type())) {
                    return List.of(ValidationIssue.builder()
                            .severity(ERROR)
                            .code(MISSING_FRONTMATTER_TYPE)
                            .message("File " + markdownFile.relativePath() + " is missing mandatory 'type' in frontmatter")
                            .path(markdownFile.relativePath())
                            .build());
                }

                // If there is a frontmatter and timestamp is not ISO 8601 datetime format =============================
                if (StringUtils.isNotBlank(frontmatter.timestamp()) && frontmatter.parsedTimestamp() == null) {
                    return List.of(ValidationIssue.builder()
                            .severity(ERROR)
                            .code(INVALID_TIMESTAMP)
                            .message("File " + markdownFile.relativePath() + " has invalid 'timestamp' in frontmatter. It must be in ISO 8601 datetime format")
                            .path(markdownFile.relativePath())
                            .build());
                }

                // If there is a frontmatter and generated.at is not ISO 8601 datetime format =========================
                if (frontmatter.generated() != null
                        && StringUtils.isNotBlank(frontmatter.generated().at())
                        && frontmatter.generated().parsedAt() == null) {
                    return List.of(ValidationIssue.builder()
                            .severity(ERROR)
                            .code(INVALID_TIMESTAMP)
                            .message("File " + markdownFile.relativePath() + " has invalid 'generated.at' in frontmatter. It must be in ISO 8601 datetime format")
                            .path(markdownFile.relativePath())
                            .build());
                }
            }

        } else {
            // Non-CONCEPT file should not contain frontmatter =========================================================
            if (markdownFile.frontmatterPresent()) {
                return List.of(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(UNEXPECTED_FRONTMATTER)
                        .message("File " + markdownFile.relativePath() + " is not a concept file and should not contain frontmatter")
                        .path(markdownFile.relativePath())
                        .build());
            }
        }
        return List.of();
    }

}
