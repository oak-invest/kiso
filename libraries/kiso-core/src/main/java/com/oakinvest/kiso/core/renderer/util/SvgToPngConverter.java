package com.oakinvest.kiso.core.renderer.util;

import com.oakinvest.kiso.core.exception.SvgToPngConversionException;
import lombok.experimental.UtilityClass;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Converts SVG files to PNG format using Apache Batik.
 * Maintains the original SVG dimensions in the generated PNG.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class SvgToPngConverter {

    /**
     * Converts an SVG file to PNG format.
     * The PNG will maintain the same dimensions as specified in the SVG.
     *
     * @param svgPath path to the source SVG file
     * @param pngPath path where the PNG file will be written
     * @param width   width of the output PNG in pixels
     * @param height  height of the output PNG in pixels
     * @throws SvgToPngConversionException if the conversion fails
     */
    public static void convert(final Path svgPath, final Path pngPath, final int width, final int height) {
        try {
            convertSvgToPng(svgPath, pngPath, width, height);
        } catch (IOException exception) {
            throw new SvgToPngConversionException(
                    "Failed to read SVG file: " + svgPath,
                    exception
            );
        } catch (SAXException exception) {
            throw new SvgToPngConversionException(
                    "Failed to parse SVG file: " + svgPath,
                    exception
            );
        } catch (TranscoderException exception) {
            throw new SvgToPngConversionException(
                    "Failed to transcode SVG to PNG: " + svgPath + " -> " + pngPath,
                    exception
            );
        }
    }

    /**
     * Internal method to perform SVG to PNG conversion.
     *
     * @param svgPath path to the source SVG file
     * @param pngPath path where the PNG file will be written
     * @param width   width of the output PNG in pixels
     * @param height  height of the output PNG in pixels
     * @throws IOException         if I/O operations fail
     * @throws SAXException        if SVG parsing fails
     * @throws TranscoderException if transcoding fails
     */
    private static void convertSvgToPng(final Path svgPath, final Path pngPath, final int width, final int height)
            throws IOException, SAXException, TranscoderException {
        try (FileInputStream svgInputStream = new FileInputStream(svgPath.toFile());
             FileOutputStream pngOutputStream = new FileOutputStream(pngPath.toFile())) {

            final Transcoder pngTranscoder = new PNGTranscoder();
            pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
            pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);

            final TranscoderInput transcoderInput = new TranscoderInput(svgInputStream);
            final TranscoderOutput transcoderOutput = new TranscoderOutput(pngOutputStream);

            pngTranscoder.transcode(transcoderInput, transcoderOutput);
        }
    }

}
