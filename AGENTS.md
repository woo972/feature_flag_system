# Repository Guidelines

## Project Structure & Module Organization
- Multi-module Gradle project targeting Java 21. Modules: `core` (feature APIs), `admin` (Spring MVC dashboard), `sdk` (client library), `shared` (models/utilities), `sample` (integration example).
- Source lives under `src/main/java`; tests mirror structure within `src/test/java`.
- Admin templates and static assets sit in `admin/src/main/resources/templates` and `admin/src/main/resources/static`.
- Shared configurations (Jackson, exceptions, models) are centralized in `shared`; reuse them rather than duplicating per module.

## Build, Test, and Development Commands
- `./gradlew clean build` – end-to-end build; also triggers frontend placeholders if present.
- `./gradlew :admin:bootRun` – launch the admin UI with the embedded server.
- `./gradlew :core:test` / `./gradlew :sdk:test` – scope testing to a single module.
- `./gradlew test` – run the full JVM test suite before pushing changes.
- `./gradlew buildFrontend` – optional React build hook; skip if the `frontend` module is absent locally.

## Coding Style & Naming Conventions
- Prefer 4-space indentation and `com.featureflag.<module>` package roots. Classes and enums use PascalCase; methods, fields, and variables stay lowerCamelCase.
- Take advantage of Lombok builders already defined in `shared` models (e.g., `FeatureFlag.builder()`); keep constructors minimal.
- Keep controllers thin by delegating to services; surface-only data classes belong in `shared`.
- Avoid wildcard imports and ensure IntelliJ/Gradle formatting is applied before committing.

## Testing Guidelines
- Tests are JUnit 5 + Mockito. Name files `<Subject>Test` and use `@DisplayName` to describe behavior.
- Slice tests (e.g., `@WebMvcTest`) should mock dependent services and assert views/models, as shown in `AdminControllerTest`.
- External dependencies (DB, Redis, SSE) are not provisioned; rely on stubs or local profiles.
- Add regression coverage for new business rules; note that Jacoco integration is pending but expected.

## Commit & Pull Request Guidelines
- Follow the existing concise, imperative commit style (`Remove unnecessary import statements`, `Add stream provider`). Mention the affected module when helpful.
- Each PR should outline scope, testing commands run, configuration changes, and include screenshots for UI shifts.
- Ensure the Gradle commands above succeed locally; call out any intentionally skipped modules (e.g., frontend scaffolding).

## Configuration Tips
- Default YAML files live under each module’s `src/main/resources`; override via `application-local.yml` when wiring databases or Redis.
- Do not check in secrets. Use environment variables or ignored `.env` files for local credentials.
