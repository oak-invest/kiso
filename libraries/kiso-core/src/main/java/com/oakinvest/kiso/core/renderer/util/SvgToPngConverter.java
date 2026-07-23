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
import java.util.List;

/**
 * Converts SVG files to PNG format.
 * Uses Apache Batik on the JVM, and falls back to an external CLI tool
 * (rsvg-convert, inkscape, or resvg) when running as a GraalVM native image.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class SvgToPngConverter {

    /** GraalVM property exposing whether the code is running inside a native image. */
    private static final String NATIVE_IMAGE_CODE_PROPERTY = "org.graalvm.nativeimage.imagecode";

    /** GraalVM property value used at native image runtime. */
    private static final String NATIVE_IMAGE_RUNTIME_CODE = "runtime";

    /** External CLI tools tried in order when Batik is not available. */
    private static final List<String> EXTERNAL_TOOLS = List.of("rsvg-convert", "inkscape", "resvg");

    /** Cached result of the external tool search. Null means not yet searched. */
    private static volatile String cachedExternalTool = null;

    /** Whether the external tool search has already been performed. */
    private static volatile boolean externalToolSearchDone = false;

    /**
     * Returns true when SVG to PNG conversion is available.
     * In JVM mode, Batik is always available.
     * In GraalVM native image mode, returns true if at least one external CLI tool is installed.
     *
     * @return {@code true} when SVG to PNG conversion is available
     */
    public static boolean isAvailable() {
        if (!isNativeImageRuntime()) {
            return true;
        }
        return findExternalTool() != null;
    }

    /**
     * Converts an SVG file to PNG format.
     * Uses Batik on the JVM, or an external CLI tool in native image mode.
     *
     * @param svgPath path to the source SVG file
     * @param pngPath path where the PNG file will be written
     * @param width   width of the output PNG in pixels
     * @param height  height of the output PNG in pixels
     * @throws SvgToPngConversionException if the conversion fails
     */
    public static void convert(final Path svgPath, final Path pngPath, final int width, final int height) {
        if (!isNativeImageRuntime()) {
            convertWithBatik(svgPath, pngPath, width, height);
        } else {
            convertWithExternalTool(svgPath, pngPath, width, height);
        }
    }

    /**
     * Returns true when running inside a GraalVM native image.
     *
     * @return {@code true} when running as a native image
     */
    static boolean isNativeImageRuntime() {
        return NATIVE_IMAGE_RUNTIME_CODE.equals(System.getProperty(NATIVE_IMAGE_CODE_PROPERTY));
    }

    /**
     * Finds the first available external CLI tool for SVG to PNG conversion.
     * The result is cached after the first search.
     *
     * @return the name of the available tool, or null if none is installed
     */
    static synchronized String findExternalTool() {
        if (!externalToolSearchDone) {
            externalToolSearchDone = true;
            for (String tool : EXTERNAL_TOOLS) {
                if (isToolInstalled(tool)) {
                    cachedExternalTool = tool;
                    break;
                }
            }
        }
        return cachedExternalTool;
    }

    /**
     * Returns true if the given CLI tool is installed and can be launched.
     *
     * @param toolName the tool to check
     * @return {@code true} if the tool is available
     */
    private static boolean isToolInstalled(final String toolName) {
        try {
            Process process = new ProcessBuilder(toolName, "--version")
                    .redirectErrorStream(true)
                    .start();
            process.waitFor();
            return true;
        } catch (IOException exception) {
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Converts SVG to PNG using an external CLI tool.
     *
     * @param svgPath path to the source SVG file
     * @param pngPath path where the PNG file will be written
     * @param width   width of the output PNG in pixels
     * @param height  height of the output PNG in pixels
     */
    private static void convertWithExternalTool(final Path svgPath, final Path pngPath, final int width, final int height) {
        String tool = findExternalTool();
        if (tool == null) {
            throw new SvgToPngConversionException(
                    "No SVG to PNG conversion tool available. Please install rsvg-convert, inkscape, or resvg.",
                    null
            );
        }
        try {
            List<String> command = buildExternalToolCommand(tool, svgPath, pngPath, width, height);
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new SvgToPngConversionException(
                        "External tool '" + tool + "' failed with exit code " + exitCode + " for: " + svgPath,
                        null
                );
            }
        } catch (IOException exception) {
            throw new SvgToPngConversionException(
                    "Failed to run external tool '" + tool + "' for: " + svgPath,
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new SvgToPngConversionException(
                    "Interrupted while running external tool '" + tool + "' for: " + svgPath,
                    exception
            );
        }
    }

    /**
     * Builds the command list for the given external tool.
     *
     * @param tool    the tool name
     * @param svgPath path to the source SVG file
     * @param pngPath path where the PNG file will be written
     * @param width   width of the output PNG in pixels
     * @param height  height of the output PNG in pixels
     * @return the command as a list of arguments
     */
    private static List<String> buildExternalToolCommand(final String tool, final Path svgPath, final Path pngPath, final int width, final int height) {
        return switch (tool) {
            case "rsvg-convert" -> List.of(
                    "rsvg-convert",
                    "-w", String.valueOf(width),
                    "-h", String.valueOf(height),
                    "-o", pngPath.toString(),
                    svgPath.toString()
            );
            case "inkscape" -> List.of(
                    "inkscape",
                    "--export-type=png",
                    "--export-filename=" + pngPath,
                    "-w", String.valueOf(width),
                    "-h", String.valueOf(height),
                    svgPath.toString()
            );
            case "resvg" -> List.of(
                    "resvg",
                    "-w", String.valueOf(width),
                    "-h", String.valueOf(height),
                    svgPath.toString(),
                    pngPath.toString()
            );
            default -> throw new SvgToPngConversionException("Unknown external tool: " + tool, null);
        };
    }

    /**
     * Converts SVG to PNG using Apache Batik.
     *
     * @param svgPath path to the source SVG file
     * @param pngPath path where the PNG file will be written
     * @param width   width of the output PNG in pixels
     * @param height  height of the output PNG in pixels
     */
    private static void convertWithBatik(final Path svgPath, final Path pngPath, final int width, final int height) {
        try {
            convertSvgToPngWithBatik(svgPath, pngPath, width, height);
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
     * Internal Batik transcoding method.
     *
     * @param svgPath path to the source SVG file
     * @param pngPath path where the PNG file will be written
     * @param width   width of the output PNG in pixels
     * @param height  height of the output PNG in pixels
     * @throws IOException         if I/O operations fail
     * @throws SAXException        if SVG parsing fails
     * @throws TranscoderException if transcoding fails
     */
    private static void convertSvgToPngWithBatik(final Path svgPath, final Path pngPath, final int width, final int height)
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
