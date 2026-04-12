package com.precision.bdd.core;

import com.precision.bdd.config.Configuration;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public final class RequestFactory {

  private RequestFactory() {}

  public static RequestSpecification baseSpec() {
    return RestAssured.given()
        .baseUri(Configuration.baseUri())
        .contentType("application/json")
        .accept("application/json");
  }
}
