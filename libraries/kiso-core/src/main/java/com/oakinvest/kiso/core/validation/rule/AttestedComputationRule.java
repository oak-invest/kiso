package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.computation.ComputationParameter;
import com.oakinvest.kiso.core.validation.ValidationCode;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.oakinvest.kiso.core.util.contants.ConceptTypeConstants.ATTESTED_COMPUTATION;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.PARAMETERS_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.PARAMETER_REQUIRED_KEY;
import static com.oakinvest.kiso.core.util.contants.MarkdownConstants.HEADING_LEVEL_1;
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

/**
 * Attested computation rule.
 */
public class AttestedComputationRule implements MarkdownFileRule {

    /** Markdown parser. */
    private static final Parser PARSER = Parser.builder().build();

    /** URI scheme pattern. */
    private static final Pattern URI_SCHEME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*:.*");

    /** Invalid path characters pattern. */
    private static final Pattern INVALID_PATH_CHARACTERS = Pattern.compile("[\\p{Cntrl}\\s]");

    @Override
    public final List<ValidationIssue> validate(final Bundle bundle, final MarkdownFile markdownFile) {
        final List<ValidationIssue> issues = new LinkedList<>();
        final Frontmatter frontmatter = markdownFile.frontmatter();

        if (!ATTESTED_COMPUTATION.equalsIgnoreCase(frontmatter.type())) {
            return issues;
        }

        validateRuntime(markdownFile, issues, frontmatter);
        validateParameters(markdownFile, issues, frontmatter);
        validateComputationPath(markdownFile, issues, frontmatter);
        validateExecutor(markdownFile, issues, frontmatter);
        validateAttester(markdownFile, issues, frontmatter);
        validateComputationDefinition(markdownFile, issues, frontmatter);

        return issues;
    }

    private void validateRuntime(final MarkdownFile markdownFile,
                                 final List<ValidationIssue> issues,
                                 final Frontmatter frontmatter) {
        if (StringUtils.isBlank(frontmatter.runtime())) {
            issues.add(issue(
                    markdownFile,
                    MISSING_COMPUTATION_RUNTIME,
                    "Missing runtime field in Attested Computation frontmatter"
            ));
        }
    }

    private void validateParameters(final MarkdownFile markdownFile,
                                    final List<ValidationIssue> issues,
                                    final Frontmatter frontmatter) {
        for (int index = 0; index < frontmatter.parameters().size(); index++) {
            final ComputationParameter parameter = frontmatter.parameters().get(index);
            if (StringUtils.isBlank(parameter.name())) {
                issues.add(issue(
                        markdownFile,
                        MISSING_COMPUTATION_PARAMETER_NAME,
                        "Missing parameters[].name field in Attested Computation frontmatter"
                ));
            }
            if (StringUtils.isBlank(parameter.type())) {
                issues.add(issue(
                        markdownFile,
                        MISSING_COMPUTATION_PARAMETER_TYPE,
                        "Missing parameters[].type field in Attested Computation frontmatter"
                ));
            }
            if (parameter.required() == null) {
                issues.add(issue(
                        markdownFile,
                        MISSING_COMPUTATION_PARAMETER_REQUIRED,
                        "Missing parameters[].required field in Attested Computation frontmatter"
                ));
            }
            if (!rawParameterRequiredIsBoolean(frontmatter, index)) {
                issues.add(issue(
                        markdownFile,
                        INVALID_COMPUTATION_PARAMETER_REQUIRED,
                        "Invalid parameters[].required field in Attested Computation frontmatter"
                ));
            }
        }
    }

    private boolean rawParameterRequiredIsBoolean(final Frontmatter frontmatter, final int index) {
        Object parameters = frontmatter.extraFields().get(PARAMETERS_KEY);
        if (!(parameters instanceof List<?> parameterValues)) {
            return true;
        }
        if (index >= parameterValues.size()) {
            return true;
        }
        Object parameterValue = parameterValues.get(index);
        if (!(parameterValue instanceof Map<?, ?> parameterMap)) {
            return true;
        }
        Object required = parameterMap.get(PARAMETER_REQUIRED_KEY);
        if (required == null) {
            return true;
        }
        return required instanceof Boolean;
    }

    private void validateComputationPath(final MarkdownFile markdownFile,
                                         final List<ValidationIssue> issues,
                                         final Frontmatter frontmatter) {
        if (StringUtils.isNotBlank(frontmatter.computation()) && !isValidPathOrUri(frontmatter.computation())) {
            issues.add(issue(
                    markdownFile,
                    INVALID_COMPUTATION_PATH,
                    "Invalid computation field in Attested Computation frontmatter"
            ));
        }
    }

