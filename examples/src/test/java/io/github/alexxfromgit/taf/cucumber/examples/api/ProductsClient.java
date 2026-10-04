package io.github.alexxfromgit.taf.cucumber.examples.api;

import io.github.alexxfromgit.taf.cucumber.core.http.ServiceClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

/** Products of the DummyJSON shop API. */
public class ProductsClient extends ServiceClient {

    public ProductsClient() {
        super("shop-api");
    }

    @Step("GET product {id}")
    public Response product(long id) {
        return request().pathParam("id", id).get("/products/{id}");
    }

    @Step("Search products for '{query}'")
    public Response search(String query) {
        return request().queryParam("q", query).get("/products/search");
    }

    @Step("GET products (limit {limit}, skip {skip})")
    public Response page(int limit, int skip) {
        return request().queryParam("limit", limit).queryParam("skip", skip).get("/products");
    }
}
