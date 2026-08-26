package com.oakinvest.kiso.core.model.markdown;

import com.oakinvest.kiso.core.model.markdown.computation.ComputationAttester;
import com.oakinvest.kiso.core.model.markdown.computation.ComputationExecutor;
import com.oakinvest.kiso.core.model.markdown.computation.ComputationParameter;
import com.oakinvest.kiso.core.model.markdown.provenance.Source;
import com.oakinvest.kiso.core.model.markdown.provenance.UsageWindow;
import com.oakinvest.kiso.core.model.markdown.trust.TrustEvent;
import com.oakinvest.kiso.core.util.types.LifecycleStatus;
import com.oakinvest.kiso.core.util.types.TrustLevel;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Frontmatter associated with a Markdown file.
 *
 * @param type        A short string identifying the kind of concept. Consumers use this for routing, filtering, and presentation. Example values: BigQuery Table, BigQuery Dataset, API Endpoint, Metric, Playbook, Reference
 * @param title       Human-readable display name. If omitted, consumers MAY derive a title from the filename
 * @param description A single sentence summarizing the concept. Used by index.md generators, search snippets, and previews
 * @param resource    A URI that uniquely identifies the underlying asset the concept describes. Absent for concepts that describe abstract ideas rather than physical resources
 * @param tags        A YAML list of short strings for cross-cutting categorization
 * @param tagSlugs    A YAML list of short strings representing the slugs of the tags
 * @param timestamp   ISO 8601 datetime of the last meaningful change - DEPRECATED SINCE v0.2
 * @param sources     materials the concept derives from
 * @param usageWindow date range that frames source usage counts
 * @param generated   event recording how the current content was produced
 * @param verified    A list of verification events
 * @param status      lifecycle status value.
 * @param staleAfter  absolute date after which content is stale
 * @param runtime     computation runtime for Attested Computation concepts
 * @param parameters  typed, named holes the agent may fill for an Attested Computation
 * @param computation path to a file holding the computation
 * @param executor    run instructions for an Attested Computation
 * @param attester    deterministic check for an Attested Computation
 * @param extraFields producer-defined fields not modeled by OKF
 */
@Builder
@SuppressWarnings("unused")
public record Frontmatter(
        @Nullable String type,
        @Nullable String title,
        @Nullable String description,
        @Nullable String resource,
        List<String> tags,
        List<String> tagSlugs,
        @Nullable String timestamp,
        List<Source> sources,
        @Nullable UsageWindow usageWindow,
        @Nullable TrustEvent generated,
        List<TrustEvent> verified,
        LifecycleStatus status,
        @Nullable String staleAfter,
        @Nullable String runtime,
        List<ComputationParameter> parameters,
        @Nullable String computation,
        @Nullable ComputationExecutor executor,
        @Nullable ComputationAttester attester,
        Map<String, Object> extraFields
) {

    /**
     * Creates a Frontmatter with safe default values.
     */
    public Frontmatter {
        tags = Objects.requireNonNullElse(tags, List.of());
        tagSlugs = Objects.requireNonNullElse(tagSlugs, List.of());
        sources = Objects.requireNonNullElse(sources, List.of());
        verified = Objects.requireNonNullElse(verified, List.of());
        status = Objects.requireNonNullElse(status, LifecycleStatus.STABLE);
        parameters = Objects.requireNonNullElse(parameters, List.of());
        extraFields = Objects.requireNonNullElse(extraFields, Map.of());
    }

    /**
     * Empty Frontmatter.
     *
     * @return empty frontmatter
     */
    public static Frontmatter empty() {
        return Frontmatter.builder()
                .tags(List.of())
                .sources(List.of())
                .verified(List.of())
                .parameters(List.of())
                .extraFields(Map.of())
                .build();
    }

    /**
     * Returns the actor that generated the content.
     *
     * @return actor that generated the content or null if not available
     */
    public @Nullable String generatedBy() {
        if (generated == null) {
            return null;
        }
        if (generated.by() == null) {
            return null;
        }
        return generated.by().identifier();
    }

    /**
     * Returns the ISO 8601 datetime of the generation.
     *
     * @return ISO 8601 datetime of the generation or null if not available
     */
    public @Nullable OffsetDateTime generatedAt() {
        if (generated == null) {
            return null;
        }
        return generated.parsedAt();
    }

    /**
     * Returns parsed timestamp.
     *
     * @return timestamp as OffsetDateTime or null if the timestamp is blank or not parsable
     */
    public @Nullable OffsetDateTime parsedTimestamp() {
        if (StringUtils.isBlank(timestamp)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(timestamp);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Returns parsed stale date.
     *
     * @return stale date as LocalDate or null if the value is blank or not parsable
     */
    public @Nullable LocalDate parsedStaleAfter() {
        if (StringUtils.isBlank(staleAfter)) {
            return null;
        }

        try {
            return LocalDate.parse(staleAfter);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Returns lifecycle status.
     *
     * @return lifecycle status, stable when missing or unknown
     */
    public LifecycleStatus lifecycleStatus() {
        return status;
    }

    /**
     * Returns trust tier inferred from verification events.
     *
     * @return inferred trust tier
     */
    public TrustLevel trustTier() {
        if (verified.isEmpty()) {
            return TrustLevel.UNVERIFIED;
        }
        boolean humanReviewed = verified.stream()
                .map(TrustEvent::by)
                .filter(Objects::nonNull)
                .anyMatch(Actor::isHuman);
        if (humanReviewed) {
            return TrustLevel.HUMAN_REVIEWED;
        }
        return TrustLevel.MACHINE_CONFIRMED;
    }

}
