package com.oakinvest.kiso.cli.exception;

/**
 * Exception thrown when social preview image generation fails.
 */
public class SocialPreviewGenerationException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public SocialPreviewGenerationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
