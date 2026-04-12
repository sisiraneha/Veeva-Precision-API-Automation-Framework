package com.precision.bdd.context;

import com.precision.bdd.models.Pet;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class TestContext {

  private final Map<String, Object> bag = new HashMap<>();
  private Response lastResponse;
  private Pet currentPetPayload;

  public void setLastResponse(Response response) {
    this.lastResponse = response;
  }

  public Response lastResponse() {
    return lastResponse;
  }

  public void put(String key, Object value) {
    bag.put(key, value);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) bag.get(key);
  }

  public long petId() {
    Object id = bag.get("petId");
    if (id instanceof Number n) {
      return n.longValue();
    }
    throw new IllegalStateException("petId not set in context");
  }

  public void setCurrentPetPayload(Pet pet) {
    this.currentPetPayload = pet;
  }

  public Pet currentPetPayload() {
    return currentPetPayload;
  }

  public void clear() {
    bag.clear();
    lastResponse = null;
    currentPetPayload = null;
  }
}
