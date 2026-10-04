@epic=Web_shop @owner=shop-team @web
Feature: Shopping cart
  Customers collect products in the cart before checking out.

  Background:
    Given I am logged in as the standard user

  @smoke
  Scenario: Adding products updates the cart badge
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Bike Light" to the cart
    Then the cart badge shows 2

  Scenario: The cart lists the added products
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Onesie" to the cart
    And I open the cart
    Then the cart contains:
      | Sauce Labs Backpack |
      | Sauce Labs Onesie   |

  Scenario: Removing the last product empties the cart
    When I add "Sauce Labs Onesie" to the cart
    And I open the cart
    And I remove "Sauce Labs Onesie" from the cart
    Then the cart is empty
