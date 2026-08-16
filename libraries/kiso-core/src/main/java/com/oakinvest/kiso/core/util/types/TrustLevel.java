package com.oakinvest.kiso.core.util.types;

/**
 * A level derived from a concept's verified field: unverified, machine-confirmed, or human-reviewed.
 */
public enum TrustLevel {

    /** No verification event is present. */
    UNVERIFIED,

    /** Verification events exist, but none comes from a human actor. */
    MACHINE_CONFIRMED,

    /** At least one verification event comes from a human actor. */
    HUMAN_REVIEWED

}
