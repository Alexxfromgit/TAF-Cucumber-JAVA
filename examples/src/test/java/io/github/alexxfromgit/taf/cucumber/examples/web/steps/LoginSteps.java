package io.github.alexxfromgit.taf.cucumber.examples.web.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alexxfromgit.taf.cucumber.core.users.UserCredentials;
import io.github.alexxfromgit.taf.cucumber.core.verify.Verify;
import io.github.alexxfromgit.taf.cucumber.core.web.PageFactory;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.InventoryPage;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.LoginPage;

public class LoginSteps {

    @Given("I am on the login page")
    public void openLoginPage() {
        PageFactory.open(LoginPage.class);
    }

    @Given("I am logged in as {user}")
    public void loggedInAs(UserCredentials user) {
        PageFactory.open(LoginPage.class).loginAs(user.username(), user.password());
    }

    @When("I log in as {user}")
    public void logInAs(UserCredentials user) {
        PageFactory.create(LoginPage.class).fill(user.username(), user.password()).submit();
    }

    @When("I log in with username {string} and password {string}")
    public void logInWith(String username, String password) {
        PageFactory.create(LoginPage.class).fill(username, password).submit();
    }

    @Then("I see the product catalog")
    public void catalogIsShown() {
        Verify.that(PageFactory.ready(InventoryPage.class).isOpen(), "Product catalog is shown");
    }

    @Then("I see the login error {string}")
    public void loginError(String expected) {
        Verify.equal(PageFactory.create(LoginPage.class).error(), expected, "Login error");
    }
}
