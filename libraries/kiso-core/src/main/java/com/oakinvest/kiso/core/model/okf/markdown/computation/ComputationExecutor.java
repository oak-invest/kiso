package com.oakinvest.kiso.core.model.okf.markdown.computation;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Run instructions for an attested computation.
 *
 * @param resource path to the run instructions or code
 * @param receipt  fields a run must return for attestation
 */
@Builder
@SuppressWarnings("unused")
public record ComputationExecutor(
        @Nullable String resource,
        List<String> receipt
) {

    /**
     * Creates an executor with safe default values.
     */
    public ComputationExecutor {
        receipt = Objects.requireNonNullElse(receipt, List.of());
    }

}
