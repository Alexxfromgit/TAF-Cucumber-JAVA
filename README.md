# cucumber-taf

[![CI](https://github.com/Alexxfromgit/TAF-Cucumber-JAVA/actions/workflows/ci.yml/badge.svg)](https://github.com/Alexxfromgit/TAF-Cucumber-JAVA/actions/workflows/ci.yml)
[![Allure report](https://img.shields.io/badge/report-Allure-orange)](https://alexxfromgit.github.io/TAF-Cucumber-JAVA/)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![Java 21](https://img.shields.io/badge/java-21-informational)

**A ready-to-use Java framework for BDD test automation with Cucumber.** Click **Use this template**, write your
scenarios in Gherkin, and run them against a web UI (Selenium) and REST APIs in parallel, with a readable Allure
report.

It runs on Java 21, Cucumber 7 on the JUnit Platform 6, Selenium 4, REST Assured 5 and Allure 2.

```gherkin
@epic=Web_shop @owner=checkout-team @web
Feature: Checkout

  Background:
    Given I am logged in as the standard user
    And I add "Sauce Labs Backpack" to the cart
    And I open the cart
    And I proceed to checkout

  @smoke @severity=blocker
  Scenario: Complete a purchase
    When I enter the shipping information "Ada", "Tester", "12345"
    And I continue to the order overview
    Then the order total is "$32.39"
    When I finish the order
    Then I see the order confirmation "Thank you for your order!"
```

Each step definition is a one-liner over page objects or API clients. In the report, every Gherkin step shows the
UI actions or HTTP exchanges behind it, with passwords and tokens masked. A failed scenario also gets a screenshot,
its URL and the page HTML.

## Features

| | |
|---|---|
| **Two layers, one language** | Web scenarios on Selenium page objects and API scenarios on REST Assured clients, in the same suite and report. |
| **Declarative page objects** | `@Locate(testId = "login-button")`, components scoped to a root (header, product cards), `ComponentList`, mixins, `@PageIdentifier` readiness, `@Url` navigation. Explicit waits only. |
| **Readable failures** | `LoginPage.loginButton is not visible after 10s [By.cssSelector: [data-test='login-button']]`: page, field and locator in one line. |
| **Parallel by default** | Scenarios run in parallel. Every thread has its own browser, state is shared between steps via PicoContainer, and API-only scenarios never open a browser. |
| **Tags drive metadata** | `@epic=`, `@owner=`, `@severity=` and `@issue=` build the Allure behaviours tree. `@known-issue=GH-12` reports a known bug under *Known issues*, and `@quarantined=2026-11-15` skips a scenario until that date. |
| **Any browser, anywhere** | Chrome, Firefox or Edge, headed or headless. Drivers are resolved automatically by Selenium Manager. Point `selenium.grid.url` at a Grid or a cloud. |
| **Pluggable API auth** | `none`, `bearer`, `api-key`, `basic` or `oauth2` client credentials by configuration, plus tokens obtained in a step. Credentials are masked in reports. |
| **Failure taxonomy** | Allure categories separate *Product defects* and *UI element not found* from *Test data*, *Environment* and *Framework* problems, and show *Known issues*, *Quarantined* and *Undefined steps* separately. |
| **Suite hygiene** | Soft/hard `Verify`, a feature linter (every Feature has an epic, every Scenario an owner, valid quarantines, no secrets in config) and a secrets guard on configuration files. |

## Quick start

Requirements: JDK 21+ and Chrome (Maven comes with the wrapper, and the browser driver is downloaded automatically).

```bash
./mvnw verify
```

This runs the framework's unit tests, then every feature: web scenarios against the public
[Sauce Labs demo shop](https://www.saucedemo.com) and API scenarios against [DummyJSON](https://dummyjson.com).
To open the report:

```bash
./mvnw -pl examples allure:serve
```

| Command | What it does |
|---|---|
| `./mvnw verify -Denv=ci` | Headless browser (as in CI) |
| `./mvnw verify -Dcucumber.filter.tags="@smoke"` | Only scenarios tagged `@smoke` |
| `./mvnw verify -Dcucumber.filter.tags="@api"` | API scenarios only, no browser |
| `./mvnw verify -Dbrowser.name=firefox` | Another browser (`chrome`, `firefox`, `edge`) |
| `./mvnw verify -Denv=grid` | Run on a Selenium Grid (`selenium.grid.url`) |
| `./mvnw verify -Dverify.mode=soft` | Collect all `Verify` failures of a scenario instead of stopping at the first |

## Project layout

```
taf-core/   framework: hooks, web and API layers, config, reporting, linter (+ unit tests, no browser needed)
examples/   features, step definitions, page objects and API clients for the demo targets (replace with your own)
docs/       guides
```

## Documentation

- [Adapting the template to your project](docs/adapting-to-your-project.md): start here
- [Writing features: tags, steps and shared state](docs/writing-features.md)
- [Web layer: browsers, page objects, components](docs/web-layer.md)
- [API layer: clients and authentication](docs/api-layer.md)
- [Configuration reference](docs/configuration.md)
- [Reports, failure taxonomy and suite hygiene](docs/reports.md)
- [Architecture](docs/architecture.md)

## Contributing and license

Contributions are welcome, see [CONTRIBUTING.md](CONTRIBUTING.md). The project is licensed under [MIT](LICENSE).
The examples use public demo services that are not affiliated with this project, see [NOTICE](NOTICE).
