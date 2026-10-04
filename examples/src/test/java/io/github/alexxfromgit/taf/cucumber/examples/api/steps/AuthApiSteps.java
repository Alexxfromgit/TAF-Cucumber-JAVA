package io.github.alexxfromgit.taf.cucumber.examples.api.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alexxfromgit.taf.cucumber.core.context.ScenarioContext;
import io.github.alexxfromgit.taf.cucumber.core.users.UserCredentials;
import io.github.alexxfromgit.taf.cucumber.core.verify.Verify;
import io.github.alexxfromgit.taf.cucumber.examples.api.AuthClient;
import io.restassured.response.Response;

import static io.github.alexxfromgit.taf.cucumber.examples.support.Keys.ACCESS_TOKEN;
import static io.github.alexxfromgit.taf.cucumber.examples.support.Keys.LAST_RESPONSE;

public class AuthApiSteps {

    private final ScenarioContext context;
    private final AuthClient auth = new AuthClient();

    public AuthApiSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I log in to the API as {user}")
    public void logIn(UserCredentials user) {
        logInWith(user.username(), user.password());
    }

    @When("I log in to the API with username {string} and password {string}")
    public void logInWith(String username, String password) {
        Response response = auth.login(username, password);
        context.put(LAST_RESPONSE, response);
        if (response.statusCode() == 200) {
            context.put(ACCESS_TOKEN, response.jsonPath().getString("accessToken"));
        }
    }

    @Then("I receive an access token")
    public void receivedToken() {
        Verify.that(context.find(ACCESS_TOKEN).filter(t -> t.split("\\.").length == 3).isPresent(),
                "Access token is a JWT");
    }

    @Then("my profile username is {string}")
    public void profileUsername(String expected) {
        Response me = auth.me(context.get(ACCESS_TOKEN));
        Verify.equal(me.statusCode(), 200, "Profile status");
        Verify.equal(me.jsonPath().getString("username"), expected, "Profile username");
    }
}
