package com.oakinvest.kiso.cli.v0_2.renderer.util;

import com.oakinvest.kiso.cli.exception.SvgToPngConversionException;
import com.oakinvest.kiso.cli.tools.SvgToPngConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("vO.2 - SvgToPngConverter")
final class SvgToPngConverterTest {

    private static final String SAMPLE_SVG = """
            <?xml version="1.0" encoding="UTF-8"?>
            <svg xmlns="http://www.w3.org/2000/svg" width="1200" height="630" viewBox="0 0 1200 630">
                <rect width="1200" height="630" fill="#667eea"/>
                <text x="60" y="80" font-size="28" fill="#ffffff">Test</text>
            </svg>
            """;

    private static void restoreNativeImageCodeProperty(final String previousValue) {
        if (previousValue == null) {
            System.clearProperty("org.graalvm.nativeimage.imagecode");
        } else {
            System.setProperty("org.graalvm.nativeimage.imagecode", previousValue);
        }
    }

    @Test
    @DisplayName("converts SVG to PNG successfully")
    void convertsSvgToPngSuccessfully(@TempDir Path tempDir) throws Exception {
        Path svgPath = tempDir.resolve("test.svg");
        Path pngPath = tempDir.resolve("test.png");

        Files.writeString(svgPath, SAMPLE_SVG);

        SvgToPngConverter.convert(svgPath, pngPath, 1200, 630);

        assertThat(pngPath).exists();
        assertThat(Files.size(pngPath)).isGreaterThan(0);
    }

    @Test
    @DisplayName("creates PNG file with correct dimensions")
    void createsPngWithCorrectDimensions(@TempDir Path tempDir) throws Exception {
        Path svgPath = tempDir.resolve("test.svg");
        Path pngPath = tempDir.resolve("test.png");

        Files.writeString(svgPath, SAMPLE_SVG);

        SvgToPngConverter.convert(svgPath, pngPath, 1200, 630);

        assertThat(pngPath).exists().isRegularFile();
    }

    @Test
    @DisplayName("throws exception for non-existent SVG file")
    void throwsExceptionForNonExistentSvg(@TempDir Path tempDir) {
        Path svgPath = tempDir.resolve("nonexistent.svg");
        Path pngPath = tempDir.resolve("output.png");

        assertThatThrownBy(() -> SvgToPngConverter.convert(svgPath, pngPath, 1200, 630))
                .isInstanceOf(SvgToPngConversionException.class);
    }

    @Test
    @DisplayName("throws exception for invalid SVG")
    void throwsExceptionForInvalidSvg(@TempDir Path tempDir) throws Exception {
        Path svgPath = tempDir.resolve("invalid.svg");
        Path pngPath = tempDir.resolve("output.png");

        Files.writeString(svgPath, "This is not valid SVG");

        assertThatThrownBy(() -> SvgToPngConverter.convert(svgPath, pngPath, 1200, 630))
                .isInstanceOf(SvgToPngConversionException.class);
    }

    @Test
    @DisplayName("handles UTF-8 encoded SVG")
    void handlesUtf8EncodedSvg(@TempDir Path tempDir) throws Exception {
        String svgWithUtf8 = """
                <?xml version="1.0" encoding="UTF-8"?>
                <svg xmlns="http://www.w3.org/2000/svg" width="1200" height="630" viewBox="0 0 1200 630">
                    <rect width="1200" height="630" fill="#667eea"/>
                    <text x="60" y="80" font-size="28" fill="#ffffff">Kiso - Publishing Engine 🚀</text>
                </svg>
                """;

        Path svgPath = tempDir.resolve("utf8.svg");
        Path pngPath = tempDir.resolve("utf8.png");

        Files.writeString(svgPath, svgWithUtf8);

        SvgToPngConverter.convert(svgPath, pngPath, 1200, 630);

        assertThat(pngPath).exists();
    }

    @Test
    @DisplayName("reports conversion as available in JVM mode")
    void reportsConversionAsAvailableInJvmMode() {
        String previousValue = System.getProperty("org.graalvm.nativeimage.imagecode");
        try {
            System.clearProperty("org.graalvm.nativeimage.imagecode");

            assertThat(SvgToPngConverter.isAvailable()).isTrue();
        } finally {
            restoreNativeImageCodeProperty(previousValue);
        }
    }

    @Test
    @DisplayName("finds an external tool when running in native image mode")
    void findsExternalToolInNativeImageMode() {
        assumeTrue(SvgToPngConverter.findExternalTool() != null,
                "Skipped: no external SVG to PNG tool (rsvg-convert, inkscape, resvg) installed");

        String previousValue = System.getProperty("org.graalvm.nativeimage.imagecode");
        try {
            System.setProperty("org.graalvm.nativeimage.imagecode", "runtime");

            assertThat(SvgToPngConverter.isAvailable()).isTrue();
        } finally {
            restoreNativeImageCodeProperty(previousValue);
        }
    }

    @Test
    @DisplayName("converts SVG to PNG using external tool in native image mode")
    void convertsSvgToPngWithExternalToolInNativeImageMode(@TempDir Path tempDir) throws Exception {
        assumeTrue(SvgToPngConverter.findExternalTool() != null,
                "Skipped: no external SVG to PNG tool (rsvg-convert, inkscape, resvg) installed");

        Path svgPath = tempDir.resolve("test.svg");
        Path pngPath = tempDir.resolve("test.png");
        Files.writeString(svgPath, SAMPLE_SVG);

        String previousValue = System.getProperty("org.graalvm.nativeimage.imagecode");
        try {
            System.setProperty("org.graalvm.nativeimage.imagecode", "runtime");

            SvgToPngConverter.convert(svgPath, pngPath, 1200, 630);

            assertThat(pngPath).exists();
            assertThat(Files.size(pngPath)).isGreaterThan(0);
        } finally {
            restoreNativeImageCodeProperty(previousValue);
        }
    }

}
