package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.validation.rule.AttestedComputationRule;
import com.oakinvest.kiso.core.validation.rule.BrokenLinkRule;
import com.oakinvest.kiso.core.validation.rule.DuplicateSourceIdRule;
import com.oakinvest.kiso.core.validation.rule.EncodingRule;
import com.oakinvest.kiso.core.validation.rule.MarkdownFileRule;
import com.oakinvest.kiso.core.validation.rule.SourceFootnoteRule;
import com.oakinvest.kiso.core.validation.rule.TrustEventRule;
import com.oakinvest.kiso.core.validation.rule.ValidFrontmatterRule;
import com.oakinvest.kiso.core.validation.rule.ValidLogRule;
import com.oakinvest.kiso.core.validation.rule.ValidSourceRule;
import com.oakinvest.kiso.core.validation.rule.ValidStaleAfterRule;
import com.oakinvest.kiso.core.validation.rule.ValidStatusRule;
import com.oakinvest.kiso.core.validation.rule.ValidUsageWindowRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Validation runner")
class ValidationRunnerTest {

    @Test
    @DisplayName("All markdown file rules are registered")
    void allMarkdownFileRulesAreRegistered() throws ReflectiveOperationException {
        final Field rulesField = ValidationRunner.class.getDeclaredField("MARKDOWN_FILE_RULES");
        rulesField.setAccessible(true);

        @SuppressWarnings("unchecked") final List<MarkdownFileRule> registeredRules = (List<MarkdownFileRule>) rulesField.get(null);

        assertThat(registeredRules)
                .extracting(Object::getClass)
                .containsExactlyInAnyOrder(
                        AttestedComputationRule.class,
                        BrokenLinkRule.class,
                        DuplicateSourceIdRule.class,
                        EncodingRule.class,
                        SourceFootnoteRule.class,
                        TrustEventRule.class,
                        ValidFrontmatterRule.class,
                        ValidLogRule.class,
                        ValidSourceRule.class,
                        ValidStaleAfterRule.class,
                        ValidStatusRule.class,
                        ValidUsageWindowRule.class
                );
    }

}
