package io.github.alexxfromgit.taf.cucumber.examples;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Runs every feature under {@code src/test/resources/features} on the Cucumber JUnit Platform engine.
 * Glue, plugins and parallelism live in {@code junit-platform.properties}; filter with
 * {@code -Dcucumber.filter.tags="@smoke and not @web"}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class RunCucumberTest {
}
