# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [2.0.0] - 2026-10-04

### Changed
- Rewritten as a framework template: Java 21, Cucumber 7.34 on JUnit Platform 6, Selenium 4.50, REST Assured 5.5
  and Allure 2.35, organised as a Maven reactor (`taf-core` framework and `examples`).

### Added
- Web layer: browser factory (Chrome, Firefox, Edge, headless, Selenium Grid), one lazily started browser per
  thread, declarative page objects (`@Locate`, `@PageIdentifier`, `@Url`, components, `ComponentList`, mixins)
  with explicit waits and readable failures, and failure evidence (screenshot, URL, page HTML).
- API layer: `ServiceClient` with Allure attachments and masked credentials, and pluggable authentication
  (`none`, `bearer`, `api-key`, `basic`, `oauth2`, `custom`) plus runtime bearer tokens.
- Cucumber integration: framework hooks, a run-level plugin, `ScenarioContext` for state shared between steps
  (PicoContainer), the `{user}` parameter type, and parallel scenarios.
- Tag-driven metadata: `@epic=`, `@owner=`, `@severity=`, `@issue=`, `@known-issue=<id>` (a "Known issues"
  category) and `@quarantined=<date>` (skipped until the date).
- Shared foundation: layered configuration with a secrets guard, failure taxonomy with Allure categories, `Verify`
  (soft/hard), `Log` steps and a feature linter.
- Examples: web scenarios against the Sauce Labs demo shop and API scenarios against DummyJSON; CI with GitHub
  Pages report publishing.

### Removed
- The 2018 console-printing data-table exercise (Cucumber 1.2, Java 8).

[Unreleased]: https://github.com/Alexxfromgit/TAF-Cucumber-JAVA/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/Alexxfromgit/TAF-Cucumber-JAVA/releases/tag/v2.0.0
