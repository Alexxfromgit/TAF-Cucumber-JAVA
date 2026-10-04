# Contributing

Thanks for helping! Bug reports, docs fixes and features are all welcome.

## Build

```bash
./mvnw verify            # unit tests + all features (needs Chrome and internet for the demo sites)
./mvnw verify -Denv=ci   # headless
```

JDK 21+ is required. CI uses Temurin 21.

## Guidelines

- **Framework code goes into `taf-core`** and must not know about the example sites. Every behaviour change needs a
  unit test that runs without a browser (Mockito).
- Keep step definitions thin: one page-object or client call per step.
- Keep the framework free of shared mutable state, because scenarios run in parallel. Per-scenario state goes into
  `TestContext` or `ScenarioContext`.
- Prefer configuration keys with sensible defaults over new mandatory setup. Document new keys in
  `docs/configuration.md` and `taf/defaults.properties`.
- Never commit secrets, internal hostnames or real personal data.
- Commit messages: imperative mood, short subject line (`Add Edge options`).

## Pull requests

1. Open an issue first for larger changes, so we can agree on the approach.
2. Keep PRs focused, and update `CHANGELOG.md` under *Unreleased*.
3. Make sure `./mvnw verify` is green.

By contributing you agree that your contributions are licensed under the MIT License.
