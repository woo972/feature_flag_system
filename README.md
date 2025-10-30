# Feature Flag System

## Project Structure
### modules
- `core`: Core APIs and business logic for feature flag management
- `admin`: Admin dashboard for managing feature flags
- `sdk`: Client SDK for integrating feature flags into applications
- `shared`: Shared utilities and common code
### documents
- `README.md`: Provide guiance of this project
- `AGENTS.md`: Provide guidance for AI agent 

## Requirements

- Java 21
- Gradle 8.5

## Module Details

### Core Module
Contains the core business logic and APIs for feature flag management.
Needs Database and Redis.

### Admin Module
Administrative interface for managing feature flags, built with Spring Boot and Thymeleaf.
Frontend will be built with other frameworks/libraries like React.

### SDK Module
Client library for applications to integrate with the feature flag system.

### Shared Module
Common utilities and models shared across modules.

## Development

1. Clone the repository
2. Build the project: `./gradlew clean build`
3. Run tests: `./gradlew test`

### Code Quality Tools

This project uses three linting tools to ensure code quality:

#### Checkstyle
Checks for coding standards and style violations.
```bash
# Run Checkstyle on all modules
./gradlew checkstyleMain checkstyleTest

# Run on specific module
./gradlew :core:checkstyleMain
```
Reports are generated at: `build/reports/checkstyle/`

#### PMD
Detects code quality issues and potential bugs.
```bash
# Run PMD on all modules
./gradlew pmdMain pmdTest

# Run on specific module
./gradlew :core:pmdMain
```
Reports are generated at: `build/reports/pmd/`

#### SpotBugs
Finds bugs through static analysis.
```bash
# Run SpotBugs on all modules
./gradlew spotbugsMain spotbugsTest

# Run on specific module
./gradlew :core:spotbugsMain
```
Reports are generated at: `build/reports/spotbugs/`

#### Run All Linters
```bash
# Run all linters at once
./gradlew check
```

Configuration files:
- Checkstyle: `config/checkstyle/checkstyle.xml`
- PMD: `config/pmd/ruleset.xml`
- SpotBugs: Configured in `build.gradle.kts`

### Security

The Core module uses Spring Security with API key authentication for SDK clients. The implementation follows **Domain-Driven Design (DDD)** principles with clear separation of concerns.

#### Architecture
The API key module is structured in DDD layers:
- **Domain Layer**: Rich domain model with business logic (ApiKey, ApiKeyValue, ApiKeyStatus)
- **Application Layer**: Use case orchestration (ApiKeyApplicationService)
- **Infrastructure Layer**: JPA entities, repositories, security filters
- **Presentation Layer**: REST controllers

#### API Key Authentication
SDK clients must include an `X-API-Key` header in all requests to protected endpoints.

**Create an API key:**
```bash
curl -X POST http://localhost:8082/api/v1/api-keys \
  -H "Content-Type: application/json" \
  -d '{"name": "My SDK Client", "description": "Production SDK"}'
```

**Use the API key in SDK:**
```java
FeatureFlagProperty.API_KEY = "your-api-key-here";
FeatureFlagClient client = DefaultFeatureFlagClient.builder().build();
client.initialize();
```

#### Admin Authentication (JWT)
Admin users authenticate via JWT tokens with role-based access control.

**Login:**
```bash
curl -X POST http://localhost:8082/api/v1/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Default Admin User:**
- Username: `admin`
- Password: `admin123` ⚠️ Change immediately!
- Role: `SUPER_ADMIN`

For detailed security documentation, see [SECURITY.md](SECURITY.md)

## License

MIT License
