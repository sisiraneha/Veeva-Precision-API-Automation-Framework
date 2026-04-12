package com.precision.bdd.api;

import com.precision.bdd.core.JsonSupport;
import com.precision.bdd.core.RequestFactory;
import io.restassured.response.Response;

public class UserApiClient {

  public Response createUser(Object body) {
    return RequestFactory.baseSpec().body(JsonSupport.toJson(body)).post("/user");
  }

  public Response getUser(String username) {
    return RequestFactory.baseSpec().pathParam("username", username).get("/user/{username}");
  }

  public Response login(String username, String password) {
    return RequestFactory.baseSpec()
        .queryParam("username", username)
        .queryParam("password", password)
        .get("/user/login");
  }
}
