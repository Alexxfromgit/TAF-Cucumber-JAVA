package io.github.alexxfromgit.taf.cucumber.core.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks elements that prove the page is open. {@code waitReady()} waits until all of them are visible.
 * Every page needs at least one.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PageIdentifier {
}
