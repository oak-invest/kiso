package com.oakinvest.kiso.core.model.concept;

import com.oakinvest.kiso.core.model.markdown.MarkdownLink;

import java.nio.file.Path;
import java.util.List;

/**
 * A concept: A single unit of knowledge within a bundle. Represented as one Markdown document.
 *
 * @param conceptId the path of the concept's file within the bundle, with the .md suffix removed. For example, tables/users.md has concept ID tables/users
 * @param path absolute or execution-relative file path
 * @param relativePath path relative to the bundle root directory
 * @param frontmatter parsed frontmatter metadata
 * @param body body without frontmatter
 * @param links links found in the document body
 */
public record Concept(
        String conceptId,
        Path path,
        Path relativePath,
        Frontmatter frontmatter,
        String body,
        List<MarkdownLink> links
) {
}
