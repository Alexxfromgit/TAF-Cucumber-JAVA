package io.github.alexxfromgit.taf.cucumber.examples.api.steps;

import io.cucumber.java.en.Then;
import io.github.alexxfromgit.taf.cucumber.core.context.ScenarioContext;
import io.github.alexxfromgit.taf.cucumber.core.verify.Verify;

import static io.github.alexxfromgit.taf.cucumber.examples.support.Keys.LAST_RESPONSE;

/** Steps shared by all API features; the response comes from the scenario context. */
public class ApiSteps {

    private final ScenarioContext context;

    public ApiSteps(ScenarioContext context) {
        this.context = context;
    }

    @Then("the response status is {int}")
    public void status(int expected) {
        Verify.equal(context.get(LAST_RESPONSE).statusCode(), expected, "HTTP status");
    }

    @Then("the error message is {string}")
    public void errorMessage(String expected) {
        Verify.equal(context.get(LAST_RESPONSE).jsonPath().getString("message"), expected, "Error message");
    }
}
