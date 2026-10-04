package io.github.alexxfromgit.taf.cucumber.examples.web.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alexxfromgit.taf.cucumber.core.verify.Verify;
import io.github.alexxfromgit.taf.cucumber.core.web.PageFactory;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.CartPage;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.CheckoutCompletePage;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.CheckoutInformationPage;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.CheckoutOverviewPage;

public class CheckoutSteps {

    @When("I proceed to checkout")
    public void proceedToCheckout() {
        PageFactory.ready(CartPage.class).checkout();
    }

    @When("I enter the shipping information {string}, {string}, {string}")
    public void enterShippingInformation(String first, String last, String postal) {
        PageFactory.ready(CheckoutInformationPage.class).fill(first, last, postal);
    }

    @When("I continue to the order overview")
    public void continueToOverview() {
        PageFactory.create(CheckoutInformationPage.class).continueToOverview();
    }

    @When("I try to continue to the order overview")
    public void tryToContinue() {
        PageFactory.create(CheckoutInformationPage.class).continueExpectingError();
    }

    @When("I finish the order")
    public void finishOrder() {
        PageFactory.ready(CheckoutOverviewPage.class).finish();
    }

    @Then("the order total is {string}")
    public void orderTotal(String expected) {
        Verify.equal(PageFactory.ready(CheckoutOverviewPage.class).total(), expected, "Order total");
    }

    @Then("I see the order confirmation {string}")
    public void orderConfirmation(String expected) {
        Verify.equal(PageFactory.ready(CheckoutCompletePage.class).header(), expected, "Confirmation");
    }

    @Then("I see the checkout error {string}")
    public void checkoutError(String expected) {
        Verify.equal(PageFactory.create(CheckoutInformationPage.class).error(), expected, "Checkout error");
    }
}
