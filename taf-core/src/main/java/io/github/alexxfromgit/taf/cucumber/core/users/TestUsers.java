package io.github.alexxfromgit.taf.cucumber.core.users;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;

/**
 * Test accounts by alias. Feature files say {@code Given I am logged in as the standard user}; a parameter type
 * resolves "standard" through this class:
 * <pre>
 * users.standard.username=standard_user     # taf.properties / env file
 * USERS_STANDARD_PASSWORD=...               # environment variable (secret)
 * </pre>
 */
public final class TestUsers {

    private TestUsers() {
    }

    public static UserCredentials get(String alias) {
        TafConfig config = TafConfig.get();
        return new UserCredentials(alias,
                config.string("users." + alias + ".username"),
                config.secret("users." + alias + ".password"));
    }
}
