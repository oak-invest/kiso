package com.oakinvest.kiso.cli.configuration;

/**
 * Kiso-cli configuration loading exception.
 */
public final class KisoCliConfigurationLoadingException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message message
     */
    public KisoCliConfigurationLoadingException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public KisoCliConfigurationLoadingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
