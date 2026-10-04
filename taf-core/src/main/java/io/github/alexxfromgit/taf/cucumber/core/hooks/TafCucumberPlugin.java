package io.github.alexxfromgit.taf.cucumber.core.hooks;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.github.alexxfromgit.taf.cucumber.core.allure.AllureResults;
import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.github.alexxfromgit.taf.cucumber.core.web.DriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * Run-level lifecycle, registered as a Cucumber plugin ({@code cucumber.plugin} in junit-platform.properties):
 * installs the Allure categories before the first scenario; writes the Allure environment and closes all browsers
 * after the last one.
 */
public class TafCucumberPlugin implements ConcurrentEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(TafCucumberPlugin.class);

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestRunStarted.class, event -> AllureResults.installCategories());
        publisher.registerHandlerFor(TestRunFinished.class, event -> finish());
    }

    private static void finish() {
        try {
            AllureResults.writeEnvironment(environment());
        } catch (RuntimeException e) {
            LOG.error("Could not write the Allure environment", e);
        } finally {
            DriverManager.quitAll();
        }
    }

    private static Map<String, String> environment() {
        TafConfig config = TafConfig.get();
        Map<String, String> values = new TreeMap<>();
        values.put("browser", config.string("browser.name", "chrome")
                + (config.bool("browser.headless", false) ? " (headless)" : ""));
        config.optional("selenium.grid.url").ifPresent(v -> values.put("selenium.grid", v));
        config.optional("web.base-url").ifPresent(v -> values.put("web.base-url", v));
        config.withPrefix("services.").forEach((key, value) -> {
            if (key.endsWith(".base-uri")) {
                values.put("service." + key.substring(0, key.length() - ".base-uri".length()), value);
            }
        });
        return values;
    }
}
