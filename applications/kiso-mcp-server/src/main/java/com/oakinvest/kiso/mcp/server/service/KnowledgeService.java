package com.oakinvest.kiso.mcp.server.service;

import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Knowledge Service.
 */
public class KnowledgeService {

    /** Concept paths. */
    private final Map<String, Path> conceptPaths;

    /**
     * Constructor.
     *
     * @param knowledgeBundle Knowledge bundle.
     */
    public KnowledgeService(final KnowledgeBundle knowledgeBundle) {
        conceptPaths = knowledgeBundle.markdownFiles()
                .filter(markdownFile -> CONCEPT.equals(markdownFile.kind()))
                .collect(Collectors.toUnmodifiableMap(
                        MarkdownFile::conceptId,
                        MarkdownFile::absolutePath
                ));
    }

    /**
     * Returns the Markdown content of a concept.
     *
     * @param conceptId concept identifier
     * @return Markdown content if the concept exists
     */
    public Optional<String> getConceptContent(final String conceptId) {
        // Get the path of the concept from the map.
        final Path path = conceptPaths.get(conceptId);
        if (path == null) {
            return Optional.empty();
        }

        // Get the content of the concept from the file system.
        try {
            return Optional.of(Files.readString(path, UTF_8));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

}
