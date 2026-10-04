package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.Page;
import io.github.alexxfromgit.taf.cucumber.core.web.PageIdentifier;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;
import io.github.alexxfromgit.taf.cucumber.core.web.Url;

/** Sign-in form. */
@Url("/")
public class LoginPage extends Page {

    @PageIdentifier
    @Locate(testId = "login-button")
    private UiElement loginButton;

    @Locate(testId = "username")
    private UiElement username;

    @Locate(testId = "password")
    private UiElement password;

    @Locate(testId = "error")
    private UiElement error;

    public LoginPage fill(String user, String secret) {
        username.type(user);
        password.type(secret);
        return this;
    }

    public InventoryPage loginAs(String user, String secret) {
        fill(user, secret);
        return loginButton.clickAndExpect(InventoryPage.class);
    }

    public LoginPage submit() {
        loginButton.click();
        return this;
    }

    public String error() {
        return error.text();
    }
}
