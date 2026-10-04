# Reports, failure taxonomy and suite hygiene

## Allure

The Allure Cucumber plugin turns every scenario into a test with its Gherkin steps. The framework adds:
- UI actions and HTTP exchanges as sub-steps, with credentials masked;
- the epic label from `@epic=`;
- failure evidence (screenshot, URL, page HTML);
- `environment.properties` (browser, grid, base URLs).

```bash
./mvnw -pl examples allure:serve
```

CI publishes the report of `main` to GitHub Pages.

## Failure taxonomy

| Category | Status | Comes from |
|---|---|---|
| Known issues | failed / broken | Scenarios tagged `@known-issue=ID` |
| UI element not found | failed | `ElementNotFoundException`, `PageNotReadyException`: the UI changed or a locator is outdated |
| Product defects | failed | Any other assertion: the product behaves differently from the scenario |
| Test data problems | broken | `TestDataException`, e.g. a product that is not in the catalog |
| Environment / infrastructure | broken | Browser or grid not available, timeouts, refused connections |
| Quarantined | skipped | Scenarios tagged `@quarantined=<date>` |
| Undefined or pending steps | broken / skipped | Steps without a definition, or marked pending |
| Framework problems | broken | `FrameworkException`: misconfiguration or a bug in the step code |

Throw the exception that says why a step could not pass. Then the report tells you who has to act.

## Known issues and quarantine

- `@known-issue=GH-12`: the scenario runs. A failure is prefixed with `[Known issue GH-12]`, linked, and grouped
  under *Known issues*, so a known bug does not look like a new defect.
- `@quarantined=2026-11-15`: the scenario is skipped until that date and then runs again automatically. The linter
  rejects dates more than 90 days away.

## Feature linter

`FeatureLintTest` runs with every build, no browser needed, and fails when:
- a Feature has no `@epic=`;
- a Scenario has no `@owner=` (directly or inherited);
- a `@quarantined=` date is invalid or too far away;
- a `.properties` file contains a secret-looking value.

## Retries

There are no automatic retries by design: a failure is categorised, not hidden. For a flaky environment, Surefire
can rerun failed scenarios with `-Dsurefire.rerunFailingTestsCount=1`. Use it as a stopgap, not a default.
