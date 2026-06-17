package com.oakinvest.kiso.core.model.markdown;

/**
 * Semantic kind of Markdown file inside an OKF bundle.
 */
public enum MarkdownFileKind {

    /** Standard OKF concept document. */
    CONCEPT,

    /**
     * Directory index document (index.md).
     * An index.md file MAY appear in any directory, including the bundle root. It enumerates the directory's contents
     * to support progressive disclosure — letting a human or agent see what is available before opening individual
     * documents.
     * */
    INDEX,

    /**
     * Directory update log document (log.md).
     * A log.md file MAY appear at any level of the hierarchy to record the history of changes to that scope.
     * */
    LOG

}
