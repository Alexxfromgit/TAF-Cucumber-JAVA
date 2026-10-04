package io.github.alexxfromgit.taf.cucumber.core.web;

import io.github.alexxfromgit.taf.cucumber.core.failure.ElementNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Repeated components, e.g. product cards. The field's {@link Locate} finds the item roots:
 * <pre>{@code @Locate(css = ".inventory_item") private ComponentList<ProductCard> products;}</pre>
 */
public final class ComponentList<C extends Component> {

    private final Class<C> type;
    private final UiElements items;
    private final UiElement template;

    ComponentList(Class<C> type, UiElement template) {
        this.type = type;
        this.template = template;
        this.items = new UiElements(template);
    }

    /** Item at {@code index} (0-based); its elements are searched inside that item only. */
    public C get(int index) {
        return PageFactory.componentAt(type, template.nth(index));
    }

    /** Current number of items, without waiting. */
    public int size() {
        return items.count();
    }

    public ComponentList<C> waitAtLeast(int minimum) {
        items.waitAtLeast(minimum);
        return this;
    }

    public Stream<C> stream() {
        return IntStream.range(0, size()).mapToObj(this::get);
    }

    public List<C> all() {
        return stream().toList();
    }

    public Optional<C> find(Predicate<C> condition) {
        return stream().filter(condition).findFirst();
    }

    public C first(Predicate<C> condition) {
        return find(condition).orElseThrow(() -> new ElementNotFoundException(
                "No item of " + template.name() + " matches the condition (" + size() + " items checked)", null));
    }
}
