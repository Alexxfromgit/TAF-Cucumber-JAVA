package io.github.alexxfromgit.taf.cucumber.core.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Path of a page relative to {@code web.base-url}, used by {@code PageFactory.open(Page.class)}:
 * <pre>{@code @Url("/inventory.html") public class InventoryPage extends Page { ... }}</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Url {

    String value();
}
