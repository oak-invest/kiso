package com.oakinvest.kiso.mcp.server.service;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.IOUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.BODY;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.CONCEPT_ID;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.DESCRIPTION;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.TAGS;
import static com.oakinvest.kiso.mcp.server.service.KnowledgeIndexFields.TITLE;

/**
 * Builds the Lucene index from the OKF bundle.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public final class KnowledgeIndexBuilder {

    /**
     * Builds a new concept index from the given root bundle path.
     *
     * @param rootBundlePath root bundle path
     * @return newly built index directory
     */
    public static KnowledgeIndex build(final Path rootBundlePath) {
        // Load the knowledge bundle from the root bundle path =========================================================
        final KnowledgeBundle knowledgeBundle = KnowledgeBundleLoader.load(rootBundlePath);

        // Create the index directory ==================================================================================
        final Directory directory = new ByteBuffersDirectory();

        // Creates the index for the knowledge bundle ==================================================================
        try (Analyzer analyzer = new StandardAnalyzer()) {
            // Create index writer.
            final IndexWriterConfig configuration = new IndexWriterConfig(analyzer);
            configuration.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // Add all concept documents to the index.
            try (IndexWriter writer = new IndexWriter(directory, configuration)) {
                knowledgeBundle.markdownFiles()
                        .filter(markdownFile -> CONCEPT.equals(markdownFile.kind()))
                        .forEach(markdownFile -> addDocument(writer, markdownFile));
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        // Creates the directory reader ================================================================================
        DirectoryReader reader = null;
        try {
            reader = DirectoryReader.open(directory);
        } catch (IOException exception) {
            IOUtils.closeWhileHandlingException(reader, directory);
            throw new UncheckedIOException(exception);
        } catch (RuntimeException | Error exception) {
            IOUtils.closeWhileHandlingException(reader, directory);
            throw exception;
        }

        // Returns the knowledge index =================================================================================
        return KnowledgeIndex.builder()
                .directory(directory)
                .reader(reader)
                .searcher(new IndexSearcher(reader))
                .build();
    }

    /**
     * Adds a document file to the index.
     *
     * @param writer       index writer
     * @param markdownFile Markdown file
     */
    private static void addDocument(final IndexWriter writer, final MarkdownFile markdownFile) {
        final Document document = new Document();

        // Concept ID ==================================================================================================
        final String conceptId = markdownFile.conceptId();
        if (StringUtils.isNotBlank(conceptId)) {
            document.add(new StringField(CONCEPT_ID, conceptId, Field.Store.YES));
        } else {
            return; // We skip the document if the concept ID is null, as it is required for indexing.
        }

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
            document.add(new TextField(TAGS, tags, Field.Store.NO));
        }

        // Body ========================================================================================================
        final String body = markdownFile.body();
        if (StringUtils.isNotBlank(body)) {
            document.add(new TextField(BODY, body, Field.Store.NO));
        }

        // Writing the document to the index ===========================================================================
        try {
            writer.addDocument(document);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

}
