@epic=Shop_API @owner=account-team @api
Feature: Authentication API
  API clients log in to receive a bearer token.

  @smoke @severity=critical
  Scenario: Log in and read the own profile
    When I log in to the API as the api user
    Then I receive an access token
    And my profile username is "emilys"

  Scenario: Wrong password is rejected
    When I log in to the API with username "emilys" and password "wrong"
    Then the response status is 400
    And the error message is "Invalid credentials"
