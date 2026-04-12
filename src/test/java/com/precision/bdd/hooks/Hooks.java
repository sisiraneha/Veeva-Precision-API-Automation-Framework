package com.precision.bdd.hooks;

import com.precision.bdd.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks {

  private static final Logger log = LogManager.getLogger(Hooks.class);
  private final TestContext context;

  public Hooks(TestContext context) {
    this.context = context;
  }

  @Before
  public void beforeScenario(Scenario scenario) {
    context.clear();
    log.info("Start scenario: {}", scenario.getName());
  }

  @After
  public void afterScenario(Scenario scenario) {
    if (scenario.isFailed() && context.lastResponse() != null) {
      String body = context.lastResponse().asPrettyString();
      scenario.log("Last response body:\n" + body);
      log.warn("Failed scenario {} — last response logged to report", scenario.getName());
    }
  }
}
