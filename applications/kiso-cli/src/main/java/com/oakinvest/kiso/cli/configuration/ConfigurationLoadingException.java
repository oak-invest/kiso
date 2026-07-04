package com.oakinvest.kiso.cli.configuration;

/**
 * Kiso-cli configuration loading exception.
 */
public final class ConfigurationLoadingException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message message
     */
    public ConfigurationLoadingException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ConfigurationLoadingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
