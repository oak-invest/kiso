package com.oakinvest.kiso.core.model.markdown;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * Semantic kind of Markdown file inside an OKF bundle.
 */
@SuppressWarnings("unused")
public enum MarkdownFileKind {

    /** Standard OKF concept document. */
    CONCEPT(null),

    /**
     * Directory index document (index.md).
     * An index.md file MAY appear in any directory, including the bundle rootBundleDirectory. It enumerates the directory's contents
     * to support progressive disclosure — letting a human or agent see what is available before opening individual
     * documents.
     *
     */
    INDEX("index.md"),

    /**
     * Directory update log document (log.md).
     * A log.md file MAY appear at any level of the hierarchy to record the history of changes to that scope.
     *
     */
    LOG("log.md");

    /** Filename corresponding for the Markdown file kind. */
    private final String fileName;

    /**
     * Constructor.
     *
     * @param newFileName filename corresponding to the Markdown file kind, or null if the kind does not have a specific filename
     */
    MarkdownFileKind(final String newFileName) {
        this.fileName = newFileName;
    }

    /**
     * Returns the Markdown kind from the path.
     *
     * @param path file path
     * @return Markdown kind
     */
    public static MarkdownFileKind from(final Path path) {
        final String name = path.getFileName().toString();

        return Arrays.stream(values())
                .filter(kind -> StringUtils.equalsIgnoreCase(kind.fileName, name))
                .findFirst()
                .orElse(CONCEPT);
    }

}
