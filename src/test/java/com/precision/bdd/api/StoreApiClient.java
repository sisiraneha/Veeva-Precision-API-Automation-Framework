package com.precision.bdd.api;

import com.precision.bdd.core.RequestFactory;
import io.restassured.response.Response;

public class StoreApiClient {

  public Response getInventory() {
    return RequestFactory.baseSpec().get("/store/inventory");
  }
}
