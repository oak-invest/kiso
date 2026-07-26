package com.oakinvest.kiso.core.exception;

/**
 * Exception thrown when SVG to PNG conversion fails.
 */
public class SvgToPngConversionException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message the error message
     * @param cause   the underlying exception
     */
    public SvgToPngConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
