package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.Page;
import io.github.alexxfromgit.taf.cucumber.core.web.PageIdentifier;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;

/** Checkout step 2: order overview with totals. */
public class CheckoutOverviewPage extends Page {

    @PageIdentifier
    @Locate(testId = "finish")
    private UiElement finish;

    @Locate(testId = "total-label")
    private UiElement total;

    /** "Total: $32.39" -> "$32.39" */
    public String total() {
        return total.text().replace("Total:", "").trim();
    }

    public CheckoutCompletePage finish() {
        return finish.clickAndExpect(CheckoutCompletePage.class);
    }
}
