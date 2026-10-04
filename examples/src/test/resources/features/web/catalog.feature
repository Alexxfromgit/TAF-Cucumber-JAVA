@epic=Web_shop @owner=shop-team @web
Feature: Product catalog
  Customers browse the catalog and sort it.

  Background:
    Given I am logged in as the standard user

  @smoke
  Scenario: Catalog shows products with prices
    Then the catalog shows 6 products
    And every product has a price

  Scenario: Sort by price, low to high
    When I sort the products by "Price (low to high)"
    Then the products are sorted by price ascending

  Scenario: Sort by name, Z to A
    When I sort the products by "Name (Z to A)"
    Then the products are sorted by name descending
