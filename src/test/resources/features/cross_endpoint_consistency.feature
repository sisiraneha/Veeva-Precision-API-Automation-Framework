@cross
Feature: Cross-endpoint data consistency

  Pet created with a specific category should appear under findByStatus after status change (assignment test case 4).

  Scenario Outline: Pet with category appears in sold list after PUT
    Given a new pet is prepared with base name "<baseName>" category name "<categoryName>" and status "available"
    When I create the pet via POST
    Then the response status should be 200
    And I store the pet id from the response
    When I update the stored pet status to "sold"
    Then the response status should be 200
    When I GET pets findByStatus "sold"
    Then the response status should be 200
    And the stored pet id appears in the returned pet list

    Examples:
      | baseName   | categoryName      |
      | ConsistencyPet | HighValue-Bulldog |
