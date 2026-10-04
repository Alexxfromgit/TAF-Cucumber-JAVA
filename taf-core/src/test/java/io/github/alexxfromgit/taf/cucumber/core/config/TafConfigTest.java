package io.github.alexxfromgit.taf.cucumber.core.config;

import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TafConfigTest {

    private static final ClassLoader LOADER = TafConfigTest.class.getClassLoader();

    @AfterEach
    void cleanUp() {
        RuntimeOverrides.remove("layer.name");
    }

    @Test
    void projectFileOverridesFrameworkDefaults() {
        TafConfig config = TafConfig.load(Map.of(), Map.of(), LOADER);
        assertThat(config.string("layer.name")).isEqualTo("project");
        assertThat(config.string("verify.mode")).isEqualTo("hard");   // from taf/defaults.properties
        assertThat(config.env()).isEqualTo("default");
    }

    @Test
    void envFileOverridesProjectFile() {
        TafConfig config = TafConfig.load(Map.of("taf.env", "unit"), Map.of(), LOADER);
        assertThat(config.env()).isEqualTo("unit");
        assertThat(config.string("layer.name")).isEqualTo("unit-env");
        assertThat(config.string("layer.project-only")).isEqualTo("from-project");
        assertThat(config.string("layer.env-only")).isEqualTo("from-env");
    }

    @Test
    void envCanBeSelectedWithEnvironmentVariable() {
        TafConfig config = TafConfig.load(Map.of(), Map.of("TAF_ENV", "unit"), LOADER);
        assertThat(config.string("layer.name")).isEqualTo("unit-env");
    }

    @Test
    void systemPropertyOverridesFiles() {
        TafConfig config = TafConfig.load(Map.of("taf.env", "unit", "layer.name", "sys"), Map.of(), LOADER);
        assertThat(config.string("layer.name")).isEqualTo("sys");
    }

    @Test
    void environmentVariableOverridesSystemProperty() {
        TafConfig config = TafConfig.load(Map.of("layer.name", "sys"), Map.of("LAYER_NAME", "envvar"), LOADER);
        assertThat(config.string("layer.name")).isEqualTo("envvar");
    }

    @Test
    void environmentVariablesDoNotShadowUndeclaredKeys() {
        TafConfig config = TafConfig.load(Map.of("java.home", "/jdk"), Map.of("JAVA_HOME", "/other"), LOADER);
        assertThat(config.string("java.home")).isEqualTo("/jdk");
    }

    @Test
    void runtimeOverrideWinsAndPlaceholdersResolveLazily() {
        TafConfig config = TafConfig.load(Map.of(), Map.of("LAYER_NAME", "envvar"), LOADER);
        assertThat(config.string("greeting")).isEqualTo("hello envvar");
        RuntimeOverrides.put("layer.name", "runtime");
        assertThat(config.string("layer.name")).isEqualTo("runtime");
        assertThat(config.string("greeting")).isEqualTo("hello runtime");
    }

    @Test
    void missingKeyExplainsWhereToDefineIt() {
        TafConfig config = TafConfig.load(Map.of(), Map.of(), LOADER);
        assertThatThrownBy(() -> config.string("services.nope.base-uri"))
                .isInstanceOf(FrameworkException.class)
                .hasMessageContaining("SERVICES_NOPE_BASE_URI");
        assertThat(config.optional("services.nope.base-uri")).isEmpty();
    }

    @Test
    void unknownEnvironmentFails() {
        assertThatThrownBy(() -> TafConfig.load(Map.of("taf.env", "does-not-exist"), Map.of(), LOADER))
                .isInstanceOf(FrameworkException.class)
                .hasMessageContaining("env/does-not-exist.properties");
    }

    @Test
    void secretsComeOnlyFromEnvironmentOrSystemProperties() {
        TafConfig config = TafConfig.load(Map.of(), Map.of("SERVICES_API_AUTH_TOKEN", "t0k3n"), LOADER);
        assertThat(config.secret("services.api.auth.token")).isEqualTo("t0k3n");
        assertThatThrownBy(() -> config.secret("services.other.auth.token"))
                .isInstanceOf(FrameworkException.class)
                .hasMessageContaining("SERVICES_OTHER_AUTH_TOKEN");
    }

    @Test
    void typedGettersAndPrefix() {
        TafConfig config = TafConfig.load(Map.of("a.timeout", "1500ms", "a.retries", "3", "a.flag", "true",
                "svc.x.base-uri", "http://x", "svc.x.auth.type", "none"), Map.of(), LOADER);
        assertThat(config.duration("a.timeout")).isEqualTo(Duration.ofMillis(1500));
        assertThat(config.integer("a.retries")).isEqualTo(3);
        assertThat(config.bool("a.flag")).isTrue();
        assertThat(config.withPrefix("svc.x.")).containsEntry("base-uri", "http://x").containsEntry("auth.type", "none");
    }

    @Test
    void durationFormats() {
        assertThat(TafConfig.parseDuration("PT10S")).isEqualTo(Duration.ofSeconds(10));
        assertThat(TafConfig.parseDuration("10s")).isEqualTo(Duration.ofSeconds(10));
        assertThat(TafConfig.parseDuration("2m")).isEqualTo(Duration.ofMinutes(2));
        assertThat(TafConfig.parseDuration("250ms")).isEqualTo(Duration.ofMillis(250));
    }

    @Test
    void envNameMapping() {
        assertThat(TafConfig.toEnvName("services.petstore.base-uri")).isEqualTo("SERVICES_PETSTORE_BASE_URI");
    }
}
