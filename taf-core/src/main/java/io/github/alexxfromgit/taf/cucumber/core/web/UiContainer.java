package io.github.alexxfromgit.taf.cucumber.core.web;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

/** Common base of {@link Page} and {@link Component}: element injection context, name and helpers. */
public abstract class UiContainer implements PageMixin {

    private String name;
    private Supplier<? extends SearchContext> searchContext;

    final void init(String name, Supplier<? extends SearchContext> searchContext) {
        this.name = name;
        this.searchContext = searchContext;
    }

    /** Readable name used in reports and errors, e.g. {@code InventoryPage} or {@code InventoryPage.products[2]}. */
    public final String name() {
        return name;
    }

    Supplier<? extends SearchContext> searchContext() {
        return searchContext;
    }

    protected WebDriver driver() {
        return DriverManager.driver();
    }

    /** A component declared by a class-level {@link Locate}, searched inside this container. */
    @Override
    public <C extends Component> C component(Class<C> type) {
        return PageFactory.component(type, this);
    }

    /**
     * An element whose locator is only known at runtime, e.g. "the product card with this name". Prefer it over
     * picking list items by index when the list can change.
     */
    protected UiElement element(String elementName, By by) {
        return new UiElement(name, elementName, by, searchContext);
    }

    /** Creates {@code type} and waits until it is ready - for navigation that is not a single click. */
    protected <P extends Page> P expect(Class<P> type) {
        return PageFactory.create(type).waitReady();
    }
}
