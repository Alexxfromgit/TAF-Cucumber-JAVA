package io.github.alexxfromgit.taf.cucumber.examples.web.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alexxfromgit.taf.cucumber.core.verify.Verify;
import io.github.alexxfromgit.taf.cucumber.core.web.PageFactory;
import io.github.alexxfromgit.taf.cucumber.examples.web.pages.InventoryPage;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CatalogSteps {

    private InventoryPage catalog() {
        return PageFactory.ready(InventoryPage.class);
    }

    @When("I sort the products by {string}")
    public void sortBy(String option) {
        catalog().sortBy(option);
    }

    @When("I add {string} to the cart")
    public void addToCart(String product) {
        catalog().product(product).addToCart();
    }

    @Then("the catalog shows {int} products")
    public void productCount(int expected) {
        Verify.equal(catalog().products().size(), expected, "Products in the catalog");
    }

    @Then("every product has a price")
    public void everyProductHasAPrice() {
        assertThat(catalog().prices()).as("product prices").allMatch(p -> p.signum() > 0);
    }

    @Then("the products are sorted by price ascending")
    public void sortedByPriceAscending() {
        List<BigDecimal> prices = catalog().prices();
        assertThat(prices).as("prices").isSortedAccordingTo(Comparator.naturalOrder());
    }

    @Then("the products are sorted by name descending")
    public void sortedByNameDescending() {
        assertThat(catalog().names()).as("product names").isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Then("the cart badge shows {int}")
    public void cartBadge(int expected) {
        Verify.equal(catalog().header().cartCount(), expected, "Cart badge");
    }
}
