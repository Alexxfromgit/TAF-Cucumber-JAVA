package io.github.alexxfromgit.taf.cucumber.examples.api;

import io.github.alexxfromgit.taf.cucumber.core.http.ServiceClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

/** Authentication of the DummyJSON shop API (JWT bearer tokens). */
public class AuthClient extends ServiceClient {

    public AuthClient() {
        super("shop-api");
    }

    @Step("Log in as '{username}'")
    public Response login(String username, String password) {
        return request().body(Map.of("username", username, "password", password, "expiresInMins", 30))
                .post("/auth/login");
    }

    @Step("GET own profile")
    public Response me(String accessToken) {
        return requestWithBearer(accessToken).get("/auth/me");
    }
}
