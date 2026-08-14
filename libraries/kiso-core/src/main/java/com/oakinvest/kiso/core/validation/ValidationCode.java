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
    INVALID_SOURCE_LAST_MODIFIED,

    /** Missing runtime field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_RUNTIME,

    /** Missing parameters[].name field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_PARAMETER_NAME,

    /** Missing parameters[].type field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_PARAMETER_TYPE,

    /** Missing parameters[].required field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_PARAMETER_REQUIRED,

    /** Invalid parameters[].required field in Attested Computation frontmatter. */
    INVALID_COMPUTATION_PARAMETER_REQUIRED,

    /** Invalid computation path or URI in Attested Computation frontmatter. */
    INVALID_COMPUTATION_PATH,

    /** Missing executor.resource field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_EXECUTOR_RESOURCE,

    /** Missing executor.receipt field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_EXECUTOR_RECEIPT,

    /** Missing attester.resource field in Attested Computation frontmatter. */
    MISSING_COMPUTATION_ATTESTER_RESOURCE,

    /** Missing computation body or frontmatter path in Attested Computation. */
    MISSING_COMPUTATION_DEFINITION,

    /** Computation is declared both in frontmatter and body. */
    DUPLICATE_COMPUTATION_DEFINITION

}
