package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.Component;
import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;

/** Top bar shown on every page after login: cart and badge. */
@Locate(css = ".primary_header")
public class Header extends Component {

    @Locate(testId = "shopping-cart-link")
    private UiElement cart;

    @Locate(testId = "shopping-cart-badge")
    private UiElement badge;

    public CartPage openCart() {
        return cart.clickAndExpect(CartPage.class);
    }

    /** Number on the cart icon; 0 when the badge is hidden. */
    public int cartCount() {
        return badge.isDisplayed() ? Integer.parseInt(badge.text().trim()) : 0;
    }
}
