package io.github.alexxfromgit.taf.cucumber.core.web;

/**
 * A reusable piece of UI (header, product card, dialog) whose elements are searched inside its root element.
 * The root comes from the {@link Locate} on the declaring field, or from a {@link Locate} on the component class
 * itself (needed for {@code component(Header.class)} and mixins).
 */
public abstract class Component extends UiContainer {

    private UiElement root;

    final void root(UiElement root) {
        this.root = root;
    }

    public UiElement root() {
        return root;
    }

    public boolean isDisplayed() {
        return root.isDisplayed();
    }
}
