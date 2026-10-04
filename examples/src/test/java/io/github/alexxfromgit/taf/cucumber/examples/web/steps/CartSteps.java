package io.github.alexxfromgit.taf.cucumber.examples.web.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alexxfromgit.taf.cucumber.core.verify.Verify;
import io.github.alexxfromgit.taf.cucumber.core.web.PageFactory;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.CartPage;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.InventoryPage;

import java.util.List;

public class CartSteps {

    @When("I open the cart")
    public void openCart() {
        PageFactory.ready(InventoryPage.class).header().openCart();
    }

    @When("I remove {string} from the cart")
    public void remove(String product) {
        PageFactory.ready(CartPage.class).remove(product);
    }

    @Then("the cart contains:")
    public void cartContains(List<String> expected) {
        Verify.softly().assertThat(PageFactory.ready(CartPage.class).itemNames())
                .as("products in the cart").containsExactlyInAnyOrderElementsOf(expected);
    }

    @Then("the cart is empty")
    public void cartIsEmpty() {
        CartPage cart = PageFactory.ready(CartPage.class);
        Verify.that(cart.itemNames().isEmpty(), "Cart has no products");
        Verify.equal(cart.header().cartCount(), 0, "Cart badge");
    }
}
