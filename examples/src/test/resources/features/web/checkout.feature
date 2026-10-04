@epic=Web_shop @owner=checkout-team @web
Feature: Checkout
  Customers buy the products in their cart.

  Background:
    Given I am logged in as the standard user
    And I add "Sauce Labs Backpack" to the cart
    And I open the cart
    And I proceed to checkout

  @smoke @severity=blocker
  Scenario: Complete a purchase
    When I enter the shipping information "Ada", "Tester", "12345"
    And I continue to the order overview
    Then the order total is "$32.39"
    When I finish the order
    Then I see the order confirmation "Thank you for your order!"

  Scenario Outline: Shipping information is required
    When I enter the shipping information "<first name>", "<last name>", "<postal code>"
    And I try to continue to the order overview
    Then I see the checkout error "<error>"

    Examples:
      | first name | last name | postal code | error                          |
      |            | Tester    | 12345       | Error: First Name is required  |
      | Ada        |           | 12345       | Error: Last Name is required   |
      | Ada        | Tester    |             | Error: Postal Code is required |
