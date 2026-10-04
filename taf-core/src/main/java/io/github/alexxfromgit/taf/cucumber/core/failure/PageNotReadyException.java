package io.github.alexxfromgit.taf.cucumber.core.failure;

/**
 * A page did not become ready (identifiers visible, spinners gone) within its timeout
 * (Allure: failed, "UI element not found"). Usually a navigation problem or a very slow backend.
 */
public class PageNotReadyException extends AssertionError {

    public PageNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }
}
