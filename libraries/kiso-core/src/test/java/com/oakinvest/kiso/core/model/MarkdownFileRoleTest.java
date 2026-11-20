package com.oakinvest.kiso.core.model;

import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MarkdownFile role-based access control")
class MarkdownFileRoleTest {

    private final MarkdownFile markdownFile = MarkdownFile.builder()
            .frontmatter(Frontmatter.builder()
                    .roles(List.of("data_engineer", "data_analyst", "finance_analyst"))
                    .build())
            .build();

    @Test
    @DisplayName("hasRole returns true for an existing role and false for non-existing roles")
    void normalizesRequestedRoles() {
        assertThat(markdownFile.hasRole("data_engineer")).isTrue();
        assertThat(markdownFile.hasRole("admin")).isFalse();
        assertThat(markdownFile.hasRole("DATa_ENGINEER")).isTrue();
        assertThat(markdownFile.hasRole(" data_engineer ")).isTrue();
    }

    @Test
    @DisplayName("hasRole returns false for null and blank roles")
    void rejectsNullAndBlankRoles() {
        assertThat(markdownFile.hasRole(null)).isFalse();
        assertThat(markdownFile.hasRole("")).isFalse();
        assertThat(markdownFile.hasRole(" ")).isFalse();
    }

    @Test
    @DisplayName("Returns false for a role that is not present in the frontmatter")
    void returnsFalseForNonExistingRole() {
        assertThat(markdownFile.hasRole("admin")).isFalse();
        assertThat(markdownFile.hasAllRoles(List.of("admin"))).isFalse();
        assertThat(markdownFile.hasRole("finance_analyst")).isTrue();
        assertThat(markdownFile.hasAllRoles(List.of("finance_analyst"))).isTrue();
    }

    @Test
    @DisplayName("hasAllRoles returns true if any requested role is present and false otherwise")
    void matchesAnyNormalizedRequestedRole() {
        assertThat(markdownFile.hasAllRoles(List.of("finance_analyst", "data_engineer"))).isTrue();
        assertThat(markdownFile.hasAllRoles(List.of("data_engineer"))).isTrue();
        assertThat(markdownFile.hasAllRoles(List.of("data_engineer", "data_engineer"))).isTrue();
        assertThat(markdownFile.hasAllRoles(List.of("data_engineer", "admin"))).isTrue();
        assertThat(markdownFile.hasAllRoles(List.of("DATA_ENGINEER"))).isTrue();
        assertThat(markdownFile.hasAllRoles(List.of("admin", "unknown"))).isFalse();
    }

    @Test
    @DisplayName("hasAllRoles returns false for null, empty, or invalid role lists")
    void rejectsNullEmptyAndInvalidRoleLists() {
        assertThat(markdownFile.hasAllRoles(null)).isFalse();
        assertThat(markdownFile.hasAllRoles(List.of())).isFalse();
        assertThat(markdownFile.hasAllRoles(Arrays.asList("data_engineer", null))).isTrue();
        assertThat(markdownFile.hasAllRoles(List.of("data_engineer", " "))).isTrue();
        assertThat(markdownFile.hasAllRoles(Arrays.asList(null, " "))).isFalse();
    }

    @Test
    @DisplayName("hasRole and hasAllRoles return false when frontmatter or roles are absent")
    void returnsFalseWhenFrontmatterOrRolesAreAbsent() {
        MarkdownFile withoutFrontmatter = MarkdownFile.builder().build();
        MarkdownFile withoutRoles = MarkdownFile.builder()
                .frontmatter(Frontmatter.builder().title("Example").build())
                .build();

        assertThat(withoutFrontmatter.hasRole("data_engineer")).isFalse();
        assertThat(withoutFrontmatter.hasAllRoles(List.of("data_engineer"))).isFalse();
        assertThat(withoutRoles.hasRole("data_engineer")).isFalse();
        assertThat(withoutRoles.hasAllRoles(List.of("data_engineer"))).isFalse();
    }

}
