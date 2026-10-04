package io.github.alexxfromgit.taf.cucumber.core.context;

import io.cucumber.java.Scenario;
import org.assertj.core.api.SoftAssertions;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The running scenario on this thread, set by the framework hooks before every scenario. Used by framework code
 * that has no access to Cucumber's {@link Scenario} (soft assertions, logging, evidence). Step definitions share
 * their own data through the injectable {@link ScenarioContext}.
 */
public final class TestContext {

    private static final ThreadLocal<TestContext> CURRENT = ThreadLocal.withInitial(() -> new TestContext(null));

    private final Scenario scenario;
    private SoftAssertions softAssertions;

    private TestContext(Scenario scenario) {
        this.scenario = scenario;
    }

    public static TestContext current() {
        return CURRENT.get();
    }

    public static TestContext start(Scenario scenario) {
        TestContext context = new TestContext(scenario);
        CURRENT.set(context);
        return context;
    }

    public static void clear() {
        CURRENT.remove();
    }

    public Optional<Scenario> scenario() {
        return Optional.ofNullable(scenario);
    }

    public Collection<String> tags() {
        return scenario == null ? List.of() : scenario.getSourceTagNames();
    }

    /** Soft assertions collected during this scenario; verified by the framework after the last step. */
    public SoftAssertions softAssertions() {
        if (softAssertions == null) {
            softAssertions = new SoftAssertions();
        }
        return softAssertions;
    }

    public Optional<SoftAssertions> softAssertionsIfUsed() {
        return Optional.ofNullable(softAssertions);
    }
}
