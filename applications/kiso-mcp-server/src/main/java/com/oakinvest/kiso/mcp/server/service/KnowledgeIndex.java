package com.oakinvest.kiso.mcp.server.service;

import lombok.Builder;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.IOUtils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Knowledge index.
 *
 * @param directory the directory of the index
 * @param reader    the directory reader
 * @param searcher  the index searcher
 */
@Builder
@SuppressWarnings("unused")
public record KnowledgeIndex(
        Directory directory,
        DirectoryReader reader,
        IndexSearcher searcher
) implements Closeable {

    @Override
    public void close() throws IOException {
        IOUtils.close(reader, directory);
    }

}
