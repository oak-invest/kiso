package com.oakinvest.kiso.cli.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.oakinvest.kiso.core.util.FileConstants.CONFIGURATION_DIRECTORY_NAME;

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
    @SuppressWarnings("checkstyle:MagicNumber")
    public IgnorePatternMatcher(final List<String> ignorePatterns) {
        Objects.requireNonNull(ignorePatterns, "ignorePatterns must not be null");

        this.pathMatchers = Stream.concat(
                        ignorePatterns.stream().flatMap(pattern -> {
                            if (pattern.endsWith("/**")) {
                                return Stream.of(
                                        pattern,
                                        pattern.substring(0, pattern.length() - 3)
                                );
                            }
                            return Stream.of(pattern);
                        }),
                        Stream.of(
                                CONFIGURATION_DIRECTORY_NAME,
                                CONFIGURATION_DIRECTORY_NAME + "/**"
                        )
                )
                .distinct()
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