    private void validateExecutor(final MarkdownFile markdownFile,
                                  final List<ValidationIssue> issues,
                                  final Frontmatter frontmatter) {
        if (frontmatter.executor() == null) {
            return;
        }
        if (StringUtils.isBlank(frontmatter.executor().resource())) {
            issues.add(issue(
                    markdownFile,
                    MISSING_COMPUTATION_EXECUTOR_RESOURCE,
                    "Missing executor.resource field in Attested Computation frontmatter"
            ));
        }
        if (frontmatter.executor().receipt().isEmpty()) {
            issues.add(issue(
                    markdownFile,
                    MISSING_COMPUTATION_EXECUTOR_RECEIPT,
                    "Missing executor.receipt field in Attested Computation frontmatter"
            ));
        }
    }

    private void validateAttester(final MarkdownFile markdownFile,
                                  final List<ValidationIssue> issues,
                                  final Frontmatter frontmatter) {
        if (frontmatter.attester() != null && StringUtils.isBlank(frontmatter.attester().resource())) {
            issues.add(issue(
                    markdownFile,
                    MISSING_COMPUTATION_ATTESTER_RESOURCE,
                    "Missing attester.resource field in Attested Computation frontmatter"
            ));
        }
    }

    private void validateComputationDefinition(final MarkdownFile markdownFile,
                                               final List<ValidationIssue> issues,
                                               final Frontmatter frontmatter) {
        final boolean frontmatterComputationPresent = StringUtils.isNotBlank(frontmatter.computation());
        final boolean bodyComputationPresent = hasComputationCodeBlock(markdownFile.body());

        if (!frontmatterComputationPresent && !bodyComputationPresent) {
            issues.add(issue(
                    markdownFile,
                    MISSING_COMPUTATION_DEFINITION,
                    "Missing computation definition in Attested Computation"
            ));
        }
        if (frontmatterComputationPresent && bodyComputationPresent) {
            issues.add(issue(
                    markdownFile,
                    DUPLICATE_COMPUTATION_DEFINITION,
                    "Computation is declared both in frontmatter and body"
            ));
        }
    }

    private boolean hasComputationCodeBlock(final String body) {
        if (StringUtils.isBlank(body)) {
            return false;
        }

        final ComputationSectionVisitor visitor = new ComputationSectionVisitor();
        PARSER.parse(body).accept(visitor);
        return visitor.computationCodeBlockFound();
    }

    private boolean isValidPathOrUri(final String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        if (INVALID_PATH_CHARACTERS.matcher(value).find()) {
            return false;
        }
        if (value.startsWith("//") || value.startsWith("#")) {
            return false;
        }
        if (URI_SCHEME_PATTERN.matcher(value).matches()) {
            return isValidAbsoluteUri(value);
        }
        return true;
    }

    private boolean isValidAbsoluteUri(final String value) {
        try {
            return new URI(value).isAbsolute();
        } catch (URISyntaxException exception) {
            return false;
        }
    }

    private ValidationIssue issue(final MarkdownFile markdownFile,
                                  final ValidationCode code,
                                  final String message) {
        return ValidationIssue.builder()
                .severity(ERROR)
                .code(code)
                .message(message)
                .path(markdownFile.relativePath())
                .build();
    }

    /**
     * Finds a code block inside the level-one Computation section.
     */
    private static final class ComputationSectionVisitor extends AbstractVisitor {

        /** Inside the level-one Computation section. */
        private boolean insideComputationSection;

        /** Code block found inside the level-one Computation section. */
        private boolean computationCodeBlockFound;

        @Override
        public void visit(final Heading heading) {
            if (heading.getLevel() == HEADING_LEVEL_1) {
                insideComputationSection = "Computation".equals(headingText(heading));
            }
            visitChildren(heading);
        }

        @Override
        public void visit(final FencedCodeBlock fencedCodeBlock) {
            if (insideComputationSection) {
                computationCodeBlockFound = true;
            }
        }

        @Override
        public void visit(final IndentedCodeBlock indentedCodeBlock) {
            if (insideComputationSection) {
                computationCodeBlockFound = true;
            }
        }

        boolean computationCodeBlockFound() {
            return computationCodeBlockFound;
        }

        private String headingText(final Heading heading) {
            Node child = heading.getFirstChild();
            if (child instanceof Text text) {
                return text.getLiteral();
            }
            return "";
        }
    }

}
