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
    BROKEN_LINK,

    /** Invalid OKF version. */
    INVALID_OKF_VERSION,

    /** Invalid log date format - Not an ISO 8601 date. */
    INVALID_LOG_DATE_FORMAT,

    /** Missing sources[].resource field in frontmatter. */
    MISSING_SOURCE_RESOURCE,

    /** Invalid sources[].last_modified field in frontmatter - Not an ISO 8601 date. */
    INVALID_SOURCE_LAST_MODIFIED

}
