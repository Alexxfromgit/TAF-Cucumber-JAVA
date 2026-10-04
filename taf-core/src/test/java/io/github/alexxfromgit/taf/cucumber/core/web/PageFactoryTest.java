package io.github.alexxfromgit.taf.cucumber.core.web;

import io.github.alexxfromgit.taf.cucumber.core.failure.ElementNotFoundException;
import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;
import io.github.alexxfromgit.taf.cucumber.core.failure.PageNotReadyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PageFactoryTest {

    // --- fixtures: a page with a header component, a product list and a test-id locator ------------------------

    @Locate(css = ".header")
    public static class Header extends Component {
        @Locate(testId = "cart-link")
        private UiElement cart;

        UiElement cart() {
            return cart;
        }
    }

    public static class ProductCard extends Component {
        @Locate(testId = "item-name")
        private UiElement title;

        String title() {
            return title.text();
        }
    }

    public interface HasHeader extends PageMixin {
        default Header header() {
            return component(Header.class);
        }
    }

    @Url("/inventory.html")
    public static class InventoryPage extends Page implements HasHeader {
        @PageIdentifier
        @Locate(testId = "inventory-container")
        private UiElement inventory;

        @Locate(css = ".item")
        private ComponentList<ProductCard> products;

        UiElement inventory() {
            return inventory;
        }

        ComponentList<ProductCard> products() {
            return products;
        }
    }

    public static class TwoStrategiesPage extends Page {
        @Locate(css = "a", id = "b")
        private UiElement ambiguous;
    }

    // ----------------------------------------------------------------------------------------------------------

    private SearchContext browser;

    @BeforeEach
    void mockBrowser() {
        browser = mock(SearchContext.class);
        when(browser.findElements(any(By.class))).thenReturn(List.of());
    }

    @Test
    void elementsKnowTheirNameAndLocator() {
        InventoryPage page = PageFactory.create(InventoryPage.class, () -> browser);

        assertThat(page.inventory().name()).isEqualTo("InventoryPage.inventory");
        assertThat(page.inventory().by()).isEqualTo(By.cssSelector("[data-test='inventory-container']"));
    }

    @Test
    void waitReadySucceedsWhenIdentifiersAreVisible() {
        WebElement inventory = visible();
        when(browser.findElements(By.cssSelector("[data-test='inventory-container']"))).thenReturn(List.of(inventory));

        InventoryPage page = PageFactory.create(InventoryPage.class, () -> browser);

        assertThat(page.<InventoryPage>waitReady()).isSameAs(page);
        assertThat(page.isOpen()).isTrue();
    }

    @Test
    void waitReadyNamesTheMissingIdentifier() {
        InventoryPage page = PageFactory.create(InventoryPage.class, () -> browser);

        assertThatThrownBy(page::waitReady)
                .isInstanceOf(PageNotReadyException.class)
                .hasMessageContaining("InventoryPage is not ready")
                .hasMessageContaining("InventoryPage.inventory is not visible")
                .hasMessageContaining("inventory-container");
    }

    @Test
    void componentsSearchInsideTheirRoot() {
        WebElement header = visible();
        WebElement cart = visible();
        when(browser.findElements(By.cssSelector(".header"))).thenReturn(List.of(header));
        when(header.findElements(By.cssSelector("[data-test='cart-link']"))).thenReturn(List.of(cart));

        InventoryPage page = PageFactory.create(InventoryPage.class, () -> browser);
        page.header().cart().click();

        verify(cart).click();
        assertThat(page.header().cart().name()).isEqualTo("InventoryPage.header.cart");
    }

    @Test
    void listItemsAreScopedToTheirOwnRoot() {
        WebElement first = visible();
        WebElement second = visible();
        WebElement firstName = visible();
        WebElement secondName = visible();
        when(firstName.getText()).thenReturn("Backpack");
        when(secondName.getText()).thenReturn("Bike Light");
        when(browser.findElements(By.cssSelector(".item"))).thenReturn(List.of(first, second));
        when(first.findElements(By.cssSelector("[data-test='item-name']"))).thenReturn(List.of(firstName));
        when(second.findElements(By.cssSelector("[data-test='item-name']"))).thenReturn(List.of(secondName));

        InventoryPage page = PageFactory.create(InventoryPage.class, () -> browser);

        assertThat(page.products().size()).isEqualTo(2);
        assertThat(page.products().get(1).title()).isEqualTo("Bike Light");
        assertThat(page.products().first(p -> p.title().equals("Bike Light")).name())
                .isEqualTo("InventoryPage.products[1]");
    }

    @Test
    void missingElementFailsWithFieldNameAndLocator() {
        InventoryPage page = PageFactory.create(InventoryPage.class, () -> browser);

        assertThatThrownBy(() -> page.inventory().click())
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage("InventoryPage.inventory is not visible after 300ms "
                        + "[By.cssSelector: [data-test='inventory-container']]");
    }

    @Test
    void locateNeedsExactlyOneStrategy() {
        assertThatThrownBy(() -> PageFactory.create(TwoStrategiesPage.class, () -> browser))
                .isInstanceOf(FrameworkException.class)
                .hasMessageContaining("TwoStrategiesPage.ambiguous: @Locate needs exactly one strategy, found 2");
    }

    @Test
    void textLocatorHandlesQuotes() {
        assertThat(LocatorResolver.xpathString("plain")).isEqualTo("'plain'");
        assertThat(LocatorResolver.xpathString("it's")).isEqualTo("\"it's\"");
        assertThat(LocatorResolver.xpathString("it's \"x\"")).isEqualTo("concat('it', \"'\", 's \"x\"')");
    }

    private static WebElement visible() {
        WebElement element = mock(WebElement.class);
        when(element.isDisplayed()).thenReturn(true);
        when(element.findElements(any(By.class))).thenReturn(List.of());
        return element;
    }
}
