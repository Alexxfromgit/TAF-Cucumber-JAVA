package io.github.alexxfromgit.taf.cucumber.examples.api;

import io.github.alexxfromgit.taf.cucumber.core.http.ServiceClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

/** Carts of the DummyJSON shop API. */
public class CartsClient extends ServiceClient {

    public CartsClient() {
        super("shop-api");
    }

    @Step("Create a cart for user {userId}")
    public Response create(long userId, List<Map<String, Object>> products) {
        return request().body(Map.of("userId", userId, "products", products)).post("/carts/add");
    }
}
