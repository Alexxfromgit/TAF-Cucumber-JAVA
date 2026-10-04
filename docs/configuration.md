# Configuration

## Layers

Later layers win:

| # | Layer | Typical use |
|---|---|---|
| 1 | `taf/defaults.properties` (inside taf-core) | Framework defaults (below) |
| 2 | `src/test/resources/taf.properties` | Base URLs, services, test users |
| 3 | `src/test/resources/env/<env>.properties` | `default`, `ci` (headless), `grid` |
| 4 | System properties | `-Dbrowser.name=firefox` |
| 5 | Environment variables | `WEB_BASE_URL=...` overrides `web.base-url` (only keys declared in a file) |
| 6 | Runtime overrides | Values set during the run |

The environment is selected by the system property `taf.env` (or the `TAF_ENV` environment variable). The examples
POM maps the Maven property `-Denv=<name>` to it. Values may reference other keys: `web.base-url=${host}/shop`.

Cucumber's own settings (glue, plugins, parallelism) live in `src/test/resources/junit-platform.properties`.
System properties override them, e.g. `-Dcucumber.filter.tags=@smoke` or
`-Dcucumber.execution.parallel.config.fixed.parallelism=1`.

## Secrets

Secrets are read only from environment variables (or `-D` for local runs), never from files: user passwords
(`USERS_<ALIAS>_PASSWORD`) and API credentials (`SERVICES_<ID>_AUTH_...`). Loading fails if a `.properties` file
contains a literal value for a secret-looking key, and the feature linter checks the same. The examples POM passes
the public passwords of the demo sites as system properties.

## Reference

| Key | Default | |
|---|---|---|
| `web.base-url` | | Base of `@Url` paths |
| `web.test-id-attribute` | `data-test` | Attribute used by `@Locate(testId = ...)` |
| `browser.name` / `browser.headless` / `browser.window-size` / `browser.args` | `chrome` / `false` / `1440x900` / | |
| `browser.caps.<name>` | | Extra capabilities |
| `browser.page-load-timeout` | `60s` | |
| `browser.reuse` | `false` | Keep the browser between scenarios on a thread |
| `selenium.grid.url` | | Remote execution |
| `wait.timeout` / `wait.poll` / `wait.page-timeout` | `10s` / `200ms` / `20s` | Explicit waits |
| `services.<id>.base-uri` / `services.<id>.auth.*` | | API clients |
| `http.connect-timeout` / `http.read-timeout` | `10s` / `30s` | |
| `http.masked-headers` | | Extra headers hidden in reports |
| `users.<alias>.username` | | `{user}` parameter type (password from `USERS_<ALIAS>_PASSWORD`) |
| `verify.mode` | `hard` | `soft`: collect `Verify` failures |
| `evidence.screenshot-on-failure` / `evidence.page-source-on-failure` | `true` | |
| `lint.require-epic` / `lint.require-owner` / `lint.secrets` / `lint.quarantine.max-days` | `true` / `true` / `true` / `90` | Feature linter |
| `taf.secrets-guard.enabled` | `true` | Fail when config files contain secret values |
