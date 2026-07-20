package com.oakinvest.kiso.core.exception;

/**
 * Exception thrown when social preview image generation fails.
 */
public class SocialPreviewGenerationException extends RuntimeException {

    /**
     * Creates a new exception with the given message and cause.
     *
     * @param message the error message
     * @param cause   the underlying exception
     */
    public SocialPreviewGenerationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
