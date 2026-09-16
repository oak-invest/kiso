package com.oakinvest.kiso.mcp.server.service;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.IOUtils;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.oakinvest.kiso.core.util.contants.FileExtensionsConstants.MARKDOWN_EXTENSION;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.CONCEPT_ID;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.DESCRIPTION;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.FIELDS;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.FIELDS_BOOSTS;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.TITLE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Knowledge service.
 */
public class KnowledgeService implements AutoCloseable {

    /** Default number of results. */
    private static final int DEFAULT_NUMBER_OF_RESULTS = 100;

    /** Root bundle path. */
    private final Path rootBundlePath;

    /** Knowledge index. */
    private final Directory index;

    /** Reader shared by all searches. */
    private final DirectoryReader reader;

    /** Searcher shared by all searches. */
    private final IndexSearcher searcher;

    /**
     * Constructor.
     *
     * @param knowledgeBundle Knowledge bundle.
     */
    public KnowledgeService(final KnowledgeBundle knowledgeBundle) {
        rootBundlePath = knowledgeBundle.rootBundle().absolutePath();
        index = KnowledgeIndexBuilder.build(knowledgeBundle);
        try {
            reader = DirectoryReader.open(index);
        } catch (IOException exception) {
            IOUtils.closeWhileHandlingException(index);
            throw new UncheckedIOException(exception);
        } catch (RuntimeException | Error exception) {
            IOUtils.closeWhileHandlingException(index);
            throw exception;
        }
        searcher = new IndexSearcher(reader);
    }

    /**
     * Searches concepts in the knowledge bundle.
     *
     * @param text search text
     * @return list of search results
     */
    public List<KnowledgeSearchResult> searchConcepts(@Nullable final String text) {
        if (StringUtils.isBlank(text)) {
            return List.of();
        }

        try (Analyzer analyzer = new StandardAnalyzer()) {

            // Create a query parser ===================================================================================
            final MultiFieldQueryParser parser = new MultiFieldQueryParser(FIELDS, analyzer, FIELDS_BOOSTS);

            // Run the search ==========================================================================================
            final TopDocs topDocuments = searcher.search(parser.parse(text), DEFAULT_NUMBER_OF_RESULTS);

            // Treat the results =======================================================================================
            final List<KnowledgeSearchResult> results = new ArrayList<>();
            for (ScoreDoc scoreDocument : topDocuments.scoreDocs) {
                // We retrieve the document.
                final Document document = searcher.storedFields().document(scoreDocument.doc);
                // And we build the result object.
                results.add(KnowledgeSearchResult.builder()
                        .conceptId(document.get(CONCEPT_ID))
                        .title(document.get(TITLE))
                        .description(document.get(DESCRIPTION))
                        .score(scoreDocument.score)
                        .build());
            }
            return results;

        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        } catch (ParseException exception) {
            throw new IllegalArgumentException("Invalid searchConcepts query: " + text, exception);
        }
    }

    /**
     * Returns the Markdown content of a concept.
     *
     * @param conceptId concept identifier
     * @return Markdown content
     */
    public Optional<String> getConceptContent(@Nullable final String conceptId) {
        if (StringUtils.isBlank(conceptId)) {
            return Optional.empty();
        }

        // Get the content of the concept from the file system.
        try {
            final Path path = rootBundlePath.resolve(conceptId + MARKDOWN_EXTENSION);
            if (!Files.isRegularFile(path)) {
                return Optional.empty();
            }

            return Optional.of(Files.readString(path, UTF_8));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Closes the reader and index after all searches have finished.
     */
    @Override
    public void close() {
        try {
            IOUtils.close(reader, index);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

}
