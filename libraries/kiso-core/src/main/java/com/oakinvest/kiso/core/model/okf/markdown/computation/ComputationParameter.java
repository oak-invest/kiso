package com.oakinvest.kiso.core.model.okf.markdown.computation;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

/**
 * Typed, named hole that an agent may fill when running an attested computation.
 *
 * @param name     parameter name
 * @param type     parameter type interpreted by the runtime
 * @param required whether the parameter is required
 */
@Builder
@SuppressWarnings("unused")
public record ComputationParameter(
        @Nullable String name,
        @Nullable String type,
        @Nullable Boolean required
) {
}
