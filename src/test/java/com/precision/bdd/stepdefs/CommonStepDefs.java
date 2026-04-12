package com.precision.bdd.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import com.precision.bdd.context.TestContext;
import io.cucumber.java.en.Then;

public class CommonStepDefs {

  private final TestContext context;

  public CommonStepDefs(TestContext context) {
    this.context = context;
  }

  @Then("the response status should be {int}")
  public void theResponseStatusShouldBe(int expected) {
    assertThat(context.lastResponse())
        .as("HTTP response should exist")
        .isNotNull();
    int actual = context.lastResponse().getStatusCode();
    assertThat(actual)
        .as("Expected status %d but was %d. Body: %s", expected, actual, context.lastResponse().asString())
        .isEqualTo(expected);
  }
}
