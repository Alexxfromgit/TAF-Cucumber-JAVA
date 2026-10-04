package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.Locate;
import io.github.alexxfromgit.taf.cucumber.core.web.Page;
import io.github.alexxfromgit.taf.cucumber.core.web.PageIdentifier;
import io.github.alexxfromgit.taf.cucumber.core.web.UiElement;

/** Order confirmation. */
public class CheckoutCompletePage extends Page {

    @PageIdentifier
    @Locate(testId = "complete-header")
    private UiElement header;

    public String header() {
        return header.text();
    }
}
