package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.Page;
import io.github.alexxfromgit.taf.cucumber.core.web.PageIdentifier;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;

/** Checkout step 1: shipping information. */
public class CheckoutInformationPage extends Page {

    @PageIdentifier
    @Locate(testId = "firstName")
    private UiElement firstName;

    @Locate(testId = "lastName")
    private UiElement lastName;

    @Locate(testId = "postalCode")
    private UiElement postalCode;

    @Locate(testId = "continue")
    private UiElement continueButton;

    @Locate(testId = "error")
    private UiElement error;

    public CheckoutInformationPage fill(String first, String last, String postal) {
        firstName.type(first);
        lastName.type(last);
        postalCode.type(postal);
        return this;
    }

    public CheckoutOverviewPage continueToOverview() {
        return continueButton.clickAndExpect(CheckoutOverviewPage.class);
    }

    public CheckoutInformationPage continueExpectingError() {
        continueButton.click();
        return this;
    }

    public String error() {
        return error.text();
    }
}
