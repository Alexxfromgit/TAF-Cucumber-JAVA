# Security policy

## Reporting a vulnerability

Please **do not open a public issue** for security problems. Use GitHub's
[private vulnerability reporting](https://github.com/Alexxfromgit/TAF-Cucumber-JAVA/security/advisories/new) instead.
You will get a response within a few days.

## Secrets in tests

- Passwords and API credentials are read only from environment variables;
- configuration loading fails if a `.properties` file contains a secret-like value, and the feature linter checks
  the same;
- typed passwords and `Authorization`, cookie and API-key headers are masked in Allure reports.

If you find a way these safeguards can be bypassed, please report it as above.
