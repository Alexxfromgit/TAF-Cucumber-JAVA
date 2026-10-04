package io.github.alexxfromgit.taf.cucumber.core.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.alexxfromgit.taf.cucumber.core.context.TestContext;
import io.github.alexxfromgit.taf.cucumber.core.tags.Quarantine;
import io.github.alexxfromgit.taf.cucumber.core.web.DriverManager;
import io.github.alexxfromgit.taf.cucumber.core.web.WebEvidence;
import org.opentest4j.TestAbortedException;

import java.time.LocalDate;

/**
 * Framework hooks, active when {@code io.github.alexxfromgit.taf.cucumber.core.hooks} is on the glue path.
 * Cucumber runs {@code @After} hooks from the highest order to the lowest, so the sequence after a scenario is:
 * soft assertions, then failure evidence, then browser release.
 */
public class TafHooks {

    @Before(order = Integer.MIN_VALUE)
    public void startScenario(Scenario scenario) {
        TestContext.start(scenario);
        Quarantine.activeUntil(scenario.getSourceTagNames(), LocalDate.now()).ifPresent(until -> {
            throw new TestAbortedException("Quarantined until " + until
                    + " (@quarantined tag): the scenario runs again automatically on that day");
        });
    }

    @After(order = 30_000)
    public void verifySoftAssertions() {
        TestContext.current().softAssertionsIfUsed().ifPresent(soft -> soft.assertAll());
    }

    @After(order = 20_000)
    public void attachEvidence(Scenario scenario) {
        if (scenario.isFailed()) {
            WebEvidence.onFailure();
        }
    }

    @After(order = 0)
    public void releaseBrowser() {
        DriverManager.release();
        TestContext.clear();
    }
}
