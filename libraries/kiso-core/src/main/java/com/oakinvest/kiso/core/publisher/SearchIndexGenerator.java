package com.oakinvest.kiso.core.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;

import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.CONCEPT;

/**
 * Search index generator.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class SearchIndexGenerator {

    /** JSON object mapper. */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Generates a search index from a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return search index
     */
    public static String generate(final KnowledgeBundle knowledgeBundle) {
        ObjectNode searchIndex = OBJECT_MAPPER.createObjectNode();
        ArrayNode documents = searchIndex.putArray("documents");

        knowledgeBundle.markdownFiles()
                .filter(markdownFile -> markdownFile.kind().equals(CONCEPT))
                .forEach(markdownFile -> documents.add(searchDocument(markdownFile)));

        try {
            return OBJECT_MAPPER.writeValueAsString(searchIndex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate search index JSON", e);
        }
    }

    /**
     * Converts a Markdown file to a search document JSON object.
     *
     * @param markdownFile Markdown file
     * @return search document JSON object
     */
    private static ObjectNode searchDocument(final MarkdownFile markdownFile) {
        ObjectNode document = OBJECT_MAPPER.createObjectNode();
        document.put("id", markdownFile.conceptId());
        document.put("url", markdownFile.htmlFilePath());
        document.put("title", markdownFile.frontmatter().title());
        document.put("description", markdownFile.frontmatter().description());
        document.put("body", markdownFile.body());

        ArrayNode tags = document.putArray("tags");
        markdownFile.frontmatter().tags().forEach(tags::add);

        return document;
    }

}
