package io.github.alexxfromgit.taf.cucumber.core.lint;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureLinterTest {

    private static final TafConfig CONFIG = TafConfig.load(Map.of(), Map.of(), FeatureLinterTest.class.getClassLoader());

    @Test
    void wellTaggedFeatureIsClean() {
        String feature = """
                @epic=Web_shop @owner=shop-team
                Feature: Cart
                  Some description line.

                  Background:
                    Given I am logged in as the standard user

                  @smoke
                  Scenario: Add a product
                    When I add "Backpack" to the cart
                    Then the cart badge shows 1

                  Rule: Checkout
                    # quarantined while the payment sandbox is down
                    @quarantined=%s
                    Scenario Outline: Pay with <card>
                      Then it works
                      Examples:
                        | card |
                        | visa |
                """.formatted(LocalDate.now().plusDays(10));

        assertThat(lint(feature)).isEmpty();
    }

    @Test
    void missingTagsAndBadQuarantinesAreReported() {
        String feature = """
                Feature: Untagged
                  Scenario: No owner anywhere
                    Given something

                  @owner=team @quarantined=%s
                  Scenario: Quarantined for too long
                    Given something

                  @owner=team @quarantined=soon
                  Example: Bad date
                    Given something
                """.formatted(LocalDate.now().plusDays(200));

        assertThat(lint(feature)).extracting(LintViolation::toString).containsExactly(
                "x.feature:1: Feature has no @epic= tag (where does it belong in the report?)",
                "x.feature:2: Scenario 'No owner anywhere' has no @owner= tag (who maintains it?)",
                "x.feature:6: @quarantined=" + LocalDate.now().plusDays(200)
                        + " is more than 90 days away - fix or delete the scenario instead",
                "x.feature:10: @quarantined=soon is not an ISO date (yyyy-MM-dd)");
    }

    private static List<LintViolation> lint(String text) {
        List<LintViolation> out = new ArrayList<>();
        FeatureLinter.lintFeature("x.feature", text, CONFIG, out);
        return out;
    }
}
