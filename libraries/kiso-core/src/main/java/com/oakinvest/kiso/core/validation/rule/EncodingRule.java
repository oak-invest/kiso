package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.validation.ValidationIssue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_ENCODING;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;

/**
 * Encoding validator.
 * <a href="https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md">OKF Spec</a>
 * 4. Concept Documents
 * Every concept is a UTF-8 markdown file.
 */
public class EncodingRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final Bundle bundle, final MarkdownFile markdownFile) {
        Objects.requireNonNull(bundle, "bundle must not be null");
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        // If it's a concept file.
        if (markdownFile.kind().equals(CONCEPT)) {
            try {
                if (!isValidUtf8(markdownFile.absolutePath())) {
                    return List.of(ValidationIssue.builder()
                            .severity(ERROR)
                            .code(INVALID_ENCODING)
                            .message("File " + markdownFile.relativePath() + " is not valid UTF-8 encoded")
                            .path(markdownFile.relativePath())
                            .build());
                }
            } catch (IOException e) {
                // If we had an error while checking the file.
                return List.of(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(INVALID_ENCODING)
                        .message("Error while checking encoding of " + e.getMessage())
                        .path(markdownFile.relativePath())
                        .build());
            }
        }
        return List.of();
    }

    private boolean isValidUtf8(final Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);

        CharsetDecoder decoder = StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        try {
            decoder.decode(ByteBuffer.wrap(bytes));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }

}
