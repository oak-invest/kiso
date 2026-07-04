package com.oakinvest.kiso.cli.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Objects;

/**
 * Ignore pattern matcher.
 */
public class IgnorePatternMatcher {

    /** Path matchers. */
    private final List<PathMatcher> pathMatchers;

    /**
     * Constructor.
     *
     * @param ignorePatterns ignore patterns
     */
    public IgnorePatternMatcher(final List<String> ignorePatterns) {
        this.pathMatchers = Objects.requireNonNull(ignorePatterns).stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + pattern))
                .toList();
    }

    /**
     * Returns whether the given relative path matches at least one configured ignore pattern.
     *
     * @param relativePath the path relative to the bundle root
     * @return {@code true} if the path matches an ignore pattern; {@code false} otherwise
     */
    public boolean matches(final Path relativePath) {
        return pathMatchers.stream().anyMatch(pathMatcher -> pathMatcher.matches(relativePath));
    }

}
