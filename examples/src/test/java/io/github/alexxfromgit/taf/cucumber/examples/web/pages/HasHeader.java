package io.github.alexxfromgit.taf.cucumber.examples.web.pages;

import io.github.alexxfromgit.taf.cucumber.core.web.PageMixin;

/** Mixin for pages that show the header. */
public interface HasHeader extends PageMixin {

    default Header header() {
        return component(Header.class);
    }
}
