package com.oakinvest.kiso.core.exception;

/**
 * Knowledge bundle load exception.
 */
public final class KnowledgeBundleLoadException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message exception message
     */
    public KnowledgeBundleLoadException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public KnowledgeBundleLoadException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
