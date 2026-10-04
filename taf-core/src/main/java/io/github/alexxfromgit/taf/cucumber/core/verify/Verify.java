package io.github.alexxfromgit.taf.cucumber.core.verify;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.github.alexxfromgit.taf.cucumber.core.context.TestContext;
import io.github.alexxfromgit.taf.cucumber.core.failure.PotentialDefectException;
import io.github.alexxfromgit.taf.cucumber.core.log.Log;
import io.github.alexxfromgit.taf.cucumber.core.web.DriverManager;
import io.github.alexxfromgit.taf.cucumber.core.web.WebEvidence;
import io.qameta.allure.model.Status;
import org.assertj.core.api.SoftAssertions;

import java.util.Objects;

/**
 * Business-level checks for {@code Then} steps. Every check becomes an Allure step.
 * <p>
 * {@code verify.mode=hard} (default): the first failed check fails the step.<br>
 * {@code verify.mode=soft}: failures are collected and reported together after the scenario's last step, so one
 * run shows every broken expectation; with a browser open, each soft failure attaches a screenshot.
 * <p>
 * For rich AssertJ assertions use plain {@code assertThat(...)} (hard) or {@link #softly()} (always soft).
 */
public final class Verify {

    private Verify() {
    }

    public static void that(boolean condition, String description) {
        if (condition) {
            Log.step(description, Status.PASSED);
            return;
        }
        fail(description);
    }

    public static void equal(Object actual, Object expected, String description) {
        if (Objects.equals(actual, expected)) {
            Log.step(description + ": " + actual, Status.PASSED);
            return;
        }
        fail(description + ": expected [" + expected + "] but was [" + actual + "]");
    }

    /** Soft assertions bound to the current scenario; verified automatically after its last step. */
    public static SoftAssertions softly() {
        return TestContext.current().softAssertions();
    }

    public static boolean isSoftMode() {
        return "soft".equalsIgnoreCase(TafConfig.get().string("verify.mode", "hard"));
    }

    private static void fail(String message) {
        Log.step(message, Status.FAILED);
        if (isSoftMode()) {
            if (DriverManager.hasBrowser()) {
                WebEvidence.screenshot("Screenshot: " + message);
            }
            softly().fail(message);
        } else {
            throw new PotentialDefectException(message);
        }
    }
}
