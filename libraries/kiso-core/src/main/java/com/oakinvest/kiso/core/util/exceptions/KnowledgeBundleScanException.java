package com.oakinvest.kiso.core.util.exceptions;

/**
 * Knowledge bundle load exception.
 */
public class KnowledgeBundleScanException extends RuntimeException {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param message exception message
     */
    public KnowledgeBundleScanException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public KnowledgeBundleScanException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
