package com.oakinvest.kiso.core.renderer.util;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * HTML Cleaner.
 */
public class HtmlCleaner {

    /** HTML compressor. */
    private final HtmlCompressor compressor;

    /**
     * Constructor.
     */
    public HtmlCleaner() {
        compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveMultiSpaces(true);
        compressor.setRemoveIntertagSpaces(false);
        compressor.setCompressCss(false);
        compressor.setCompressJavaScript(false);
    }

    /**
     * Cleans the HTML content.
     *
     * @param htmlContent HTML content
     * @return cleaned HTML content
     */
    public String clean(final String htmlContent) {
        Document document = Jsoup.parse(compressor.compress(htmlContent));
        document.outputSettings()
                .prettyPrint(true)
                .indentAmount(1)
                .maxPaddingWidth(Integer.MAX_VALUE);
        return document.outerHtml();
    }

}
