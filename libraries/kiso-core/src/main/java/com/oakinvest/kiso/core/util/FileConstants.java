package com.oakinvest.kiso.core.util;


import lombok.experimental.UtilityClass;

import java.nio.file.Path;

/**
 * Filenames constants.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public final class FileConstants {

    /** Recursive directory pattern. */
    public static final String RECURSIVE_DIRECTORY_PATTERN = "/**";

    /** Assets directory. */
    public static final String ASSETS_DIRECTORY = "assets";

    /** Kiso configuration directory. */
    public static final String CONFIGURATION_DIRECTORY_NAME = ".kiso";

    /** Kiso configuration file name. */
    public static final String CONFIGURATION_FILE_NAME = "configuration.yaml";

    /** Kiso configuration directory. */
    public static final Path CONFIGURATION_DIRECTORY = Path.of(CONFIGURATION_DIRECTORY_NAME);

    /** Kiso configuration file. */
    public static final Path CONFIGURATION_FILE = CONFIGURATION_DIRECTORY.resolve(CONFIGURATION_FILE_NAME);

    /** Filename for llms.txt. */
    public static final String LLMS_TXT_FILENAME = "llms.txt";

    /** Filename for sitemap.xml. */
    public static final String SITEMAP_XML_FILENAME = "sitemap.xml";

    /** Filename for the search index JSON file. */
    public static final String SEARCH_INDEX_JSON_FILENAME = "search-index.json";

}
