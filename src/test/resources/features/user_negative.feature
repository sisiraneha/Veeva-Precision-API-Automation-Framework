@user
Feature: User security and error handling

  Negative paths for user endpoints (assignment test case 3).

  Scenario Outline: Invalid email, missing user, and bad login
    Given a user payload with username "<username>" email "<email>" password "<password>"
    When I create the user via POST
    Then the response status should be allowed for user create
    When I GET user "nonExistentUser123"
    Then the response status should be 404
    And the response body should contain "User not found" ignoring case
    When I login with username "wrongUser" and password "wrongPass"
    Then the login must not issue a valid session token

    Examples:
      | username   | email         | password |
      | neg_user_1 | invalid_email | secret1  |
