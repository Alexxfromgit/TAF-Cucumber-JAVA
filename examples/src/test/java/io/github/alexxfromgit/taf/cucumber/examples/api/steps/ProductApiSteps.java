package io.github.alexxfromgit.taf.cucumber.examples.api.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alexxfromgit.taf.cucumber.core.context.ScenarioContext;
import io.github.alexxfromgit.taf.cucumber.core.verify.Verify;
import io.github.alexxfromgit.taf.cucumber.examples.api.ProductsClient;
import io.restassured.path.json.JsonPath;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.github.alexxfromgit.taf.cucumber.examples.support.Keys.LAST_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductApiSteps {

    private final ScenarioContext context;
    private final ProductsClient products = new ProductsClient();

    public ProductApiSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I request product {long}")
    public void requestProduct(long id) {
        context.put(LAST_RESPONSE, products.product(id));
    }

    @When("I search products for {string}")
    public void search(String query) {
        context.put(LAST_RESPONSE, products.search(query));
    }

    @When("I request products with limit {int} and skip {int}")
    public void page(int limit, int skip) {
        context.put(LAST_RESPONSE, products.page(limit, skip));
    }

    @Then("the product has a title, a price and a positive stock")
    public void productIsComplete() {
        JsonPath product = context.get(LAST_RESPONSE).jsonPath();
        Verify.softly().assertThat(product.getString("title")).as("title").isNotBlank();
        Verify.softly().assertThat(product.getDouble("price")).as("price").isPositive();
        Verify.softly().assertThat(product.getInt("stock")).as("stock").isPositive();
    }

    @Then("every found product mentions {string}")
    public void everyResultMentions(String term) {
        List<Map<String, Object>> found = context.get(LAST_RESPONSE).jsonPath().getList("products");
        assertThat(found).as("search results").isNotEmpty();
        String needle = term.toLowerCase(Locale.ROOT);
        found.forEach(p -> Verify.softly()
                .assertThat((p.get("title") + " " + p.get("description")).toLowerCase(Locale.ROOT))
                .as("product %s", p.get("id")).contains(needle));
    }

    @Then("I get {int} products starting at id {int}")
    public void pageContent(int count, int firstId) {
        List<Integer> ids = context.get(LAST_RESPONSE).jsonPath().getList("products.id", Integer.class);
        Verify.equal(ids.size(), count, "Products on the page");
        Verify.equal(ids.get(0), firstId, "First product id");
    }
}
