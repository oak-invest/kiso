package com.oakinvest.kiso.core.model.okf.markdown.computation;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

/**
 * Deterministic check used to inspect a computation receipt and return a verdict.
 *
 * @param resource path to the attester code
 */
@Builder
@SuppressWarnings("unused")
public record ComputationAttester(
        @Nullable String resource
) {
}
