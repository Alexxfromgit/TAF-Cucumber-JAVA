package io.github.alexxfromgit.taf.cucumber.examples.api.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alexxfromgit.taf.cucumber.core.context.ScenarioContext;
import io.github.alexxfromgit.taf.cucumber.core.verify.Verify;
import io.github.alexxfromgit.taf.cucumber.examples.api.CartsClient;
import io.restassured.path.json.JsonPath;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static io.github.alexxfromgit.taf.cucumber.examples.support.Keys.LAST_RESPONSE;

public class CartApiSteps {

    private final ScenarioContext context;
    private final CartsClient carts = new CartsClient();

    public CartApiSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I create a cart for user {long} with:")
    public void createCart(long userId, List<Map<String, String>> lines) {
        List<Map<String, Object>> products = lines.stream()
                .map(l -> Map.<String, Object>of("id", Long.parseLong(l.get("product id")),
                        "quantity", Integer.parseInt(l.get("quantity"))))
                .toList();
        context.put(LAST_RESPONSE, carts.create(userId, products));
    }

    @Then("the cart total equals the sum of its line totals")
    public void totalIsSumOfLines() {
        JsonPath cart = context.get(LAST_RESPONSE).jsonPath();
        BigDecimal sum = cart.getList("products.total", Float.class).stream()
                .map(f -> new BigDecimal(f.toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        Verify.equal(new BigDecimal(cart.getString("total")).setScale(2, RoundingMode.HALF_UP), sum, "Cart total");
    }

    @Then("the cart contains {int} items")
    public void totalQuantity(int expected) {
        Verify.equal(context.get(LAST_RESPONSE).jsonPath().getInt("totalQuantity"), expected, "Items in the cart");
    }
}
