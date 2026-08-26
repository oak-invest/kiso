package com.oakinvest.kiso.cli.util.contants;

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

    /** JTE precompiled social preview template class. */
    public static final String PRECOMPILED_SOCIAL_PREVIEW_TEMPLATE_CLASS = "gg.jte.generated.precompiled.JtesocialpreviewsvgGenerated";

    /** JTE source templates directory when tests are launched directly from the repository isRoot. */
    public static final Path ROOT_SOURCE_TEMPLATES_DIRECTORY = Path.of("libraries/kiso-core/src/main/jte");

    /** JTE source templates directory when tests are launched directly from the module directory. */
    public static final Path MODULE_SOURCE_TEMPLATES_DIRECTORY = Path.of("src/main/jte");

    /** Index template page. */
    public static final String INDEX_TEMPLATE_PAGE = "index.jte";

    /** Concept template page. */
    public static final String CONCEPT_TEMPLATE_PAGE = "concept.jte";

    /** Log template page. */
    public static final String LOG_TEMPLATE_PAGE = "log.jte";

    /** Social preview template image. */
    public static final String SOCIAL_PREVIEW_TEMPLATE_IMAGE = "social-preview.svg.jte";

}
