package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.ComponentList;
import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.Page;
import io.github.alexxfromgit.taf.cucumber.core.web.PageIdentifier;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;
import io.github.alexxfromgit.taf.cucumber.core.web.Url;
import io.github.alexxfromgit.taf.cucumber.core.failure.TestDataException;

import java.util.List;

/** "Your Cart". */
@Url("/cart.html")
public class CartPage extends Page implements HasHeader {

    @PageIdentifier
    @Locate(testId = "cart-list")
    private UiElement list;

    @Locate(testId = "inventory-item")
    private ComponentList<CartItem> items;

    @Locate(testId = "checkout")
    private UiElement checkout;

    public List<String> itemNames() {
        return items.stream().map(CartItem::title).toList();
    }

    public CartPage remove(String name) {
        items.find(i -> i.title().equals(name))
                .orElseThrow(() -> new TestDataException("'" + name + "' is not in the cart: " + itemNames()))
                .remove();
        return this;
    }

    public CheckoutInformationPage checkout() {
        return checkout.clickAndExpect(CheckoutInformationPage.class);
    }
}
