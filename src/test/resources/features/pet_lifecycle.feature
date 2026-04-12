@pet
Feature: Pet lifecycle (CRUD and chaining)

  As an API consumer
  I want to manage a pet through its full lifecycle
  So that create, read, update, and delete flows stay consistent

  Scenario Outline: Create, verify, update status, delete, and confirm removal
    Given a new pet is prepared with base name "<baseName>" and category id <categoryId> and status "available"
    When I create the pet via POST
    Then the response status should be 200
    And I store the pet id from the response
    When I retrieve the pet by stored id with GET
    Then the response status should be 200
    And the pet name should match the prepared name
    And the pet status should be "available"
    When I update the stored pet status to "sold"
    Then the response status should be 200
    When I delete the stored pet
    Then the delete operation completes successfully
    When I confirm the stored pet is removed via GET

    Examples:
      | baseName    | categoryId |
      | LifecyclePet | 1          |
      | LifecycleDog | 2          |
