package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.Component;
import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;

/** One line of the cart. */
public class CartItem extends Component {

    @Locate(testId = "inventory-item-name")
    private UiElement title;

    @Locate(css = "button[data-test^='remove']")
    private UiElement remove;

    public String title() {
        return title.text();
    }

    public void remove() {
        remove.click();
        root().waitGone();
    }
}
