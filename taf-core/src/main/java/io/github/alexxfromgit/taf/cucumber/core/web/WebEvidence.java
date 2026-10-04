package io.github.alexxfromgit.taf.cucumber.core.web;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Evidence for failed scenarios: screenshot, current URL and the page HTML. Attached automatically when a scenario
 * with an open browser fails; never fails the scenario itself.
 */
public final class WebEvidence {

    private static final Logger LOG = LoggerFactory.getLogger(WebEvidence.class);

    private WebEvidence() {
    }

    public static void onFailure() {
        if (!DriverManager.hasBrowser() || Allure.getLifecycle().getCurrentTestCaseOrStep().isEmpty()) {
            return;
        }
        TafConfig config = TafConfig.get();
        if (config.bool("evidence.screenshot-on-failure", true)) {
            screenshot("Screenshot on failure");
        }
        try {
            Allure.addAttachment("URL on failure", "text/uri-list", DriverManager.driver().getCurrentUrl(), ".uri");
        } catch (RuntimeException e) {
            LOG.warn("Could not read the current URL: {}", e.getMessage());
        }
        if (config.bool("evidence.page-source-on-failure", true)) {
            pageSource("Page source on failure");
        }
    }

    public static void screenshot(String name) {
        try {
            WebDriver driver = DriverManager.driver();
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), ".png");
        } catch (RuntimeException e) {
            LOG.warn("Could not take a screenshot: {}", e.getMessage());
        }
    }

    public static void pageSource(String name) {
        try {
            String html = DriverManager.driver().getPageSource();
            Allure.addAttachment(name, "text/html",
                    new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), ".html");
        } catch (RuntimeException e) {
            LOG.warn("Could not read the page source: {}", e.getMessage());
        }
    }
}
