# PermissionPolicy.md

## 1. Purpose
Define which commands and operations AI Agent is allowed to run.

## 2. Allowed Commands
- `./gradlew clean build` – end-to-end build; also triggers frontend placeholders if present.
- `./gradlew :admin:bootRun` – launch the admin UI with the embedded server.
- `./gradlew :shared:test` / `./gradlew :admin:test` / `./gradlew :core:test` / `./gradlew :sdk:test` – scope testing to a single module.
- `./gradlew test` – run the full JVM test suite before pushing changes.
- Read/write inside project workspace only.

## 3. Restricted Operations
- No system-level file modification.
- No network calls unless explicitly allowed.
- No external package installation without approval.

## 4. Escalation Procedure
- New permissions require human review via PR and explicit approval.