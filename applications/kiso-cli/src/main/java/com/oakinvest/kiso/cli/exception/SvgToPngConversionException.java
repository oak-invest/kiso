package com.oakinvest.kiso.cli.exception;

/**
 * Exception thrown when SVG to PNG conversion fails.
 */
public class SvgToPngConversionException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public SvgToPngConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
