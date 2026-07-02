package com.oakinvest.kiso.core.validation;

/**
 * Validation code.
 */
public enum ValidationCode {

    /** Invalid encoding. */
    INVALID_ENCODING,

    /** Missing frontmatter. */
    MISSING_FRONTMATTER,

    /** Missing type field in frontmatter. */
    MISSING_FRONTMATTER_TYPE,

    /** Invalid timestamp field in frontmatter - Not a ISO 8601 datetime format. */
    INVALID_TIMESTAMP

}
