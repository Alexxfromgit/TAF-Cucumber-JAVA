@epic=Shop_API @owner=api-team @api
Feature: Products API
  The shop API serves the product catalog.

  @smoke
  Scenario: Get a product by id
    When I request product 1
    Then the response status is 200
    And the product has a title, a price and a positive stock

  Scenario: Search products
    When I search products for "phone"
    Then the response status is 200
    And every found product mentions "phone"

  Scenario Outline: Paginate the catalog
    When I request products with limit <limit> and skip <skip>
    Then the response status is 200
    And I get <limit> products starting at id <first id>

    Examples:
      | limit | skip | first id |
      | 5     | 0    | 1        |
      | 5     | 10   | 11       |

  Scenario: Unknown product
    When I request product 999999
    Then the response status is 404
    And the error message is "Product with id '999999' not found"
