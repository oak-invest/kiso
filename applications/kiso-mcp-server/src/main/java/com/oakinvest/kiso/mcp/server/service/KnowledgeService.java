package com.oakinvest.kiso.mcp.server.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.oakinvest.kiso.core.util.contants.FileExtensionsConstants.MARKDOWN_EXTENSION;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.CONCEPT_ID;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.DESCRIPTION;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.FIELDS;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.FIELDS_BOOSTS;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.TITLE;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.Thread.MIN_PRIORITY;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Knowledge service.
 */
public class KnowledgeService implements AutoCloseable {

    /** Reports index lifecycle events and failures. */
    private static final System.Logger LOGGER = System.getLogger(KnowledgeService.class.getName());

    /** Refresh interval in minutes. */
    private static final int REFRESH_INTERVAL = 10;

    /** Default number of results. */
    private static final int DEFAULT_NUMBER_OF_RESULTS = 100;

    /** Root bundle path. */
    private final Path rootBundlePath;

    /** Protects the active index and its lifecycle. */
    private final ReentrantReadWriteLock indexLock = new ReentrantReadWriteLock();

    /** Refresh schedule. */
    private final ScheduledExecutorService refreshScheduler;

    /** Knowledge index. */
    private KnowledgeIndex index;

    /** Guarded by this service's monitor, together with refresh and shutdown. */
    private boolean closed;

    /**
     * Constructor.
     *
     * @param newRootBundlePath root bundle path
     */
    public KnowledgeService(final Path newRootBundlePath) {
        // Create index ================================================================================================
        rootBundlePath = newRootBundlePath;
        LOGGER.log(INFO, "Building initial knowledge index from {0}", rootBundlePath);
        try {
            index = KnowledgeIndexBuilder.build(rootBundlePath);
        } catch (RuntimeException exception) {
            LOGGER.log(ERROR, "Could not build the initial knowledge index from {0}", rootBundlePath, exception);
            throw exception;
        }
        LOGGER.log(INFO, "Initial knowledge index ready: {0} concepts indexed ", index.reader().numDocs());

        // Create a scheduled executor service to refresh the index periodically.
        refreshScheduler = Executors.newSingleThreadScheduledExecutor(
                Thread.ofPlatform().daemon().name("knowledge-index-refresher").priority(MIN_PRIORITY).factory()
        );
        refreshScheduler.scheduleAtFixedRate(this::refreshIndex, REFRESH_INTERVAL, REFRESH_INTERVAL, MINUTES);
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

        indexLock.readLock().lock();
        try (Analyzer analyzer = new StandardAnalyzer()) {

            // Create a query parser ===================================================================================
            final MultiFieldQueryParser parser = new MultiFieldQueryParser(FIELDS, analyzer, FIELDS_BOOSTS);

            // Run the search ==========================================================================================
            final TopDocs topDocuments = index.searcher().search(parser.parse(text.trim()), DEFAULT_NUMBER_OF_RESULTS);

            // Treat the results =======================================================================================
            final List<KnowledgeSearchResult> results = new ArrayList<>();
            for (ScoreDoc scoreDocument : topDocuments.scoreDocs) {
                // We retrieve the document.
                final Document document = index.searcher().storedFields().document(scoreDocument.doc);
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
        } finally {
            indexLock.readLock().unlock();
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
            final Path path = rootBundlePath.resolve(conceptId.trim() + MARKDOWN_EXTENSION);
            if (!Files.isRegularFile(path)) {
                return Optional.empty();
            }

            return Optional.of(Files.readString(path, UTF_8));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Refreshes the index.
     */
    private synchronized void refreshIndex() {
        if (closed) {
            return;
        }
        LOGGER.log(INFO, "Refreshing knowledge index from {0}", rootBundlePath);
        try {
            // We retrieve the current index and build the new one =====================================================
            final KnowledgeIndex newIndex = KnowledgeIndexBuilder.build(rootBundlePath);

            // We lock and replace the index with the new one ==========================================================
            indexLock.writeLock().lock();
            try {
                final KnowledgeIndex previousIndex = index;
                index = newIndex;
                previousIndex.close();
            } finally {
                indexLock.writeLock().unlock();
            }
            LOGGER.log(INFO, "Initial knowledge index ready: {0} concepts indexed ", index.reader().numDocs());
        } catch (IOException | RuntimeException exception) {
            LOGGER.log(ERROR, "Could not refresh the knowledge index from {0}", rootBundlePath, exception);
        }
    }

    /**
     * Waits for any refresh and active searches before closing the index.
     */
    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        refreshScheduler.shutdownNow();
        indexLock.writeLock().lock();
        try {
            index.close();
            LOGGER.log(INFO, "Knowledge service closed ");
        } catch (IOException exception) {
            LOGGER.log(ERROR, "Could not close the knowledge index for " + rootBundlePath, exception);
            throw new UncheckedIOException(exception);
        } finally {
            indexLock.writeLock().unlock();
        }
    }

}
