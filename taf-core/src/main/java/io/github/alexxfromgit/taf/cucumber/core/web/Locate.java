package io.github.alexxfromgit.taf.cucumber.core.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Where to find a field's element. Set exactly one strategy:
 * <pre>{@code
 * @Locate(testId = "login-button") private UiElement login;     // [data-test='login-button']
 * @Locate(css = ".inventory_item") private ComponentList<ProductCard> products;
 * @Locate(text = "Checkout: Your Information") private UiElement title;
 * }</pre>
 * Prefer {@code testId}: dedicated test attributes survive redesigns. The attribute name is configurable with
 * {@code web.test-id-attribute} (default {@code data-test}). On a {@link Component} class it declares the root.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Locate {

    /** Value of the test attribute ({@code web.test-id-attribute}, default {@code data-test}). */
    String testId() default "";

    String css() default "";

    String id() default "";

    String name() default "";

    String xpath() default "";

    /** Exact visible text (normalised whitespace). */
    String text() default "";

    String linkText() default "";

    String className() default "";
}
