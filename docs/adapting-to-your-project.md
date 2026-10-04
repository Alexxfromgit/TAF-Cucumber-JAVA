# Adapting the template to your project

1. **Create your repository.** Click **Use this template** and run `./mvnw verify`.
2. **Rename (optional).** Change the `groupId` in the POMs and the packages `io.github.alexxfromgit.taf.cucumber.*`
   (IDE refactoring). Then update the glue and plugin class names in
   `examples/src/test/resources/junit-platform.properties`, and the Allure listener in
   `taf-core/src/main/resources/META-INF/services/io.qameta.allure.listener.TestLifecycleListener`.
3. **Point it at your system** in `examples/src/test/resources/taf.properties`:
   ```properties
   web.base-url=https://staging.example.com
   services.orders.base-uri=https://api.staging.example.com
   users.buyer.username=qa.buyer@example.com     # password: USERS_BUYER_PASSWORD
   ```
4. **Model pages.** For each page of your first flow, add a `@PageIdentifier`, the elements the steps need
   (preferably `testId`), and navigation methods that return the next page. See [web-layer.md](web-layer.md).
5. **Write features first, then steps.** Run once, copy the snippets Cucumber prints for undefined steps, and
   implement each one as a single page-object or client call.
6. **Tag everything.** `@epic=` on the feature, `@owner=` on the feature or scenario, `@smoke` for the critical
   path. The linter enforces the first two.
7. **Remove the examples.** Delete the demo features, pages, clients and steps. Keep `RunCucumberTest`,
   `FeatureLintTest`, `support/ParameterTypes` and `support/Keys` (adjust them).
8. **CI.** `.github/workflows/ci.yml` runs everything headless and publishes the report to GitHub Pages. Enable
   *Settings > Pages > Source: GitHub Actions*. Add secrets as repository secrets and map them to environment
   variables in the workflow.
