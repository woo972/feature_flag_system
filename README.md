# Feature Flag System

## Project Structure

- `core`: Core APIs and business logic for feature flag management
- `admin`: Admin dashboard for managing feature flags
- `sdk`: Client SDK for integrating feature flags into applications
- `shared`: Shared utilities and common code

## Requirements

- Java 21
- Gradle 8.5

## Building the Project

```bash
./gradlew clean build
```

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
2. Build the project: `./gradlew build`
3. Run tests: `./gradlew test`

## License

MIT License
