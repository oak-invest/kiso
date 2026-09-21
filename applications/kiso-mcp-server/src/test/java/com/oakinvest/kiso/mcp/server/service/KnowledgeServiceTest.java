package com.oakinvest.kiso.mcp.server.service;

import com.oakinvest.kiso.mcp.server.util.BaseTest;
import org.apache.lucene.store.AlreadyClosedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Knowledge service tests")
public class KnowledgeServiceTest extends BaseTest {

    private KnowledgeService knowledgeService;

    private static void refresh(final KnowledgeService service) throws Exception {
        var method = KnowledgeService.class.getDeclaredMethod("refreshIndex");
        method.setAccessible(true);
        method.invoke(service);
    }

    private static KnowledgeIndex activeIndex(final KnowledgeService service) throws Exception {
        var field = KnowledgeService.class.getDeclaredField("index");
        field.setAccessible(true);
        return (KnowledgeIndex) field.get(service);
    }

    private static ReentrantReadWriteLock indexLock(final KnowledgeService service) throws Exception {
        var field = KnowledgeService.class.getDeclaredField("indexLock");
        field.setAccessible(true);
        return (ReentrantReadWriteLock) field.get(service);
    }

    private static void await(final BooleanSupplier condition) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        assertThat(condition.getAsBoolean()).isTrue();
    }

    @BeforeEach
    public void setUp() {
        knowledgeService = new KnowledgeService(getResourcePath(KB_ACME_V_0_2));
    }

    @AfterEach
    public void tearDown() {
        knowledgeService.close();
    }

    @Test
    @Timeout(5)
    @DisplayName("The initial index is available without waiting for a scheduled refresh")
    public void initialIndexIsImmediatelyAvailable() {
        try (var service = new KnowledgeService(getResourcePath(KB_ACME_V_0_2))) {
            assertThat(service.searchConcepts("discount_amount")).hasSize(2);
        }
    }

    @Test
    @DisplayName("Concurrent searches share the reader safely")
    public void concurrentSearches() throws Exception {
        var expectedResults = knowledgeService.searchConcepts("discount_amount");
        try (var executor = Executors.newFixedThreadPool(4)) {
            List<Future<List<KnowledgeSearchResult>>> searches = new ArrayList<>();
            for (int search = 0; search < 20; search++) {
                searches.add(executor.submit(() -> knowledgeService.searchConcepts("discount_amount")));
            }
            for (var search : searches) {
                assertThat(search.get()).isEqualTo(expectedResults);
            }
        }
    }

    @Test
    @DisplayName("Closing the service closes its reader")
    public void close() {
        knowledgeService.close();
        assertThatThrownBy(() -> knowledgeService.searchConcepts("discount_amount"))
                .isInstanceOf(AlreadyClosedException.class);
    }

    @Test
    @DisplayName("A failed refresh preserves the index and allows the next refresh")
    public void failedRefresh(@TempDir final Path temporaryDirectory) throws Exception {
        Path source = getResourcePath(KB_ACME_V_0_2);
        Path bundle = temporaryDirectory.resolve("bundle");
        try (var paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path target = bundle.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(target);
                } else {
                    Files.copy(path, target);
                }
            }
        }
        try (var service = new KnowledgeService(bundle)) {
            KnowledgeIndex previousIndex = activeIndex(service);
            var expected = service.searchConcepts("discount_amount");
            Path unavailableBundle = temporaryDirectory.resolve("unavailable");
            Files.move(bundle, unavailableBundle);

            refresh(service);
            assertThat(activeIndex(service)).isSameAs(previousIndex);
            assertThat(service.searchConcepts("discount_amount")).isEqualTo(expected);

            Files.move(unavailableBundle, bundle);
            refresh(service);
            assertThat(activeIndex(service)).isNotSameAs(previousIndex);
            assertThat(previousIndex.reader().getRefCount()).isZero();
            assertThatThrownBy(() -> previousIndex.directory().listAll())
                    .isInstanceOf(AlreadyClosedException.class);
            assertThat(service.searchConcepts("discount_amount")).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("Closing waits for active searches before releasing the index")
    public void closeWaitsForSearches() throws Exception {
        ReentrantReadWriteLock lock = indexLock(knowledgeService);
        KnowledgeIndex previousIndex = activeIndex(knowledgeService);
        try (var executor = Executors.newSingleThreadExecutor()) {
            Future<?> closing;
            lock.readLock().lock();
            try {
                closing = executor.submit(knowledgeService::close);
                await(lock::hasQueuedThreads);
                assertThat(closing.isDone()).isFalse();
                assertThat(previousIndex.reader().getRefCount()).isPositive();
            } finally {
                lock.readLock().unlock();
            }
            closing.get(5, TimeUnit.SECONDS);
            assertThat(previousIndex.reader().getRefCount()).isZero();
        }
    }

    @Test
    @DisplayName("Closing waits for a refresh and prevents any later refresh")
    public void closeDuringRefresh() throws Exception {
        ReentrantReadWriteLock lock = indexLock(knowledgeService);
        KnowledgeIndex previousIndex = activeIndex(knowledgeService);
        try (var executor = Executors.newFixedThreadPool(2)) {
            Future<?> refreshing;
            Future<?> closing;
            lock.readLock().lock();
            try {
                refreshing = executor.submit(() -> {
                    refresh(knowledgeService);
                    return null;
                });
                await(lock::hasQueuedThreads);
                closing = executor.submit(knowledgeService::close);
                assertThat(previousIndex.reader().getRefCount()).isPositive();
            } finally {
                lock.readLock().unlock();
            }
            refreshing.get(5, TimeUnit.SECONDS);
            closing.get(5, TimeUnit.SECONDS);
            KnowledgeIndex finalIndex = activeIndex(knowledgeService);
            assertThat(finalIndex).isNotSameAs(previousIndex);
            assertThat(previousIndex.reader().getRefCount()).isZero();
            assertThat(finalIndex.reader().getRefCount()).isZero();
            assertThatThrownBy(() -> finalIndex.directory().listAll())
                    .isInstanceOf(AlreadyClosedException.class);
            refresh(knowledgeService);
            assertThat(activeIndex(knowledgeService)).isSameAs(finalIndex);
        }
    }

    @Test
    @DisplayName("searchConcepts()")
    public void search() {
        // Searching for an unknown thing should return an empty list.
        assertThat(knowledgeService.searchConcepts("ZZZ")).isEmpty();

        // Searching for a known thing should return a list of results.
        assertThat(knowledgeService.searchConcepts("discount_amount")).hasSize(2)
                .satisfiesExactly(
                        result1 -> assertThat(result1.conceptId()).isEqualTo("tables/orders"),
                        result2 -> assertThat(result2.conceptId()).isEqualTo("policies/revenue-recognition")
                );
    }

    @Test
    @DisplayName("getConceptContent()")
    public void getConceptContent() {
        // Calling an unknown concept should return an empty optional.
        assertThat(knowledgeService.getConceptContent("unknown-concept")).isEmpty();

        // Calling a known concept should return the content of the concept.
        assertThat(knowledgeService.getConceptContent("computations/revenue-ytd")).isPresent()
                .hasValueSatisfying(content ->
                        assertThat(content).containsSubsequence(
                                "---",
                                "type: Attested Computation",
                                "---",
                                "# Computation",
                                "This computation implements the four rules of the FY2026 Revenue Recognition Policy",
                                "# Freshness",
                                "[^revenue-policy]: Revenue Recognition Policy (FY2026)"
                        )
                );
    }

}
