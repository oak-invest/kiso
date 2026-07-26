package com.oakinvest.kiso.core.exception;

/**
 * Exception thrown when social preview image generation fails.
 */
public class SocialPreviewGenerationException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message the error message
     * @param cause   the underlying exception
     */
    public SocialPreviewGenerationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
