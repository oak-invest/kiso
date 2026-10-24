package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Link;
import org.commonmark.parser.Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.oakinvest.kiso.core.validation.ValidationCode.BROKEN_LINK;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;

/**
 * Check broken links.
 */
public class BrokenLinkRule implements MarkdownFileRule {

    /** Markdown parser. */
    private static final Parser PARSER = Parser.builder().build();

    /** URI scheme pattern. */
    private static final Pattern URI_SCHEME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*:.*");

    @Override
    public final List<ValidationIssue> validate(final KnowledgeBundle knowledgeBundle, final MarkdownFile markdownFile) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        final Path rootPath = knowledgeBundle.rootBundle().absolutePath().toAbsolutePath().normalize();

        // Getting all the links inside the markdown content ===========================================================
        return getLocalLinks(markdownFile).stream()
                .filter(link -> !targetExists(rootPath, markdownFile, link.getDestination()))
                .map(link -> brokenLinkIssue(markdownFile, link))
                .toList();
    }

    /**
     * Returns all local links in a markdown file.
     *
     * @param markdownFile Markdown file
     * @return local links
     */
    private List<Link> getLocalLinks(final MarkdownFile markdownFile) {
        if (markdownFile == null || StringUtils.isBlank(markdownFile.body())) {
            return List.of();
        }

        // We parse the content with a visitor to get all links.
        final List<Link> links = new LinkedList<>();
        PARSER.parse(markdownFile.body()).accept(new AbstractVisitor() {

            @Override
            public void visit(final Link link) {
                if (isLocalLink(link.getDestination())) {
                    links.add(link);
                }
                visitChildren(link);
            }
        });
        return links;
    }

    private ValidationIssue brokenLinkIssue(final MarkdownFile markdownFile, final Link link) {
        return ValidationIssue.builder()
                .severity(WARNING)
                .code(BROKEN_LINK)
                .message("File " + markdownFile.relativePath() + " contains a broken link: " + link.getDestination())
                .path(markdownFile.relativePath())
                .build();
    }

    private boolean isLocalLink(final String destination) {
        if (StringUtils.isBlank(destination)) {
            return false;
        }

        final String cleanedDestination = cleanDestination(destination);
        if (StringUtils.isBlank(cleanedDestination)) {
            return false;
        }

        return !cleanedDestination.startsWith("#")
                && !cleanedDestination.startsWith("//")
                && !URI_SCHEME_PATTERN.matcher(cleanedDestination).matches();
    }

    private boolean targetExists(final Path rootPath, final MarkdownFile markdownFile, final String destination) {
        final String cleanedDestination = cleanDestination(destination);
        final Path targetPath;
        if (cleanedDestination.startsWith("/")) {
            targetPath = rootPath.resolve(cleanedDestination.substring(1)).normalize();
        } else {
            final Path parentPath = rootPath.resolve(markdownFile.relativePath()).getParent();
            targetPath = parentPath.resolve(cleanedDestination).normalize();
        }
        return targetPath.startsWith(rootPath) && Files.isRegularFile(targetPath);
    }

    private String cleanDestination(final String destination) {
        String cleanedDestination = destination.strip();

        int fragmentIndex = cleanedDestination.indexOf('#');
        if (fragmentIndex >= 0) {
            cleanedDestination = cleanedDestination.substring(0, fragmentIndex);
        }

        int queryIndex = cleanedDestination.indexOf('?');
        if (queryIndex >= 0) {
            cleanedDestination = cleanedDestination.substring(0, queryIndex);
        }

        return cleanedDestination;
    }

}
