# ProjectStructure.md

## 1. Base Structure
- Multi-module Gradle project targeting Java 21. Modules: `core` (feature APIs), `admin` (Spring MVC dashboard), `sdk` (client library), `shared` (models/utilities), `sample` (integration example).
- Source lives under `src/main/java`; tests mirror structure within `src/test/java`.
- Admin templates and static assets sit in `admin/src/main/resources/templates` and `admin/src/main/resources/static`.
- Shared configurations (Jackson, exceptions, models) are centralized in `shared`; reuse them rather than duplicating per module.