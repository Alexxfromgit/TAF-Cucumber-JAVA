# API layer

## Clients

```java
public class ProductsClient extends ServiceClient {
    public ProductsClient() { super("shop-api"); }

    @Step("GET product {id}")
    public Response product(long id) {
        return request().pathParam("id", id).get("/products/{id}");
    }
}
```

```properties
services.shop-api.base-uri=https://dummyjson.com
```

Every call gets a fresh REST Assured specification with the configured authentication and Allure request/response
attachments. `Authorization`, cookies and API-key headers are masked in the report. Steps store the response in the
`ScenarioContext`, so shared steps such as `the response status is {int}` can check it.

## Authentication

Static credentials come from configuration:

| `services.<id>.auth.type` | Keys | Secret (environment variable) |
|---|---|---|
| `none` (default) | | |
| `bearer` | | `SERVICES_<ID>_AUTH_TOKEN` |
| `api-key` | `auth.header` (default `X-API-Key`), `auth.in=header\|query`, `auth.param` | `SERVICES_<ID>_AUTH_TOKEN` |
| `basic` | `auth.username` | `SERVICES_<ID>_AUTH_PASSWORD` |
| `oauth2` | `auth.token-url`, `auth.client-id`, `auth.scope` | `SERVICES_<ID>_AUTH_CLIENT_SECRET` |
| `custom` | `auth.class` = your `AuthProvider` | your choice |

For tokens obtained during a scenario (log in as a user, then call an endpoint as that user), use
`requestWithBearer(token)`:

```java
@Step("GET own profile")
public Response me(String accessToken) {
    return requestWithBearer(accessToken).get("/auth/me");
}
```
