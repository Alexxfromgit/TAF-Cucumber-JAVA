# Writing features

## Structure

```
examples/src/test/resources/features/
  web/   login.feature, catalog.feature, cart.feature, checkout.feature
  api/   products.feature, auth.feature, carts.feature
examples/src/test/java/.../examples/
  web/pages/   page objects and components
  web/steps/   step definitions for the web layer
  api/         API clients, api/steps/ step definitions
  support/     parameter types, scenario-context keys
```

Write scenarios in business language: *what* the user does and sees, not *how* the UI is clicked. Step definitions
translate one step into one call on a page object or API client.

## Tags

| Tag | Where | Effect |
|---|---|---|
| `@epic=Web_shop` | Feature | Top level of the Allure behaviours tree (`_` is shown as a space). Required by the linter. |
| `@owner=checkout-team` | Feature, Rule or Scenario | Who maintains the scenario. Required by the linter. |
| `@severity=blocker` | Scenario | Allure severity: `blocker`, `critical`, `normal`, `minor`, `trivial` |
| `@issue=GH-7`, `@tmsLink=GH-7` | Scenario | Links (patterns in `allure.properties`) |
| `@known-issue=GH-12` | Scenario | The scenario still runs. A failure is reported under *Known issues* with a link. |
| `@quarantined=2026-11-15` | Scenario, Rule or Feature | Skipped until that date, then runs again automatically. The linter allows at most 90 days. Put the reason in a comment above the tag. |
| `@smoke`, `@web`, `@api`, ... | anywhere | Plain tags for selection: `-Dcucumber.filter.tags="@smoke and not @web"` |

## Sharing state between steps

Step classes are created per scenario by PicoContainer. To pass data from one step class to another (a login token,
the last API response), ask for the `ScenarioContext` in the constructor and use typed keys:

```java
public final class Keys {
    public static final Key<Response> LAST_RESPONSE = Key.of("last API response", Response.class);
}

public class ApiSteps {
    private final ScenarioContext context;
    public ApiSteps(ScenarioContext context) { this.context = context; }

    @Then("the response status is {int}")
    public void status(int expected) {
        Verify.equal(context.get(LAST_RESPONSE).statusCode(), expected, "HTTP status");
    }
}
```

`context.get(key)` fails with a clear message when a previous step did not store the value.

## Test users

```gherkin
Given I am logged in as the standard user
```

The `{user}` parameter type (`support/ParameterTypes`) resolves "standard" to `users.standard.username` plus the
password from the environment variable `USERS_STANDARD_PASSWORD`. Passwords never appear in feature files or
reports.

## Assertions

```java
Verify.equal(cart.total(), "$32.39", "Order total");                 // becomes a report step
Verify.that(catalog.isOpen(), "Product catalog is shown");
Verify.softly().assertThat(names).containsExactly("Backpack");       // soft, checked after the last step
assertThat(prices).isSortedAccordingTo(naturalOrder());              // plain AssertJ: hard
```

With `verify.mode=soft`, `Verify.that/equal` failures are collected, and each one attaches a screenshot when a
browser is open.

## Undefined steps

Cucumber fails a scenario with an undefined step and prints a ready-to-paste snippet (camel-case method names).
The report groups these under *Undefined or pending steps*.
