package io.github.alexxfromgit.taf.cucumber.core.web;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

/** Turns a {@link Locate} into a Selenium {@link By}. */
public final class LocatorResolver {

    private LocatorResolver() {
    }

    public static By resolve(Locate locate, String where) {
        List<By> candidates = new ArrayList<>();
        if (!locate.testId().isEmpty()) {
            candidates.add(testId(locate.testId()));
        }
        if (!locate.css().isEmpty()) {
            candidates.add(By.cssSelector(locate.css()));
        }
        if (!locate.id().isEmpty()) {
            candidates.add(By.id(locate.id()));
        }
        if (!locate.name().isEmpty()) {
            candidates.add(By.name(locate.name()));
        }
        if (!locate.xpath().isEmpty()) {
            candidates.add(By.xpath(locate.xpath()));
        }
        if (!locate.text().isEmpty()) {
            candidates.add(text(locate.text()));
        }
        if (!locate.linkText().isEmpty()) {
            candidates.add(By.linkText(locate.linkText()));
        }
        if (!locate.className().isEmpty()) {
            candidates.add(By.className(locate.className()));
        }
        if (candidates.size() != 1) {
            throw new FrameworkException(where + ": @Locate needs exactly one strategy, found " + candidates.size());
        }
        return candidates.get(0);
    }

    /** {@code [data-test='value']} (attribute from {@code web.test-id-attribute}). */
    public static By testId(String value) {
        String attribute = TafConfig.get().string("web.test-id-attribute", "data-test");
        return By.cssSelector("[" + attribute + "=" + cssString(value) + "]");
    }

    /** Elements whose normalised visible text equals {@code value}. */
    public static By text(String value) {
        return By.xpath("//*[normalize-space(.)=" + xpathString(value) + " and not(*[normalize-space(.)="
                + xpathString(value) + "])]");
    }

    private static String cssString(String value) {
        return "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    static String xpathString(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }
}
