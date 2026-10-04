package io.github.alexxfromgit.taf.cucumber.core.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares how a page becomes ready, on top of its {@link PageIdentifier} fields:
 * <pre>{@code
 * @WaitFor(gone = @Locate(css = ".spinner"), timeout = "30s")
 * public class SearchResultsPage extends Page { ... }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface WaitFor {

    /** Elements that must disappear first (spinners, overlays). */
    Locate[] gone() default {};

    /** Whether to wait for the {@link PageIdentifier} fields to be visible. */
    boolean identifiers() default true;

    /** Overrides {@code wait.page-timeout}, e.g. {@code "30s"}. */
    String timeout() default "";
}
