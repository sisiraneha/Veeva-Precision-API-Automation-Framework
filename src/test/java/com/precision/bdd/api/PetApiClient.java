package com.precision.bdd.api;

import com.precision.bdd.core.JsonSupport;
import com.precision.bdd.core.RequestFactory;
import io.restassured.response.Response;

public class PetApiClient {

  public Response createPet(Object body) {
    return RequestFactory.baseSpec().body(JsonSupport.toJson(body)).post("/pet");
  }

  public Response getPet(long petId) {
    return RequestFactory.baseSpec().pathParam("petId", petId).get("/pet/{petId}");
  }

  public Response updatePet(Object body) {
    return RequestFactory.baseSpec().body(JsonSupport.toJson(body)).put("/pet");
  }

  public Response deletePet(long petId) {
    return RequestFactory.baseSpec().pathParam("petId", petId).delete("/pet/{petId}");
  }

  public Response findByStatus(String status) {
    return RequestFactory.baseSpec().queryParam("status", status).get("/pet/findByStatus");
  }
}
