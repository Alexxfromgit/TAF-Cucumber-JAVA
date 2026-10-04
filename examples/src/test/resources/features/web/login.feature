@epic=Web_shop @owner=account-team @web
Feature: Login
  Registered customers sign in with their username and password.

  @smoke @severity=critical
  Scenario: Standard user signs in
    Given I am on the login page
    When I log in as the standard user
    Then I see the product catalog

  Scenario: Locked-out user is rejected
    Given I am on the login page
    When I log in as the locked user
    Then I see the login error "Epic sadface: Sorry, this user has been locked out."

  Scenario Outline: Wrong or missing credentials are explained
    Given I am on the login page
    When I log in with username "<username>" and password "<password>"
    Then I see the login error "<error>"

    Examples:
      | username      | password | error                                                                     |
      |               | anything | Epic sadface: Username is required                                        |
      | standard_user |          | Epic sadface: Password is required                                        |
      | standard_user | wrong    | Epic sadface: Username and password do not match any user in this service |
