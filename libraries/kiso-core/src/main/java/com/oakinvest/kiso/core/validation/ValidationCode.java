package com.oakinvest.kiso.core.validation;

/**
 * Validation code.
 */
public enum ValidationCode {

    /** Invalid encoding. */
    INVALID_ENCODING,

    /** Missing frontmatter. */
    MISSING_FRONTMATTER,

    /** Unexpected frontmatter. */
    UNEXPECTED_FRONTMATTER,

    /** Missing type field in frontmatter. */
    MISSING_FRONTMATTER_TYPE,

    /** Invalid timestamp field in frontmatter - Not an ISO 8601 datetime format. */
    INVALID_TIMESTAMP,

    /** Missing title field in frontmatter. */
    MISSING_GENERATED_BY,

    /** Missing generated.at field in frontmatter. */
    MISSING_GENERATED_AT,

    /** Invalid generated.at field in frontmatter - Not an ISO 8601 datetime format. */
    INVALID_GENERATED_AT,

    /** Missing verified.by field in frontmatter. */
    MISSING_VERIFIED_BY,

    /** Missing verified.at field in frontmatter. */
    MISSING_VERIFIED_AT,

    /** Invalid verified.at field in frontmatter - Not an ISO 8601 datetime format. */
    INVALID_VERIFIED_AT,

    /** Broken local link. */
    BROKEN_LINK

}
