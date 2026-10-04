package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import org.openqa.selenium.By;

import io.github.alexxfromgit.taf.cucumber.core.web.Component;
import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;

import java.math.BigDecimal;

/** One product of the catalog grid; its elements are searched inside the card only. */
public class ProductCard extends Component {

    @Locate(testId = "inventory-item-name")
    private UiElement title;

    @Locate(testId = "inventory-item-price")
    private UiElement price;

    @Locate(css = "button[data-test^='add-to-cart']")
    private UiElement addToCart;

    public String title() {
        return title.text();
    }

    /** "$29.99" -> 29.99 */
    public BigDecimal price() {
        return new BigDecimal(price.text().replace("$", "").trim());
    }

    public void addToCart() {
        addToCart.click();
    }
}
