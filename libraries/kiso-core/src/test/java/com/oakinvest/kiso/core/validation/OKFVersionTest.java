package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.util.contants.OKFVersion;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OKF Versions tests")
class OKFVersionTest {

    @ParameterizedTest
    @ValueSource(strings = {"0.1", "0.2", " 0.2 "})
    @DisplayName("Recognizes known versions")
    void recognizesKnownVersions(String version) {
        Assertions.assertThat(OKFVersion.exists(version)).isTrue();
        assertThat(OKFVersion.isValidFormat(version)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "0.3", "1.0", "12.34", " 1.0 "})
    @DisplayName("Accepts unknown but valid versions")
    void acceptsUnknownVersionFormat(String version) {
        assertThat(OKFVersion.exists(version)).isFalse();
        assertThat(OKFVersion.isValidFormat(version)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "v0.2", "V0.2", "bonjour", "0", "0.2.1", "0.2-beta", "-1.0", "0.-1", "0. 2"})
    @DisplayName("Rejects invalid versions")
    void rejectsInvalidVersions(String version) {
        assertThat(OKFVersion.exists(version)).isFalse();
        assertThat(OKFVersion.isValidFormat(version)).isFalse();
    }
}
