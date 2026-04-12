package com.precision.bdd.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.precision.bdd.api.PetApiClient;
import com.precision.bdd.api.StoreApiClient;
import com.precision.bdd.context.TestContext;
import com.precision.bdd.models.Category;
import com.precision.bdd.models.Pet;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PetStepDefs {

  private final TestContext context;
  private final PetApiClient petApi;
  private final StoreApiClient storeApi;

  public PetStepDefs(TestContext context, PetApiClient petApi, StoreApiClient storeApi) {
    this.context = context;
    this.petApi = petApi;
    this.storeApi = storeApi;
  }

  @Given("a new pet is prepared with base name {string} and category id {long} and status {string}")
  public void preparePetSimpleCategory(String baseName, long categoryId, String status) {
    preparePetInternal(baseName, categoryId, null, status);
  }

  @Given("a new pet is prepared with base name {string} category name {string} and status {string}")
  public void preparePetWithCategoryName(String baseName, String categoryName, String status) {
    long categoryId = ThreadLocalRandom.current().nextLong(1, 9_999_999);
    preparePetInternal(baseName, categoryId, categoryName, status);
  }

  private void preparePetInternal(String baseName, long categoryId, String categoryNameOrNull, String status) {
    String uniqueName = baseName + "-" + UUID.randomUUID();
    Pet pet = new Pet();
    long clientId = ThreadLocalRandom.current().nextLong(10_000_000_000L, 9_000_000_000_000_000L);
    pet.setId(clientId);
    pet.setName(uniqueName);
    pet.setStatus(status);
    pet.setPhotoUrls(List.of("https://example.com/photo.png"));
    pet.setTags(Collections.emptyList());
    Category category = new Category();
    category.setId(categoryId);
    category.setName(categoryNameOrNull != null ? categoryNameOrNull : "default");
    pet.setCategory(category);
    context.setCurrentPetPayload(pet);
    context.put("expectedPetName", uniqueName);
  }

  @When("I create the pet via POST")
  public void iCreateThePetViaPost() {
    Pet pet = context.currentPetPayload();
    assertThat(pet).as("Pet payload should be prepared").isNotNull();
    Response r = petApi.createPet(pet);
    context.setLastResponse(r);
  }

  @And("I store the pet id from the response")
  public void iStoreThePetIdFromTheResponse() {
    Response r = context.lastResponse();
    assertThat(r).isNotNull();
    long id = r.jsonPath().getLong("id");
    context.put("petId", id);
    Pet updated = context.currentPetPayload();
    if (updated != null) {
      updated.setId(id);
    }
  }

  @When("I retrieve the pet by stored id with GET")
  public void iRetrieveThePetByStoredIdWithGet() throws InterruptedException {
    Response r = null;
    for (int attempt = 0; attempt < 20; attempt++) {
      r = petApi.getPet(context.petId());
      if (r.getStatusCode() == 200) {
        break;
      }
      if (r.getStatusCode() != 404) {
        break;
      }
      Thread.sleep(150);
    }
    context.setLastResponse(r);
  }

  @And("the pet name should match the prepared name")
  public void thePetNameShouldMatchThePreparedName() {
    String expected = context.get("expectedPetName");
    String actual = context.lastResponse().jsonPath().getString("name");
    assertThat(actual).as("Pet name should match value from feature-driven payload").isEqualTo(expected);
  }

  @And("the pet status should be {string}")
  public void thePetStatusShouldBe(String expectedStatus) {
    String actual = context.lastResponse().jsonPath().getString("status");
    assertThat(actual).as("Pet status").isEqualTo(expectedStatus);
  }

  @When("I update the stored pet status to {string}")
  public void iUpdateTheStoredPetStatusTo(String newStatus) throws InterruptedException {
    Response current = null;
    for (int attempt = 0; attempt < 25; attempt++) {
      current = petApi.getPet(context.petId());
      if (current.getStatusCode() == 200) {
        break;
      }
      Thread.sleep(150);
    }
    assertThat(current).isNotNull();
    assertThat(current.getStatusCode()).as("load pet before PUT").isEqualTo(200);
    Map<String, Object> body = current.jsonPath().getMap("$");
    body.put("status", newStatus);
    Response r = petApi.updatePet(body);
    context.setLastResponse(r);
    Pet pet = context.currentPetPayload();
    if (pet != null) {
      pet.setStatus(newStatus);
    }
  }

  @When("I delete the stored pet")
  public void iDeleteTheStoredPet() throws InterruptedException {
    Response r = null;
    for (int attempt = 0; attempt < 20; attempt++) {
      r = petApi.deletePet(context.petId());
      if (r.getStatusCode() == 200) {
        break;
      }
      if (r.getStatusCode() != 404) {
        break;
      }
      Thread.sleep(250);
    }
    context.setLastResponse(r);
  }

  /**
   * Petstore often returns 200 for DELETE; under load it may return 404 with an empty body while
   * the pet is already gone or not yet visible to that node. Accept 200, or 404 only if GET
   * confirms the pet no longer exists.
   */
  @Then("the delete operation completes successfully")
  public void theDeleteOperationCompletesSuccessfully() throws InterruptedException {
    Response del = context.lastResponse();
    assertThat(del).as("DELETE response should exist").isNotNull();
    if (del.getStatusCode() == 200) {
      return;
    }
    assertThat(del.getStatusCode())
        .as("Unexpected DELETE status; body=%s", del.asString())
        .isEqualTo(404);
    for (int i = 0; i < 20; i++) {
      Response g = petApi.getPet(context.petId());
      if (g.getStatusCode() == 404) {
        context.setLastResponse(g);
        return;
      }
      Thread.sleep(200);
    }
    fail(
        "DELETE returned 404 but GET /pet/{id} still did not return 404 — shared Petstore may be inconsistent");
  }

  @When("I confirm the stored pet is removed via GET")
  public void iConfirmTheStoredPetIsRemovedViaGet() throws InterruptedException {
    Response r = null;
    for (int attempt = 0; attempt < 30; attempt++) {
      r = petApi.getPet(context.petId());
      context.setLastResponse(r);
      if (r.getStatusCode() == 404) {
        return;
      }
      Thread.sleep(200);
    }
    assertThat(r).isNotNull();
    assertThat(r.getStatusCode())
        .as("Pet should be gone after delete; last body: %s", r.asString())
        .isEqualTo(404);
  }

  @When("I GET pets findByStatus {string}")
  public void iGetPetsFindByStatus(String status) {
    Response r = petApi.findByStatus(status);
    context.setLastResponse(r);
  }

  @And("the stored pet id appears in the returned pet list")
  public void theStoredPetIdAppearsInTheReturnedPetList() throws InterruptedException {
    long createdId = context.petId();
    boolean found = false;
    List<Map<String, Object>> pets = null;
    for (int attempt = 0; attempt < 25; attempt++) {
      Response r = petApi.findByStatus("sold");
      context.setLastResponse(r);
      assertThat(r.getStatusCode()).isEqualTo(200);
      pets = r.jsonPath().getList("$");
      assertThat(pets).as("Sold pets list").isNotNull();
      found =
          pets.stream()
              .anyMatch(
                  m -> {
                    Object id = m.get("id");
                    return id instanceof Number && ((Number) id).longValue() == createdId;
                  });
      if (found) {
        return;
      }
      Thread.sleep(200);
    }
    assertThat(found)
        .as("Expected pet id %d to appear in findByStatus=sold (list size %d)", createdId, pets.size())
        .isTrue();
  }

  @When("I GET store inventory")
  public void iGetStoreInventory() {
    Response r = storeApi.getInventory();
    context.setLastResponse(r);
  }

  @And("the findByStatus list size matches store inventory for status {string}")
  public void findByStatusMatchesStoreInventory(String statusKey) throws InterruptedException {
    assertThatMatchInventoryAndList(statusKey, 40, 250);
  }

  /**
   * Public Petstore is shared; inventory and findByStatus can diverge briefly. Poll until equal or
   * attempts exhausted.
   */
  private void assertThatMatchInventoryAndList(String statusKey, int maxAttempts, long sleepMs)
      throws InterruptedException {
    Integer lastInventory = null;
    int lastListSize = -1;
    for (int attempt = 0; attempt < maxAttempts; attempt++) {
      Response invResp = storeApi.getInventory();
      assertThat(invResp.getStatusCode()).as("inventory GET").isEqualTo(200);
      Map<String, Object> map = invResp.jsonPath().getMap("$");
      Object raw = map.get(statusKey);
      assertThat(raw).as("Inventory key '%s'", statusKey).isNotNull();
      lastInventory =
          raw instanceof Number ? ((Number) raw).intValue() : Integer.parseInt(raw.toString());

      Response listResp = petApi.findByStatus(statusKey);
      assertThat(listResp.getStatusCode()).as("findByStatus GET").isEqualTo(200);
      context.setLastResponse(listResp);
      List<?> list = listResp.jsonPath().getList("$");
      assertThat(list).as("findByStatus list").isNotNull();
      lastListSize = list.size();

      if (lastListSize == lastInventory) {
        return;
      }
      Thread.sleep(sleepMs);
    }
    assertThat(lastListSize)
        .as(
            "After %d attempts, list size should match /store/inventory for '%s' (last inventory=%s)",
            maxAttempts,
            statusKey,
            lastInventory)
        .isEqualTo(lastInventory);
  }
}
