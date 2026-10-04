package io.github.alexxfromgit.taf.cucumber.core.context;

import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Data shared between step definition classes within one scenario. Cucumber (PicoContainer) creates one instance
 * per scenario and injects it into every step class that asks for it in its constructor:
 * <pre>{@code
 * public class AuthSteps {
 *     private final ScenarioContext context;
 *     public AuthSteps(ScenarioContext context) { this.context = context; }
 *
 *     @When("I log in to the API as {user}")
 *     public void login(UserCredentials user) { context.put(Keys.TOKEN, auth.login(user)); }
 * }
 * }</pre>
 * Use typed {@link Key}s instead of raw strings so typos fail at compile time.
 */
public class ScenarioContext {

    /** A typed key, e.g. {@code static final Key<String> TOKEN = Key.of("token", String.class);} */
    public record Key<T>(String name, Class<T> type) {
        public static <T> Key<T> of(String name, Class<T> type) {
            return new Key<>(name, type);
        }
    }

    private final Map<Key<?>, Object> values = new HashMap<>();

    public <T> void put(Key<T> key, T value) {
        values.put(key, value);
    }

    public <T> Optional<T> find(Key<T> key) {
        return Optional.ofNullable(key.type().cast(values.get(key)));
    }

    /** The value, or a {@link FrameworkException} that names the step that should have stored it. */
    public <T> T get(Key<T> key) {
        return find(key).orElseThrow(() -> new FrameworkException("No '" + key.name()
                + "' in the scenario context. A previous step must store it - check the order of the steps."));
    }

    public boolean has(Key<?> key) {
        return values.containsKey(key);
    }
}
