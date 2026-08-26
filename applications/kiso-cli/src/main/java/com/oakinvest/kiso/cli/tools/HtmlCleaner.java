package com.oakinvest.kiso.cli.tools;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * HTML Cleaner.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class HtmlCleaner {

    /** HTML compressor. */
    private static final HtmlCompressor HTML_COMPRESSOR = createHtmlCompressor();

    /**
     * Creates the configured HTML compressor.
     *
     * @return configured HTML compressor
     */
    private static HtmlCompressor createHtmlCompressor() {
        HtmlCompressor htmlCompressor = new HtmlCompressor();
        htmlCompressor.setRemoveComments(true);
        htmlCompressor.setRemoveMultiSpaces(true);
        htmlCompressor.setRemoveIntertagSpaces(false);
        htmlCompressor.setCompressCss(false);
        htmlCompressor.setCompressJavaScript(false);
        return htmlCompressor;
    }

    /**
     * Cleans the HTML content.
     *
     * @param htmlContent HTML content
     * @return cleaned HTML content
     */
    public static String clean(final String htmlContent) {
        Document document = Jsoup.parse(HTML_COMPRESSOR.compress(htmlContent));
        document.outputSettings()
                .prettyPrint(true)
                .indentAmount(1)
                .maxPaddingWidth(Integer.MAX_VALUE);
        return document.outerHtml();
    }

}
