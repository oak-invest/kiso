package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.util.BaseTest;
import com.oakinvest.kiso.core.validation.rule.ValidLogRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_LOG_DATE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Valid log rule")
public class ValidLogRuleTest extends BaseTest {

    final ValidLogRule rule = new ValidLogRule();

    @Test
    @DisplayName("Date headings")
    void dateHeadings(@TempDir Path temporaryDirectory) throws IOException {

        // Validate log.md date headings use the YYYY-MM-DD format =====================================================
        Files.writeString(temporaryDirectory.resolve("log.md"), """
                # Bundle history
                
                ## 2026-07-01
                - line 1.
                
                ## 2026-13-30
                - Line 2.
                
                ## Note a date
                
                """);


        // We load the bundle and check errors =========================================================================
        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);
        var markdownFile = bundle.rootBundle().markdownFiles().getFirst();

        assertThat(rule.validate(bundleWith(markdownFile), markdownFile))
                .satisfiesExactly(
                        issue1 -> {
                            assertThat(issue1.code()).isEqualTo(INVALID_LOG_DATE_FORMAT);
                            assertThat(issue1.message()).isEqualTo("Invalid heading date format - Not an ISO 8601 date: 2026-13-30");
                            assertThat(issue1.path()).isEqualTo(Path.of("log.md"));
                        },
                        issue2 -> {
                            assertThat(issue2.code()).isEqualTo(INVALID_LOG_DATE_FORMAT);
                            assertThat(issue2.message()).isEqualTo("Invalid heading date format - Not an ISO 8601 date: Note a date");
                            assertThat(issue2.path()).isEqualTo(Path.of("log.md"));
                        }
                );


    }

}
