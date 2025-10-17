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

## To do
1. Local cache for core module in local profile
2. Real DB and Redis connection for core module
3. Replace frontend with React
4. Add Integration tests
6. Add SonarQube to check code quality
7. Add GitHub Actions for CI/CD
9. Add Linter or add convention.md for the agent
8. Add Docker for containerization
9. Shared module refactoring (Util, Exception, Constants)
10. Shared model, dto refactoring (FeatureFlag, TargetingRule, RegionRule, xxxRequest, xxxResponse, etc)
11. <high> Does TargetingRule should be separated entity?
12. <high> TargetingRule Support when registration
13. <high> Input validation
14. <high> Admin module has to invoke core module by api not direct call
15. Provide custom annotation for featureflag client without spring dependencies
16. <mid> Host should be injected from outside
17. Support pre-defined targeting rule
18. <mid> Authz, Authn for admin module

## License

MIT License
