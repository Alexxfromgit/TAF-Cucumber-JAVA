package io.github.alexxfromgit.taf.cucumber.examples.lint;

import io.github.alexxfromgit.taf.cucumber.core.lint.FeatureLinter;
import org.junit.jupiter.api.Test;

/**
 * Static checks on every build, no browser needed: each Feature has an epic, each Scenario an owner, quarantines
 * are valid and short, and no properties file contains a secret.
 */
class FeatureLintTest {

    @Test
    void featuresAreTaggedAndConfigurationIsSecretFree() {
        FeatureLinter.onClasspath().assertClean();
    }
}
