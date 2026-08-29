package com.oakinvest.kiso.core.exception;

/**
 * Exception thrown when a knowledge bundle fails to load.
 */
public final class KnowledgeBundleLoadingException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message exception message
     */
    public KnowledgeBundleLoadingException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public KnowledgeBundleLoadingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
