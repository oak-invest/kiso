package com.oakinvest.kiso.core.v0_1.renderer.util;

import com.oakinvest.kiso.core.renderer.util.TextWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.1 - TextWrapper")
final class TextWrapperTest {

    @Nested
    @DisplayName("wrap(text)")
    class WrapWithDefaults {

        @Test
        @DisplayName("returns empty list for null text")
        void returnsEmptyListForNullText() {
            List<String> result = TextWrapper.wrap(null);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty list for blank text")
        void returnsEmptyListForBlankText() {
            List<String> result = TextWrapper.wrap("   ");
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns single line for short text")
        void returnsSingleLineForShortText() {
            List<String> result = TextWrapper.wrap("Hello");
            assertThat(result).containsExactly("Hello");
        }

        @Test
        @DisplayName("wraps long text into multiple lines")
        void wrapsLongTextIntoMultipleLines() {
            String longText = "The quick brown fox jumps over the lazy dog and continues running";
            List<String> result = TextWrapper.wrap(longText, 20, 3);
            assertThat(result).hasSizeGreaterThanOrEqualTo(1).hasSizeLessThanOrEqualTo(3);
        }

        @Test
        @DisplayName("preserves words")
        void preservesWords() {
            String text = "Hello world from test";
            List<String> result = TextWrapper.wrap(text, 15, 3);
            assertThat(result).allMatch(line -> !line.contains("Hel lo") && !line.contains("wo rld"));
        }

    }

    @Nested
    @DisplayName("wrap(text, maxLineLength, maxLines)")
    class WrapWithCustomSettings {

        @Test
        @DisplayName("respects maximum line length")
        void respectsMaximumLineLength() {
            String text = "The quick brown fox jumps over the lazy dog";
            List<String> result = TextWrapper.wrap(text, 15, 10);
            for (String line : result) {
                if (!line.endsWith("…")) {
                    assertThat(line.length()).isLessThanOrEqualTo(15);
                }
            }
        }

        @Test
        @DisplayName("respects maximum number of lines")
        void respectsMaximumNumberOfLines() {
            String text = "One two three four five six seven eight nine ten";
            List<String> result = TextWrapper.wrap(text, 20, 2);
            assertThat(result).hasSizeLessThanOrEqualTo(2);
        }

        @Test
        @DisplayName("appends ellipsis when truncated")
        void appendsEllipsisWhenTruncated() {
            String text = "One two three four five six seven eight nine ten";
            List<String> result = TextWrapper.wrap(text, 20, 2);
            if (result.size() == 2) {
                assertThat(result.get(1)).endsWith("…");
            }
        }

        @Test
        @DisplayName("does not append ellipsis when text fits maximum lines")
        void doesNotAppendEllipsisWhenTextFitsMaximumLines() {
            String text = "One two three four";
            List<String> result = TextWrapper.wrap(text, 10, 2);
            assertThat(result).containsExactly("One two", "three four");
        }

        @Test
        @DisplayName("handles single long word")
        @SuppressWarnings("SpellCheckingInspection")
        void handlesSingleLongWord() {
            String longWord = "Supercalifragilisticexpialidocious";
            List<String> result = TextWrapper.wrap(longWord, 10, 2);
            assertThat(result).isNotEmpty();
            assertThat(result.getFirst().length()).isLessThanOrEqualTo(11); // 10 + ellipsis
        }

        @Test
        @DisplayName("truncates long word before following lines")
        @SuppressWarnings("SpellCheckingInspection")
        void truncatesLongWordBeforeFollowingLines() {
            String text = "Supercalifragilisticexpialidocious small words";
            List<String> result = TextWrapper.wrap(text, 10, 3);
            assertThat(result.getFirst()).isEqualTo("Supercali…");
            assertThat(result).contains("small");
        }

        @Test
        @DisplayName("handles multiple words exceeding first line")
        void handlesMultipleWordsExceedingFirstLine() {
            String text = "The quick brown fox jumps";
            List<String> result = TextWrapper.wrap(text, 10, 10);
            assertThat(result).hasSizeGreaterThan(1);
        }

    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("handles text with extra spaces")
        void handlesExtraSpaces() {
            String text = "Hello    world    test";
            List<String> result = TextWrapper.wrap(text, 20, 3);
            assertThat(result).contains("Hello world test");
        }

        @Test
        @DisplayName("handles newlines as word separators")
        void handlesNewlines() {
            String text = "Hello\nworld\ntest";
            List<String> result = TextWrapper.wrap(text, 20, 3);
            assertThat(result).contains("Hello world test");
        }

        @Test
        @DisplayName("handles tabs as word separators")
        void handlesTabs() {
            String text = "Hello\tworld\ttest";
            List<String> result = TextWrapper.wrap(text, 20, 3);
            assertThat(result).contains("Hello world test");
        }

        @Test
        @DisplayName("returns single word with ellipsis when max line length is small")
        void returnsSingleWordWithEllipsisWhenSmallLimit() {
            String text = "Extraordinarily";
            List<String> result = TextWrapper.wrap(text, 3, 1);
            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).endsWith("…");
        }

    }

}
