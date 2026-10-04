package io.github.alexxfromgit.taf.cucumber.core.web;

import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.github.alexxfromgit.taf.cucumber.core.failure.ElementNotFoundException;
import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;
import io.github.alexxfromgit.taf.cucumber.core.failure.PageNotReadyException;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A page object. Fields are injected by {@link PageFactory}:
 * <pre>{@code
 * @Url("/inventory.html")
 * public class InventoryPage extends Page implements HasHeader {
 *
 *     @PageIdentifier
 *     @Locate(testId = "inventory-container")
 *     private UiElement inventory;
 *
 *     @Locate(testId = "inventory-item")
 *     private ComponentList<ProductCard> products;
 * }
 * }</pre>
 * Navigation methods return the next page, query methods return values: step definitions stay one-liners.
 */
public abstract class Page extends UiContainer {

    /**
     * Waits until the page is ready: {@link WaitFor#gone()} elements disappeared, then all {@link PageIdentifier}
     * fields are visible.
     */
    @SuppressWarnings("unchecked")
    public <P extends Page> P waitReady() {
        WaitFor waitFor = getClass().getAnnotation(WaitFor.class);
        Duration timeout = waitFor != null && !waitFor.timeout().isBlank()
                ? TafConfig.parseDuration(waitFor.timeout())
                : TafConfig.get().duration("wait.page-timeout");
        try {
            if (waitFor != null) {
                for (Locate gone : waitFor.gone()) {
                    new UiElement(name(), "loading indicator", LocatorResolver.resolve(gone, name() + " @WaitFor(gone)"),
                            searchContext()).waitGone(timeout);
                }
            }
            if (waitFor == null || waitFor.identifiers()) {
                for (UiElement identifier : identifiers()) {
                    identifier.waitVisible(timeout);
                }
            }
        } catch (ElementNotFoundException e) {
            throw new PageNotReadyException(name() + " is not ready after " + Waits.format(timeout) + ": "
                    + e.getMessage(), e);
        }
        return (P) this;
    }

    /** Whether all identifiers are visible right now (no waiting). */
    public boolean isOpen() {
        List<UiElement> identifiers = identifiers();
        return !identifiers.isEmpty() && identifiers.stream().allMatch(UiElement::isDisplayed);
    }

    public String currentUrl() {
        return driver().getCurrentUrl();
    }

    public String title() {
        return driver().getTitle();
    }

    List<UiElement> identifiers() {
        List<UiElement> result = new ArrayList<>();
        for (Class<?> type = getClass(); type != Page.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(PageIdentifier.class)) {
                    if (field.getType() != UiElement.class) {
                        throw new FrameworkException("@PageIdentifier " + name() + "." + field.getName()
                                + " must be a UiElement");
                    }
                    try {
                        field.setAccessible(true);
                        result.add((UiElement) field.get(this));
                    } catch (IllegalAccessException e) {
                        throw new FrameworkException("Cannot read " + field, e);
                    }
                }
            }
        }
        return result;
    }
}
