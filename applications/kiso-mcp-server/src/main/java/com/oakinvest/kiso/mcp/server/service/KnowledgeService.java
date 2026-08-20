package com.oakinvest.kiso.mcp.server.service;

import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.BODY;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.CONCEPT_ID;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.DESCRIPTION;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.TITLE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Knowledge Service.
 */
public class KnowledgeService {

    /** Default number of results. */
    private static final int DEFAULT_NUMBER_OF_RESULTS = 10;

    /** Index directory. */
    private final Path indexDirectory = Path.of(".kiso/index");

    /** Concept paths. */
    private final Map<String, Path> conceptPaths;

    /**
     * Constructor.
     *
     * @param knowledgeBundle Knowledge bundle.
     */
    public KnowledgeService(final KnowledgeBundle knowledgeBundle) {
        // Build lucence index =========================================================================================
        try (
                Analyzer analyzer = new StandardAnalyzer();
                Directory directory = FSDirectory.open(indexDirectory)
        ) {
            // Create index writer.
            final IndexWriterConfig configuration = new IndexWriterConfig(analyzer);
            configuration.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // Add all documents to the index
            try (IndexWriter writer = new IndexWriter(directory, configuration)) {
                knowledgeBundle.markdownFiles()
                        .filter(markdownFile -> CONCEPT.equals(markdownFile.kind()))
                        .forEach(markdownFile -> addDocument(writer, markdownFile));
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        // Build concept paths =========================================================================================
        conceptPaths = knowledgeBundle.markdownFiles()
                .filter(markdownFile -> CONCEPT.equals(markdownFile.kind()))
                .collect(Collectors.toUnmodifiableMap(
                        MarkdownFile::conceptId,
                        MarkdownFile::absolutePath
                ));
    }

    /**
     * Searches the knowledge index.
     *
     * @param text search text
     * @return search results
     */
    public List<KnowledgeSearchResult> search(final String text) {
        try (
                Analyzer analyzer = new StandardAnalyzer();
                Directory directory = FSDirectory.open(indexDirectory);
                DirectoryReader reader = DirectoryReader.open(directory)
        ) {

            // Index searcher and parser ===============================================================================
            final IndexSearcher searcher = new IndexSearcher(reader);
            final MultiFieldQueryParser parser = new MultiFieldQueryParser(
                    KnowledgeIndexFields.FIELDS,
                    analyzer,
                    KnowledgeIndexFields.FIELDS_BOOSTS
            );

            // We query ================================================================================================
            final Query query = parser.parse(text);
            final TopDocs topDocuments = searcher.search(query, DEFAULT_NUMBER_OF_RESULTS);

            // We treat the results ====================================================================================
            final List<KnowledgeSearchResult> results = new ArrayList<>();
            for (ScoreDoc scoreDocument : topDocuments.scoreDocs) {
                // We retrieve the document.
                final Document document = searcher.storedFields().document(scoreDocument.doc);
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
            throw new IllegalArgumentException("Invalid search query: " + text, exception);
        }
    }

    /**
     * Returns the Markdown content of a concept.
     *
     * @param conceptId concept identifier
     * @return Markdown content if the concept exists
     */
    public Optional<String> getConceptContent(@Nullable final String conceptId) {
        if (conceptId == null) {
            return Optional.empty();
        }

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

    /**
     * Adds a document file to the index.
     *
     * @param writer       index writer
     * @param markdownFile Markdown file
     */
    private void addDocument(final IndexWriter writer, final MarkdownFile markdownFile) {
        final Document document = new Document();

        // Concept ID ==================================================================================================
        document.add(new StringField(CONCEPT_ID, markdownFile.conceptId(), Field.Store.YES));

        // Title =======================================================================================================
        final String title = markdownFile.frontmatter().title();
        if (StringUtils.isNotBlank(title)) {
            document.add(new TextField(TITLE, title, Field.Store.YES));
        }

        // Description =================================================================================================
        final String description = markdownFile.frontmatter().description();
        if (StringUtils.isNotBlank(description)) {
            document.add(new TextField(DESCRIPTION, description, Field.Store.YES));
        }

        // Tags ========================================================================================================
        final String tags = String.join(" ", markdownFile.frontmatter().tags());
        if (StringUtils.isNotBlank(tags)) {
            document.add(new TextField(DESCRIPTION, tags, Field.Store.YES));
        }

        // Body ========================================================================================================
        final String body = markdownFile.body();
        if (StringUtils.isNotBlank(body)) {
            document.add(new TextField(BODY, body, Field.Store.NO));
        }

        try {
            writer.addDocument(document);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

}
