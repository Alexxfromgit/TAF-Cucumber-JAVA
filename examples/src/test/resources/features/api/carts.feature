@epic=Shop_API @owner=checkout-team @api
Feature: Carts API
  The shop API computes cart totals.

  Scenario: A new cart computes its totals
    When I create a cart for user 1 with:
      | product id | quantity |
      | 1          | 2        |
      | 2          | 1        |
    Then the response status is 201
    And the cart total equals the sum of its line totals
    And the cart contains 3 items
