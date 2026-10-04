package io.github.alexxfromgit.taf.cucumber.core.web;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * One browser per thread, started lazily on first use, so API-only scenarios never open a browser.
 * After each scenario the framework either quits the browser ({@code browser.reuse=false}, default: full isolation)
 * or clears cookies and storage and keeps it for the next scenario on this thread ({@code browser.reuse=true}: faster).
 */
public final class DriverManager {

    private static final Logger LOG = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> CURRENT = new ThreadLocal<>();
    private static final Set<WebDriver> ALL = ConcurrentHashMap.newKeySet();

    private DriverManager() {
    }

    public static WebDriver driver() {
        WebDriver driver = CURRENT.get();
        if (driver == null) {
            driver = BrowserFactory.create();
            CURRENT.set(driver);
            ALL.add(driver);
        }
        return driver;
    }

    public static boolean hasBrowser() {
        return CURRENT.get() != null;
    }

    /** End of a scenario: quit, or clean up for reuse depending on {@code browser.reuse}. */
    public static void release() {
        WebDriver driver = CURRENT.get();
        if (driver == null) {
            return;
        }
        if (TafConfig.get().bool("browser.reuse", false)) {
            try {
                driver.manage().deleteAllCookies();
                ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
                driver.get("about:blank");
                return;
            } catch (RuntimeException e) {
                LOG.warn("Could not reset the browser for reuse, starting a new one next time: {}", e.getMessage());
            }
        }
        quit();
    }

    public static void quit() {
        WebDriver driver = CURRENT.get();
        CURRENT.remove();
        if (driver != null) {
            close(driver);
        }
    }

    /** End of the run: closes the browsers of all threads. */
    public static void quitAll() {
        CURRENT.remove();
        ALL.forEach(DriverManager::close);
    }

    private static void close(WebDriver driver) {
        if (!ALL.remove(driver)) {
            return;
        }
        try {
            driver.quit();
        } catch (RuntimeException e) {
            LOG.warn("Could not quit browser: {}", e.getMessage());
        }
    }
}
