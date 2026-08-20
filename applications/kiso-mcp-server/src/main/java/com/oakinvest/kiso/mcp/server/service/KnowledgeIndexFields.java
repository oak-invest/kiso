package com.oakinvest.kiso.mcp.server.service;

import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * Knowledge index fields.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "unused"})
public class KnowledgeIndexFields {

    /** Identifier. */
    public static final String CONCEPT_ID = "conceptId";

    /** Title. */
    public static final String TITLE = "title";

    /** Description. */
    public static final String DESCRIPTION = "description";

    /** Tags. */
    public static final String TAGS = "tags";

    /** Body. */
    public static final String BODY = "body";

    /** All fields. */
    public static final String[] FIELDS = {
            TITLE,
            DESCRIPTION,
            TAGS,
            BODY
    };

    /** Field boost factors. */
    public static final Map<String, Float> FIELDS_BOOSTS = Map.of(
            TITLE, 5.0f,
            DESCRIPTION, 2.0f,
            TAGS, 2.0f,
            BODY, 1.0f
    );

}
