package com.oakinvest.kiso.core.model.okf.markdown;

import com.oakinvest.kiso.core.util.ActorType;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import static com.oakinvest.kiso.core.util.ActorConstants.HUMAN_PREFIX;
import static com.oakinvest.kiso.core.util.ActorConstants.PROCESS_PREFIX;
import static com.oakinvest.kiso.core.util.ActorConstants.PRODUCER_VERSION_SEPARATOR;

/**
 * Identifier who or what performed an action, using the convention producer/version for agents, human:id for people,
 * and process:id for automated processes.
 *
 * @param identifier actor value following the OKF actor convention
 */
@Builder
@SuppressWarnings("unused")
public record Actor(
        String identifier
) {

    /**
     * Creates an actor from a value.
     *
     * @param value actor value
     * @return actor, or null when the value is blank
     */
    public static @Nullable Actor of(@Nullable final String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new Actor(value);
    }

    /**
     * Returns true when this actor is a human.
     *
     * @return true when this actor is a human
     */
    public boolean isHuman() {
        return Strings.CI.startsWith(identifier, HUMAN_PREFIX);
    }

    /**
     * Returns true when this actor is an automated process.
     *
     * @return true when this actor is an automated process
     */
    public boolean isProcess() {
        return Strings.CI.startsWith(identifier, PROCESS_PREFIX);
    }

    /**
     * Returns true when this actor is an agent or tool.
     *
     * @return true when this actor is an agent or tool
     */
    public boolean isAgent() {
        return !isHuman() && !isProcess() && Strings.CS.contains(identifier, PRODUCER_VERSION_SEPARATOR);
    }

    /**
     * Returns true when this actor is an agent or tool producer.
     *
     * @return true when this actor is an agent or tool producer
     */
    public boolean isProducer() {
        return isAgent();
    }

    /**
     * Returns the actor type.
     *
     * @return actor type
     */
    public ActorType type() {
        if (isHuman()) {
            return ActorType.HUMAN;
        }
        if (isProcess()) {
            return ActorType.PROCESS;
        }
        if (isAgent()) {
            return ActorType.AGENT;
        }
        return ActorType.UNKNOWN;
    }

}
