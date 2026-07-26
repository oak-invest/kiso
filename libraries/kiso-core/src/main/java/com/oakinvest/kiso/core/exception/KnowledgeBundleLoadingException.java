package com.oakinvest.kiso.core.exception;

/**
 * Exception thrown when a knowledge bundle fails to load.
 */
public final class KnowledgeBundleLoadingException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message the error message
     */
    public KnowledgeBundleLoadingException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message the error message
     * @param cause   the underlying exception
     */
    public KnowledgeBundleLoadingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
