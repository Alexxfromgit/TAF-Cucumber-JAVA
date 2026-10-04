package io.github.alexxfromgit.taf.cucumber.core.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.github.alexxfromgit.taf.cucumber.core.http.auth.AuthFilter;
import io.github.alexxfromgit.taf.cucumber.core.http.auth.AuthProviders;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for API clients used by step definitions. One subclass per service; configuration lives under
 * {@code services.<id>.*}:
 * <pre>
 * services.shop-api.base-uri=https://dummyjson.com
 * services.shop-api.auth.type=none            # none | bearer | api-key | basic | oauth2 | custom
 * </pre>
 * Every call gets a fresh {@link RequestSpecification} (no shared mutable state, safe for parallel scenarios) with
 * authentication and Allure request/response attachments (credentials masked).
 */
public abstract class ServiceClient {

    private static final List<String> ALWAYS_MASKED = List.of(
            "Authorization", "Proxy-Authorization", "Cookie", "Set-Cookie", "X-API-Key");

    private final String serviceId;

    protected ServiceClient(String serviceId) {
        this.serviceId = serviceId;
    }

    public String serviceId() {
        return serviceId;
    }

    public String baseUri() {
        return TafConfig.get().string(key("base-uri"));
    }

    /** Starting point for every request of this client. */
    protected RequestSpecification request() {
        TafConfig config = TafConfig.get();
        return RestAssured.given()
                .config(restAssuredConfig(config))
                .baseUri(baseUri())
                .contentType(config.string(key("content-type"), "application/json"))
                .accept(config.string(key("accept"), "application/json"))
                .filter(new AuthFilter(AuthProviders.forService(serviceId)))
                .filter(new AllureRestAssured());
    }

    /** A request authenticated with a token obtained at runtime (e.g. from a login step). */
    protected RequestSpecification requestWithBearer(String token) {
        return request().header("Authorization", "Bearer " + token);
    }

    protected String key(String suffix) {
        return "services." + serviceId + "." + suffix;
    }

    private RestAssuredConfig restAssuredConfig(TafConfig config) {
        int connectMs = (int) config.duration("http.connect-timeout").toMillis();
        int readMs = (int) config.duration("http.read-timeout").toMillis();

        List<String> masked = new ArrayList<>(ALWAYS_MASKED);
        config.optional("http.masked-headers").ifPresent(v ->
                Arrays.stream(v.split(",")).map(String::trim).filter(s -> !s.isEmpty()).forEach(masked::add));
        config.optional(key("auth.header")).ifPresent(masked::add);

        return RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", connectMs)
                        .setParam("http.socket.timeout", readMs))
                .logConfig(LogConfig.logConfig()
                        .blacklistHeaders(masked)
                        .enableLoggingOfRequestAndResponseIfValidationFails())
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((type, charset) -> new ObjectMapper()
                                .findAndRegisterModules()
                                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));
    }
}
