package com.oakinvest.kiso.core.renderer.util;

import com.oakinvest.kiso.core.exception.SvgToPngConversionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SvgToPngConverter")
final class SvgToPngConverterTest {

    private static final String SAMPLE_SVG = """
            <?xml version="1.0" encoding="UTF-8"?>
            <svg xmlns="http://www.w3.org/2000/svg" width="1200" height="630" viewBox="0 0 1200 630">
                <rect width="1200" height="630" fill="#667eea"/>
                <text x="60" y="80" font-size="28" fill="#ffffff">Test</text>
            </svg>
            """;

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

}
