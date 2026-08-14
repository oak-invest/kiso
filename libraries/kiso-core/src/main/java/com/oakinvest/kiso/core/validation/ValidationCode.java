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

    /** Missing <code>verified.by</code> field in frontmatter. */
    MISSING_VERIFIED_BY,

    /** Missing <code>verified.at</code> field in frontmatter. */
    MISSING_VERIFIED_AT,

    /** Invalid <code>verified.at</code> field in frontmatter - Not an ISO 8601 datetime format. */
    INVALID_VERIFIED_AT,

    /** Broken local link. */
    BROKEN_LINK,

    /** Invalid OKF version. */
    INVALID_OKF_VERSION,

    /** Invalid log date format - Not an ISO 8601 date. */
    INVALID_LOG_DATE_FORMAT,

    /** Missing <code>sources[].resource</code> field in frontmatter. */
    MISSING_SOURCE_RESOURCE,

    /** Invalid <code>sources[].last_modified</code> field in frontmatter - Not an ISO 8601 date. */
    INVALID_SOURCE_LAST_MODIFIED,

    /** Missing runtime field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_RUNTIME,

    /** Missing <code>parameters[].name</code> field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_PARAMETER_NAME,

    /** Missing <code>parameters[].type</code> field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_PARAMETER_TYPE,

    /** Missing <code>parameters[].required</code> field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_PARAMETER_REQUIRED,

    /** Invalid <code>parameters[].required</code> field in Attested Computation frontmatter. */
    INVALID_COMPUTATION_PARAMETER_REQUIRED,

    /** Invalid computation path or URI in Attested Computation frontmatter. */
    INVALID_COMPUTATION_PATH,

    /** Missing <code>executor.resource</code> field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_EXECUTOR_RESOURCE,

    /** Missing <code>executor.receipt</code> field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_EXECUTOR_RECEIPT,

    /** Missing <code>attester.resource</code> field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_ATTESTER_RESOURCE,

    /** Missing computation body or frontmatter path in Attested Computation. */
    MISSING_COMPUTATION_DEFINITION,

    /** Computation is declared both in frontmatter and body. */
    DUPLICATE_COMPUTATION_DEFINITION,

    /** Invalid <code>usage_window.from</code> field in frontmatter - Not an ISO 8601 date. */
    INVALID_USAGE_WINDOW_FROM,

    /** Invalid <code>usage_window.to</code> field in frontmatter - Not an ISO 8601 date. */
    INVALID_USAGE_WINDOW_TO

}
