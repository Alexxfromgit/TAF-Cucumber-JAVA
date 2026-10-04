package io.github.alexxfromgit.taf.cucumber.examples.support;

import io.github.alexxfromgit.taf.cucumber.core.context.ScenarioContext.Key;
import io.restassured.response.Response;

/** Typed keys for data that steps share through the {@code ScenarioContext}. */
public final class Keys {

    public static final Key<Response> LAST_RESPONSE = Key.of("last API response", Response.class);
    public static final Key<String> ACCESS_TOKEN = Key.of("API access token", String.class);

    private Keys() {
    }
}
