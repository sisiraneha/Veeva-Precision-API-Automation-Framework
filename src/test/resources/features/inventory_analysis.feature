@store
Feature: Inventory analysis

  Compare aggregate inventory counts with findByStatus results (assignment test case 2).

  Scenario: Available count from inventory matches findByStatus list size
    When I GET store inventory
    Then the response status should be 200
    When I GET pets findByStatus "available"
    Then the response status should be 200
    And the findByStatus list size matches store inventory for status "available"
