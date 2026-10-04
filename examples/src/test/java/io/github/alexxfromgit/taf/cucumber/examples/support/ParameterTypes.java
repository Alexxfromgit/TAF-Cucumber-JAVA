package io.github.alexxfromgit.taf.cucumber.examples.support;

import io.cucumber.java.ParameterType;
import io.github.alexxfromgit.taf.cucumber.core.users.TestUsers;
import io.github.alexxfromgit.taf.cucumber.core.users.UserCredentials;

/** Custom Cucumber parameter types used in the feature files. */
public class ParameterTypes {

    /** {@code the standard user} -> credentials of alias "standard" ({@code users.standard.*}). */
    @ParameterType("the (\\w+) user")
    public UserCredentials user(String alias) {
        return TestUsers.get(alias);
    }
}
