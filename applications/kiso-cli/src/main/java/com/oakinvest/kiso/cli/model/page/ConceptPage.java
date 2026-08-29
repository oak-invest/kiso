package com.oakinvest.kiso.cli.model.page;

import com.oakinvest.kiso.cli.model.util.PageContext;
import com.oakinvest.kiso.cli.model.util.PageMetadata;
import com.oakinvest.kiso.core.model.markdown.computation.ComputationAttester;
import com.oakinvest.kiso.core.model.markdown.computation.ComputationExecutor;
import com.oakinvest.kiso.core.model.markdown.computation.ComputationParameter;
import com.oakinvest.kiso.core.model.markdown.provenance.Source;
import com.oakinvest.kiso.core.model.markdown.provenance.UsageWindow;
import com.oakinvest.kiso.core.model.markdown.trust.TrustEvent;
import com.oakinvest.kiso.core.util.types.LifecycleStatus;
import com.oakinvest.kiso.core.util.types.TrustLevel;
import gg.jte.html.HtmlContent;
import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.oakinvest.kiso.core.util.contants.ConceptTypeConstants.ATTESTED_COMPUTATION;
import static com.oakinvest.kiso.core.util.types.LifecycleStatus.STABLE;
import static com.oakinvest.kiso.core.util.types.TrustLevel.UNVERIFIED;

/**
 * Concept page.
 *
 * @param context     the context of the page
 * @param metadata    the metadata of the page
 * @param type        the type of the page
 * @param resource    a URI that uniquely identifies the underlying asset the concept describes. Absent for concepts that describe abstract ideas rather than physical resources
 * @param tags        a YAML list of short strings for cross-cutting categorization
 * @param generatedBy the entity that generated the content
 * @param generatedAt the timestamp when the content was generated
 * @param timestamp   ISO 8601 datetime of the last meaningful change
 * @param sources     materials the concept derives from
 * @param usageWindow date range that frames source usage counts
 * @param generated   records how the current content was produced
 * @param verified    verification events
 * @param trustTier   trust tier inferred from verification events
 * @param status      lifecycle status
 * @param staleAfter  absolute date after which content is stale
 * @param runtime     computation runtime
 * @param parameters  computation parameters
 * @param computation path to a file holding the computation
 * @param executor    computation executor
 * @param attester    computation attester
 * @param htmlContent the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record ConceptPage(
        PageContext context,
        PageMetadata metadata,
        String type,
        @Nullable String resource,
        List<String> tags,
        @Nullable String generatedBy,
        @Nullable OffsetDateTime generatedAt,
        @Nullable OffsetDateTime timestamp,
        List<Source> sources,
        @Nullable UsageWindow usageWindow,
        @Nullable TrustEvent generated,
        List<TrustEvent> verified,
        TrustLevel trustTier,
        LifecycleStatus status,
        @Nullable String staleAfter,
        @Nullable String runtime,
        List<ComputationParameter> parameters,
        @Nullable String computation,
        @Nullable ComputationExecutor executor,
        @Nullable ComputationAttester attester,
        @Nullable HtmlContent htmlContent
) {

    /**
     * Constructor with safe default values.
     */
    public ConceptPage {
        context = Objects.requireNonNullElse(context, PageContext.empty());
        metadata = Objects.requireNonNullElse(metadata, PageMetadata.empty());
        tags = Objects.requireNonNullElse(tags, List.of());
        sources = Objects.requireNonNullElse(sources, List.of());
        verified = Objects.requireNonNullElse(verified, List.of());
        trustTier = Objects.requireNonNullElse(trustTier, UNVERIFIED);
        status = Objects.requireNonNullElse(status, STABLE);
        parameters = Objects.requireNonNullElse(parameters, List.of());
    }

    /**
     * Returns true when this page represents an Attested Computation concept.
     *
     * @return true for Attested Computation concepts
     */
    public boolean attestedComputation() {
        return ATTESTED_COMPUTATION.equalsIgnoreCase(type);
    }

    /**
     * Returns the trust tier as a frontmatter-compatible display value.
     *
     * @return trust tier display value
     */
    public String trustTierValue() {
        return trustTier.name().toLowerCase().replace('_', '-');
    }

    /**
     * Returns the context of the page (and specify @NonNull).
     *
     * @return the context of the page
     */
    @Override
    public @NonNull PageContext context() {
        return context;
    }

    /**
     * Returns the metadata of the page (and specify @NonNull).
     *
     * @return the metadata of the page
     */
    @Override
    public @NonNull PageMetadata metadata() {
        return metadata;
    }

}
