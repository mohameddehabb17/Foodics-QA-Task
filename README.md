# Foodics QA Automation Task

Unified UI + API automation framework using Selenium Java, RestAssured, TestNG, Maven, and Allure, with separated API and UI architecture.

## Tech Stack

- Java 11
- Maven
- Selenium WebDriver
- WebDriverManager
- RestAssured
- TestNG
- Allure Report

## Project Structure

- `src/main/java/com/foodics/qa/shared/config` -> shared config and property/env loading
- `src/main/java/com/foodics/qa/api/client` -> API client/services
- `src/main/java/com/foodics/qa/api/models` -> API models
- `src/main/java/com/foodics/qa/ui/driver` -> UI WebDriver factory
- `src/main/java/com/foodics/qa/ui/pages` -> UI Selenium Page Objects
- `src/main/java/com/foodics/qa/ui/models` -> UI models
- `src/main/java/com/foodics/qa/ui/utils` -> UI utility classes
- `src/test/java/com/foodics/qa/api/tests` -> API tests
- `src/test/java/com/foodics/qa/api/tests/base` -> API test base infrastructure
- `src/test/java/com/foodics/qa/api/tests/utils` -> API test utilities (`TestLogUtils`)
- `src/test/java/com/foodics/qa/ui/tests` -> UI tests
- `src/test/java/com/foodics/qa/ui/tests/base` -> UI test base infrastructure
- `src/test/java/com/foodics/qa/ui/tests/utils` -> UI test utilities (`AllureUtils`)
- `src/test/resources/config.properties` -> non-sensitive defaults

## Prerequisites

- JDK 11+
- Maven 3.8+
- Chrome and Firefox browsers

## Required Environment Variables

### UI

**You must set the following environment variables before running UI tests that require login:**

- `AMAZON_EMAIL` (your Amazon account email for login)
- `AMAZON_PASSWORD` (your Amazon account password for login)

#### Example (Windows PowerShell)

```powershell
$env:AMAZON_EMAIL="your-email@example.com"
$env:AMAZON_PASSWORD="your-password"
mvn test "-Dsurefire.suiteXmlFiles=testng-ui.xml"
```

#### Example (Linux/macOS Bash)

```bash
export AMAZON_EMAIL="your-email@example.com"
export AMAZON_PASSWORD="your-password"
mvn test "-Dsurefire.suiteXmlFiles=testng-ui.xml"
```

If these are not set, UI tests that require login will be skipped.

### Optional

- `BROWSER` (overrides config browser)
- `UI_BASE_URL` (default: <https://www.amazon.eg/>)
- `API_BASE_URL` (default: <https://reqres.in>)
- `REQRES_API_KEY` (used as `x-api-key` header for reqres requests)
- `HEADLESS` (`true` or `false`)
- `TIMEOUT_SECONDS`
- `PAGE_LOAD_TIMEOUT_SECONDS` (page load timeout for WebDriver, default: 120)
- `UI_STEP_DELAY_MS` (optional slow-down between UI steps, default: 0)

## Utility Usage Rules

- Use `BasePage` helpers (`click`, `type`, `text`, `textContent`, etc.) instead of raw Selenium interactions where possible.
- Use `WaitUtils` for waits (including custom timeout overloads) instead of creating `WebDriverWait` directly in page classes.

## Installation

```bash
mvn clean install -DskipTests
```

## Run Tests

### API only

```bash
mvn test "-Dsurefire.suiteXmlFiles=testng-api.xml"
```

### UI only

```bash
mvn test "-Dsurefire.suiteXmlFiles=testng-ui.xml"
```

UI runs one browser per execution based on selected configuration (`browser` in `config.properties`).

### Full suite

```bash
mvn test "-Dsurefire.suiteXmlFiles=testng-all.xml"
```

## Allure Report

```bash
mvn allure:serve
```

## Notes

- The UI test validates the Amazon flow up to checkout verification and does not place an order.
- For reqres, `GET /api/users/{id}` validates against available seeded users (e.g., user id 2).
- Credentials are not stored in source code and must be injected using environment variables.
