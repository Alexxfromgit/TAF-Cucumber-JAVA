package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.ComponentList;
import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.Page;
import io.github.alexxfromgit.taf.cucumber.core.web.PageIdentifier;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;
import io.github.alexxfromgit.taf.cucumber.core.web.Url;
import io.github.alexxfromgit.taf.cucumber.core.failure.TestDataException;

import java.math.BigDecimal;
import java.util.List;

/** The product catalog shown after login. */
@Url("/inventory.html")
public class InventoryPage extends Page implements HasHeader {

    @PageIdentifier
    @Locate(testId = "inventory-container")
    private UiElement inventory;

    @Locate(testId = "inventory-item")
    private ComponentList<ProductCard> products;

    @Locate(testId = "product-sort-container")
    private UiElement sort;

    public ComponentList<ProductCard> products() {
        return products.waitAtLeast(1);
    }

    public List<String> names() {
        return products().stream().map(ProductCard::title).toList();
    }

    public List<BigDecimal> prices() {
        return products().stream().map(ProductCard::price).toList();
    }

    public InventoryPage sortBy(String option) {
        sort.selectByText(option);
        return this;
    }

    public ProductCard product(String name) {
        return products().find(p -> p.title().equals(name))
                .orElseThrow(() -> new TestDataException("Product '" + name + "' is not in the catalog: " + names()));
    }
}
