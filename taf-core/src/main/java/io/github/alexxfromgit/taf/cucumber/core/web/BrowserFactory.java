package io.github.alexxfromgit.taf.cucumber.core.web;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.github.alexxfromgit.taf.cucumber.core.failure.EnvironmentException;
import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Creates browsers from configuration. Drivers are resolved automatically by Selenium Manager.
 * <pre>
 * browser.name=chrome                 # chrome | firefox | edge
 * browser.headless=false
 * browser.window-size=1440x900
 * browser.args=--lang=en-US           # extra command-line arguments, comma-separated
 * browser.caps.acceptInsecureCerts=true
 * selenium.grid.url=http://localhost:4444   # optional: run on Selenium Grid / a cloud instead of locally
 * </pre>
 */
public final class BrowserFactory {

    private BrowserFactory() {
    }

    public static WebDriver create() {
        TafConfig config = TafConfig.get();
        MutableCapabilities options = options(config);
        WebDriver driver;
        try {
            driver = config.optional("selenium.grid.url")
                    .map(url -> (WebDriver) new RemoteWebDriver(toUrl(url), options))
                    .orElseGet(() -> local(options));
        } catch (WebDriverException e) {
            throw new EnvironmentException("Could not start the browser (" + config.string("browser.name") + "): "
                    + firstLine(e.getMessage()), e);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);   // explicit waits only
        driver.manage().timeouts().pageLoadTimeout(config.duration("browser.page-load-timeout"));
        if (!config.bool("browser.headless", false) || config.optional("browser.window-size").isPresent()) {
            windowSize(config).ifPresent(size -> driver.manage().window().setSize(size));
        }
        return driver;
    }

    static MutableCapabilities options(TafConfig config) {
        String browser = config.string("browser.name", "chrome").toLowerCase(Locale.ROOT);
        boolean headless = config.bool("browser.headless", false);
        List<String> args = Arrays.stream(config.string("browser.args", "").split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        MutableCapabilities options = switch (browser) {
            case "chrome" -> {
                ChromeOptions chrome = new ChromeOptions();
                if (headless) {
                    chrome.addArguments("--headless=new");
                }
                chrome.addArguments(args);
                // no "save password" / leaked-password dialogs: they block clicks on demo sites
                chrome.setExperimentalOption("prefs", Map.of(
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false,
                        "profile.password_manager_leak_detection", false));
                yield chrome;
            }
            case "edge" -> {
                EdgeOptions edge = new EdgeOptions();
                if (headless) {
                    edge.addArguments("--headless=new");
                }
                edge.addArguments(args);
                yield edge;
            }
            case "firefox" -> {
                FirefoxOptions firefox = new FirefoxOptions();
                if (headless) {
                    firefox.addArguments("-headless");
                }
                firefox.addArguments(args);
                yield firefox;
            }
            default -> throw new FrameworkException("browser.name must be chrome, firefox or edge, not '" + browser + "'");
        };
        config.withPrefix("browser.caps.").forEach((k, v) -> options.setCapability(k, typed(v)));
        return options;
    }

    private static WebDriver local(MutableCapabilities options) {
        if (options instanceof ChromeOptions chrome) {
            return new ChromeDriver(chrome);
        }
        if (options instanceof EdgeOptions edge) {
            return new EdgeDriver(edge);
        }
        return new FirefoxDriver((FirefoxOptions) options);
    }

    private static java.util.Optional<Dimension> windowSize(TafConfig config) {
        return config.optional("browser.window-size").map(v -> {
            String[] parts = v.toLowerCase(Locale.ROOT).split("x");
            return new Dimension(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        });
    }

    static Object typed(String value) {
        String v = value.trim();
        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(v);
        }
        return v.matches("-?\\d{1,9}") ? (Object) Integer.parseInt(v) : v;
    }

    private static java.net.URL toUrl(String value) {
        try {
            return URI.create(value).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new FrameworkException("Invalid selenium.grid.url '" + value + "'", e);
        }
    }

    private static String firstLine(String message) {
        if (message == null) {
            return "";
        }
        int newline = message.indexOf('\n');
        return newline < 0 ? message : message.substring(0, newline);
    }
}
