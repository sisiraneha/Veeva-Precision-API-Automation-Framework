# Automation-Framework

## Precision API BDD Automation Framework

Hybrid **Cucumber + REST Assured + Maven** API tests against the public [Swagger Petstore](https://petstore.swagger.io/) (`https://petstore.swagger.io/v2`).

**Full usage walkthrough:** see **[USER_GUIDE.md](USER_GUIDE.md)** (run tests, read reports, change config, add scenarios, troubleshooting).

## Prerequisites (install first)

| Requirement | Notes |
|-------------|--------|
| **JDK 17+** | `java -version` should show 17 or newer |
| **Apache Maven 3.8+** | `mvn -version` |
| **Git** (optional) | For version control and assignment deliverables |
| **IntelliJ IDEA** (optional) | Cucumber plugin recommended for `.feature` navigation |

No local Petstore server is required; tests call the hosted API (needs internet).

---

## What to do after installation

1. **Open the project**  
   In IntelliJ: *File → Open* → select the folder that contains this `README.md` and `pom.xml`.

2. **Verify Maven resolves dependencies**  
   IntelliJ usually imports the Maven project automatically. If not: open the Maven tool window and click *Reload All Maven Projects*.  
   Or from a terminal:

   ```bash
   cd "/path/to/Automation frame_wroks"
   mvn -q -DskipTests dependency:resolve
   ```

3. **Run all API/BDD tests**

   ```bash
   mvn clean test
   ```

   - Console: Cucumber **pretty** output and step results.
   - **JUnit/Surefire**: `target/surefire-reports/`
   - **Cucumber HTML**: `target/cucumber-report.html`
   - **Cucumber JSON**: `target/cucumber-report.json`

4. **Open the HTML report**  
   After a successful run, open `target/cucumber-report.html` in a browser.

5. **Change base URL (optional)**  
   Edit `src/test/resources/config.properties` → `base.uri` if you point tests at another environment (e.g. mock server).

6. **Postman (assignment deliverable)**  
   - Import `postman/Petstore-Assignment.postman_collection.json`  
   - Import `postman/Petstore.postman_environment.json`  
   - Select the **Petstore** environment so `{{url}}` resolves.

7. **Team / documentation**  
   Fill in `TEAM_ROLES.md` and add an architecture diagram under `docs/` if required by your course (you can export diagrams from the plan in `plan/Precision-API-BDD-Implementation-Plan.md`).

---

## Project layout

| Path | Purpose |
|------|---------|
| `src/test/resources/features/` | Gherkin feature files |
| `src/test/java/com/precision/bdd/stepdefs/` | Step definitions |
| `src/test/java/com/precision/bdd/api/` | REST Assured clients (Pet / Store / User) |
| `src/test/java/com/precision/bdd/core/` | Base request factory + JSON helpers |
| `src/test/java/com/precision/bdd/context/` | Scenario-scoped state (e.g. `petId`) |
| `src/test/java/com/precision/bdd/runner/TestRunner.java` | JUnit Platform suite entry point |
| `postman/` | Collection + environment for manual/API exploration |

---

## Tags

- `@pet` — lifecycle CRUD feature  
- `@store` — inventory vs `findByStatus`  
- `@user` — negative user flows  
- `@cross` — cross-endpoint consistency  

Run a subset from the IDE using Cucumber tags, or add a temporary `cucumber.filter.tags=@pet` entry in `src/test/resources/junit-platform.properties` (remember to remove it when you want the full suite).

---

## Known limitations (public Petstore)

- The demo API is **shared** and can be **slow or briefly inconsistent**; tests use **retries** for GET-after-create, DELETE-after-remove, inventory vs list, and `findByStatus=sold`.
- **DELETE /pet/{id}** sometimes returns **404** with an empty body even when the pet was just updated; the lifecycle scenario treats **200** as success, or **404** only if **GET** confirms the pet is gone (see step `the delete operation completes successfully`).
- **User login** often returns HTTP 200 with a `message` even for bogus credentials; the scenario documents that a response is returned. Tighten assertions when targeting a real authenticated API.

---

## Assignment PDF

Source specification: `Source/Precision API BDD Automation Framework.pdf`  
Implementation plan: `plan/Precision-API-BDD-Implementation-Plan.md`
