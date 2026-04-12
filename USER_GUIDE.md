# User guide — Precision API BDD framework

This guide explains how to **run**, **read**, **change**, and **extend** the automation project day to day.

---

## 1. What this project does

- Reads **Gherkin** scenarios from `src/test/resources/features/*.feature`.
- **Cucumber** matches each step to Java methods in `com.precision.bdd.stepdefs`.
- Those methods call the **Pet / Store / User** clients, which use **REST Assured** to hit the API defined in `config.properties` (default: public Swagger Petstore).
- Results appear in the **console**, **Surefire** XML/HTML, and **Cucumber** HTML/JSON under `target/`.

You do **not** need a local Petstore server unless you change `base.uri` to point to your own mock or deployment.

---

## 2. Before you start

| Check | Command / action |
|--------|-------------------|
| Java 17+ | `java -version` |
| Maven 3.8+ | `mvn -version` |
| Network | Tests call `https://petstore.swagger.io` (or your `base.uri`) |

**IntelliJ (recommended):** install the **Cucumber for Java** plugin so you can click from a `.feature` line to its step definition.

---

## 3. Running tests

### 3.1 Full suite (command line)

From the project root (folder that contains `pom.xml`):

```bash
mvn clean test
```

- **Success:** build ends with `BUILD SUCCESS` and scenarios are green in the log.
- **Failure:** note the **scenario name** and the **failed step**; open `target/cucumber-report.html` for a structured view.

### 3.2 Full suite (IntelliJ)

1. Open the project via *File → Open* → select the root directory.
2. Wait for Maven import to finish.
3. Open `src/test/java/com/precision/bdd/runner/TestRunner.java`.
4. Right-click the class → **Run 'TestRunner'** (or use the gutter green arrow).

### 3.3 Run one feature file (IntelliJ)

1. Open a file under `src/test/resources/features/`.
2. Right-click inside the editor → **Run 'Feature: …'** (if the Cucumber plugin offers it), or run a scenario by its gutter icon.

*(Tag-based filtering from Maven is not pre-wired; use the IDE, or temporarily set tags in `src/test/resources/junit-platform.properties` as described in the README.)*

---

## 4. Where to find outputs

| Output | Location | Use |
|--------|----------|-----|
| Cucumber HTML report | `target/cucumber-report.html` | Share with reviewers; open in a browser |
| Cucumber JSON | `target/cucumber-report.json` | CI dashboards, custom reporting |
| Surefire reports | `target/surefire-reports/` | JUnit XML for Jenkins/GitHub Actions |
| Console log | Terminal / IDE run window | Quick feedback while developing |

If a scenario fails, the **Hooks** class attaches the **last response body** to the Cucumber report when possible.

---

## 5. How the project is organized

| Area | Path | Your typical task |
|------|------|-------------------|
| Scenarios (readable tests) | `src/test/resources/features/` | Edit/add `.feature` files |
| Step glue code | `src/test/java/.../stepdefs/` | Map new Gherkin steps to Java |
| HTTP clients | `src/test/java/.../api/` | Add or change API calls |
| Base URL & JSON | `core/`, `config/` | Change defaults, serialization |
| Shared data per scenario | `context/TestContext.java` | Store ids, last response (via DI) |
| Runner & Cucumber plugins | `runner/TestRunner.java` | Report format, glue package |
| Settings | `src/test/resources/config.properties` | `base.uri` |
| Logging | `src/test/resources/log4j2.xml` | Log levels |

**Dependency injection:** `cucumber-picocontainer` creates a **new `TestContext` per scenario** and injects it into step classes and hooks.

---

## 6. Changing the target API

1. Edit `src/test/resources/config.properties`:

   ```properties
   base.uri=https://your-host/your-base-path
   ```

2. Run `mvn clean test` again.

Paths in clients are **relative** to that base (e.g. `/pet`, `/store/inventory`). If your API uses different paths or auth, update `RequestFactory` and the `*ApiClient` classes.

---

## 7. Reading and editing feature files

Feature files use **Gherkin**:

- **Feature** — group of scenarios.
- **Scenario** / **Scenario Outline** — one test path; **Outline** + **Examples** repeats with different data.
- **Given / When / Then / And** — steps; wording must match a **step definition** in Java (regex/cucumber expressions).

Example pattern already in the project:

- Dynamic pet names are built in Java from a **base name** in the Examples table plus a UUID, so runs do not clash on the shared server.

If you add a **new sentence** in a feature, you must add a matching `@Given`, `@When`, or `@Then` method in a class under `stepdefs` (see existing `PetStepDefs`, `UserStepDefs`, `CommonStepDefs`).

---

## 8. Adding a new scenario (minimal workflow)

1. **Copy** an existing scenario in the closest `.feature` file (or add a new `.feature` under `features/`).
2. Adjust steps to match **existing** step text where possible (fastest path).
3. If you need **new** steps, add methods in the appropriate `*StepDefs` class; inject `TestContext`, `PetApiClient`, etc., via the constructor (same style as existing code).
4. Run **TestRunner** or `mvn clean test` and fix compile or assertion errors.

---

## 9. Postman (manual checks)

1. Import `postman/Petstore-Assignment.postman_collection.json`.
2. Import `postman/Petstore.postman_environment.json`.
3. Select environment **Petstore** so `{{url}}` works.
4. Run requests and use the **Tests** scripts included in the collection.

Useful when debugging failures or demonstrating assignment deliverables separate from Cucumber.

---

## 10. Troubleshooting

| Symptom | What to try |
|---------|-------------|
| `BUILD FAILURE` / red scenarios | Open `target/cucumber-report.html`; read the step error and attached response body. |
| Intermittent failures on Petstore | The public API is shared; the framework **retries** several operations. Re-run `mvn clean test`. If it keeps failing, try another time or point `base.uri` to a stable mock. |
| Step shows **undefined** in Cucumber output | Gherkin text does not match any Java step; align wording or add a new step definition in `stepdefs`. |
| `petId not set in context` | Steps ran out of order, or a prior step failed before storing the id; fix the scenario or earlier step. |
| JSON body ignored by API (wrong pet data) | Bodies are built with `JsonSupport`; ensure your model maps to valid JSON for that API. |

---

## 11. Related documents

- **README.md** — quick start, layout summary, known Petstore limitations.
- **plan/Precision-API-BDD-Implementation-Plan.md** — architecture and phased design notes.
- **TEAM_ROLES.md** — template for assignment team split.

---

## 12. Quick reference commands

```bash
# Resolve dependencies only
mvn -q -DskipTests dependency:resolve

# Clean and run all Cucumber tests
mvn clean test

# Compile tests without running (optional)
mvn -q test-compile
```

If you use **CI**, the important artifact is usually: `mvn clean test` exit code **0** plus archived `target/surefire-reports/` and `target/cucumber-report.html`.
