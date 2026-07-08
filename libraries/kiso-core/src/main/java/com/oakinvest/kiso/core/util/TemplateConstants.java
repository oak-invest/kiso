package com.oakinvest.kiso.core.util;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;

/**
 * Template constants.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class TemplateConstants {

    /** JTE precompiled index template class. */
    public static final String PRECOMPILED_INDEX_TEMPLATE_CLASS = "gg.jte.generated.precompiled.JteindexGenerated";

    /** JTE source templates directory when tests are launched directly from the repository isRoot. */
    public static final Path ROOT_SOURCE_TEMPLATES_DIRECTORY = Path.of("libraries/kiso-core/src/main/jte");

    /** JTE source templates directory when tests are launched directly from the module directory. */
    public static final Path MODULE_SOURCE_TEMPLATES_DIRECTORY = Path.of("src/main/jte");

    /** Index template page. */
    public static final String INDEX_TEMPLATE_PAGE = "index.jte";

    /** Concept template page. */
    public static final String CONCEPT_TEMPLATE_PAGE = "concept.jte";

}
