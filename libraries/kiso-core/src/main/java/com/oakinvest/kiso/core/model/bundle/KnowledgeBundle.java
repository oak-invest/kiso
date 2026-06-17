package com.oakinvest.kiso.core.model.bundle;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;

import java.nio.file.Path;
import java.util.List;

/**
 * A knowledge bundle: A self-contained, hierarchical collection of knowledge documents.
 *
 * @param rootDirectory bundle root directory
 * @param directories   directories discovered inside the bundle
 * @param markdownFiles files discovered inside the bundle (only Markdown)
 */
public record KnowledgeBundle(
        Path rootDirectory,
        List<BundleDirectory> directories,
        List<MarkdownFile> markdownFiles
) {
}
