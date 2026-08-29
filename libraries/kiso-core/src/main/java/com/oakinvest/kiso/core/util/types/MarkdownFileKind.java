package com.oakinvest.kiso.core.util.types;

import lombok.Getter;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * Semantic kind of Markdown file inside an Open Knowledge Format bundle.
 */
@SuppressWarnings("unused")
public enum MarkdownFileKind {

    /** Standard Open Knowledge Format concept document. */
    CONCEPT(null),

    /**
     * Directory listing document (index.md).
     * An index.md file MAY appear in any directory, including the bundle root. It lists the directory's contents
     * to support progressive disclosure - letting a human or agent see what is available before opening individual
     * documents.
     */
    INDEX("index.md"),

    /**
     * Update history document (log.md).
     * A log.md file MAY appear at any level of the hierarchy to record the history of changes to that scope.
     */
    LOG("log.md");

    /** Filename corresponding to the Markdown file kind. */
    @Getter
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
     * Returns the Markdown kind from a path.
     *
     * @param path file path
     * @return Markdown kind
     */
    public static MarkdownFileKind from(@Nullable final Path path) {
        if (path == null) {
            return CONCEPT;
        } else {
            final String name = path.getFileName().toString();
            return Arrays.stream(values())
                    .filter(kind -> Strings.CI.equals(kind.fileName, name))
                    .findFirst()
                    .orElse(CONCEPT);
        }
    }

}
