package com.oakinvest.kiso.mcp.server;

import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationTest {

    @Test
    void configuresExpectedLuceneWarningsAsErrorsOnly() {
        Application.configureLibraryLogging();

        assertEquals(Level.SEVERE, Logger.getLogger("org.apache.lucene.util.HotspotVMOptions").getLevel());
        assertEquals(Level.SEVERE,
                Logger.getLogger("org.apache.lucene.internal.vectorization.VectorizationProvider").getLevel());
    }
}
