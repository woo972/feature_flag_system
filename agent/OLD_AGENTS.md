# Repository Guidelines

## Principle

### 1. Summary
“Keep the loop fast. Avoid blocking. Prefer explicit over interactive.”
Codex should always optimize for determinism, log-based introspection, and minimal coupling to UI.

### 2. Purpose
Codex acts as an autonomous coding assistant that executes code, modifies files, and runs tests **without human confirmation**.  
Its goal is to maintain a fast, reliable, and reproducible development loop.

### 3. Execution Rules
- **Non-interactive:** All actions must run without human prompts.
- **Deterministic:** Commands must behave the same way on repeated runs.
- **Fail-fast:** Crashes are acceptable; hanging processes are not.
- **Timeout:** Default 60 seconds per command.
- **Scope:** Operate only inside the project workspace directory. No system-level or network changes unless explicitly authorized.

### 4. Tool Policy
- Use existing CLI tools and scripts only.
- Wrap all recurring actions in a `Makefile` or simple shell scripts (e.g., `scripts/dev.sh`).
- Avoid hidden automation or external UI tools.
- Do not rely on IDE integrations, GUI, or browser interaction.

**Optional integrations:**
- MCP (Model Context Protocol) servers may be connected only for specific tasks (e.g., browser testing via `playwright-mcp`).
- Default behavior is **no MCP connection**.

## Project Specific Guide

### Project Structure & Module Organization
- Multi-module Gradle project targeting Java 21. Modules: `core` (feature APIs), `admin` (Spring MVC dashboard), `sdk` (client library), `shared` (models/utilities), `sample` (integration example).
- Source lives under `src/main/java`; tests mirror structure within `src/test/java`.
- Admin templates and static assets sit in `admin/src/main/resources/templates` and `admin/src/main/resources/static`.
- Shared configurations (Jackson, exceptions, models) are centralized in `shared`; reuse them rather than duplicating per module.

### Build, Test, and Development Commands
- `./gradlew clean build` – end-to-end build; also triggers frontend placeholders if present.
- `./gradlew :admin:bootRun` – launch the admin UI with the embedded server.
- `./gradlew :shared:test` / `./gradlew :admin:test` / `./gradlew :core:test` / `./gradlew :sdk:test` – scope testing to a single module.
- `./gradlew test` – run the full JVM test suite before pushing changes.

### Coding Style & Naming Conventions
- Prefer 4-space indentation and `com.featureflag.<module>` package roots. Classes and enums use PascalCase; methods, fields, and variables stay lowerCamelCase.
- Take advantage of Lombok builders already defined in `shared` models (e.g., `FeatureFlag.builder()`); keep constructors minimal.
- Keep controllers thin by delegating to services; surface-only data classes belong in `shared`.
- Avoid wildcard imports and ensure IntelliJ/Gradle formatting is applied before committing.

### Testing Guidelines
- Tests are JUnit 5 + Mockito. Name files `<Subject>Test` and use `@DisplayName` to describe behavior.
- Slice tests (e.g., `@WebMvcTest`) should mock dependent services and assert views/models, as shown in `AdminControllerTest`.
- External dependencies (DB, Redis, SSE) are not provisioned; rely on stubs or local profiles.
- Add regression coverage for new business rules; note that Jacoco integration is pending but expected.

### Configuration Tips
- Default YAML files live under each module’s `src/main/resources`; override via `application-local.yml` when wiring databases or Redis.
- Do not check in secrets. Use environment variables or ignored `.env` files for local credentials.

## Tool Use Guide

### Git & Github
#### Commit & Pull Request Guidelines
- Follow the existing concise, imperative commit style (`Remove unnecessary import statements`, `Add stream provider`). Mention the affected module when helpful.
- Each PR should outline scope, testing commands run, configuration changes, and include screenshots for UI shifts.
- Ensure the Gradle commands above succeed locally; call out any intentionally skipped modules (e.g., frontend scaffolding).