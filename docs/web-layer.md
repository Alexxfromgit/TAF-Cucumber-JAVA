# Web layer

## Browsers

| Key | Default | |
|---|---|---|
| `browser.name` | `chrome` | `chrome`, `firefox`, `edge`. Drivers come from Selenium Manager. |
| `browser.headless` | `false` | `env=ci` sets `true` |
| `browser.window-size` | `1440x900` | |
| `browser.args` | | Extra arguments, comma-separated |
| `browser.caps.<name>` | | Extra capabilities, e.g. `browser.caps.acceptInsecureCerts=true` |
| `selenium.grid.url` | | Run remotely on a Selenium Grid or a cloud grid |
| `browser.reuse` | `false` | `false`: new browser per scenario. `true`: keep it per thread and clear cookies and storage between scenarios. |

A browser starts on the first page interaction of a scenario, so API scenarios never open one. Chrome starts with
password-manager prompts disabled, because they block clicks on demo sites.

## Page objects

```java
@Url("/inventory.html")
public class InventoryPage extends Page implements HasHeader {

    @PageIdentifier                              // proves the page is open
    @Locate(testId = "inventory-container")
    private UiElement inventory;

    @Locate(testId = "inventory-item")           // repeated components
    private ComponentList<ProductCard> products;

    @Locate(testId = "product-sort-container")
    private UiElement sort;

    public InventoryPage sortBy(String option) {
        sort.selectByText(option);
        return this;
    }
}
```

- `PageFactory.open(InventoryPage.class)` navigates to `web.base-url` + `@Url` and waits until the page is ready.
- `PageFactory.ready(CartPage.class)` waits for the page the browser shows now.
- `element.clickAndExpect(NextPage.class)` clicks and waits for the next page.

### Locators

`@Locate` takes exactly one of `testId`, `css`, `id`, `name`, `xpath`, `text`, `linkText` or `className`.
`testId = "x"` means `[data-test='x']`; change the attribute with `web.test-id-attribute`. Dedicated test
attributes survive redesigns, so ask developers to add them.

### Components and mixins

A component's elements are searched inside its root:

```java
@Locate(css = ".primary_header")
public class Header extends Component {
    @Locate(testId = "shopping-cart-badge") private UiElement badge;
}

public interface HasHeader extends PageMixin {
    default Header header() { return component(Header.class); }
}
```

`ComponentList<ProductCard>` gives `get(i)`, `find(predicate)`, `first(predicate)`, `stream()` and
`waitAtLeast(n)`, and each item is scoped to its own root. For elements whose locator is only known at runtime, use
`element(name, by)` from inside a page.

### Elements

`UiElement` re-locates the element on every call with an explicit wait (`wait.timeout`, polling `wait.poll`):
`click()`, `type()` (masked for fields named `*password*`), `text()`, `value()`, `attribute()`,
`selectByText()`, `selectByValue()`, `scrollIntoView()`, `isDisplayed()`, `waitVisible()` and `waitGone()`. Every
action is a report step.

## Evidence

When a scenario with an open browser fails, the report gets **Screenshot on failure**, **URL on failure** and
**Page source on failure** (the HTML, to see why a locator did not match). Switch them off with
`evidence.screenshot-on-failure` and `evidence.page-source-on-failure`.
