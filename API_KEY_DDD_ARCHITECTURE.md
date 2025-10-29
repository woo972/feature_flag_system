# API Key Module - DDD Architecture

## Overview

The API Key module has been refactored to follow Domain-Driven Design (DDD) principles, separating concerns into distinct layers and making the domain model rich with business logic.

## Architecture Layers

### 1. Domain Layer (`apikey/domain`)

The core business logic layer, independent of infrastructure concerns.

#### **Domain Models**

**`ApiKey`** - Rich domain entity (`domain/model/ApiKey.java`)
- Encapsulates all API key business logic and invariants
- Contains behavior for:
  - Validation (`isValid()`, `isExpired()`, `isActive()`)
  - State transitions (`revoke()`, `activate()`)
  - Usage tracking (`recordUsage()`)
- Ensures business rules are always enforced
- Example:
  ```java
  ApiKey apiKey = ApiKey.create("Production SDK", "Main production API key", null);
  if (apiKey.isValid()) {
      apiKey.recordUsage();
  }
  ```

**Value Objects:**
- `ApiKeyValue` - Represents the actual API key string
  - Immutable
  - Generates secure random keys
  - Provides masking for security
  - Validates format

- `ApiKeyId` - Represents the API key identifier
  - Type-safe wrapper around Long
  - Validates positive values

- `ApiKeyStatus` - Enum for API key status
  - `ACTIVE` - Key is valid for use
  - `REVOKED` - Key has been revoked

#### **Repository Interface**

**`ApiKeyRepository`** (`domain/repository/ApiKeyRepository.java`)
- Defines the contract for persistence operations
- Domain layer interface, implemented in infrastructure layer
- Operations:
  - `save(ApiKey)` - Create or update
  - `findById(ApiKeyId)` - Find by ID
  - `findByValue(ApiKeyValue)` - Find by key value
  - `findAll()` - Get all keys
  - `delete(ApiKeyId)` - Delete key
  - `existsById(ApiKeyId)` - Check existence by ID
  - `existsByValue(ApiKeyValue)` - Check existence by value

### 2. Application Layer (`apikey/application`)

Orchestrates use cases and coordinates domain objects.

#### **Application Service**

**`ApiKeyApplicationService`** (`application/service/ApiKeyApplicationService.java`)
- Orchestrates business workflows
- Transaction boundaries
- Delegates to domain model for business logic
- Use cases:
  - `createApiKey()` - Create new API key
  - `getAllApiKeys()` - List all API keys
  - `getApiKeyById()` - Get specific API key
  - `revokeApiKey()` - Revoke an API key
  - `activateApiKey()` - Activate a revoked key
  - `deleteApiKey()` - Delete an API key
  - `validateAndRecordUsage()` - Validate and track usage

#### **DTOs**

**`CreateApiKeyCommand`** (`application/dto/CreateApiKeyCommand.java`)
- Command object for creating API keys
- Input validation annotations
- Fields: name, description, expiresAt

**`ApiKeyDto`** (`application/dto/ApiKeyDto.java`)
- Data transfer object for API key data
- Converts from domain model
- Supports masked or full key display
- Includes computed fields (isActive, isExpired)

### 3. Infrastructure Layer (`apikey/infrastructure`)

Implements technical concerns and adapters.

#### **Persistence**

**`ApiKeyJpaEntity`** (`infrastructure/persistence/ApiKeyJpaEntity.java`)
- JPA entity for database mapping
- Maps to `api_keys` table
- Conversion methods:
  - `toDomain()` - Converts to domain model
  - `fromDomain()` - Creates from domain model
  - `updateFromDomain()` - Updates from domain model

**`ApiKeyJpaRepository`** (`infrastructure/persistence/ApiKeyJpaRepository.java`)
- Spring Data JPA repository
- Provides database operations

**`ApiKeyRepositoryImpl`** (`infrastructure/persistence/ApiKeyRepositoryImpl.java`)
- Adapter implementing domain repository interface
- Translates between domain and JPA entities
- Bridges domain and infrastructure layers

#### **Security**

**`ApiKeyAuthenticationFilter`** (`infrastructure/security/ApiKeyAuthenticationFilter.java`)
- Spring Security filter
- Validates `X-API-Key` header
- Delegates validation to application service
- Records usage through domain model

**`ApiKeyAuthenticationToken`** (`infrastructure/security/ApiKeyAuthenticationToken.java`)
- Spring Security authentication token
- Represents authenticated SDK client

### 4. Presentation Layer (`apikey/presentation`)

Handles HTTP requests and responses.

#### **Controller**

**`ApiKeyController`** (`presentation/controller/ApiKeyController.java`)
- REST endpoints for API key management
- Thin layer delegating to application service
- Endpoints:
  - `POST /api/v1/api-keys` - Create API key
  - `GET /api/v1/api-keys` - List all API keys
  - `GET /api/v1/api-keys/{id}` - Get API key
  - `POST /api/v1/api-keys/{id}/revoke` - Revoke key
  - `POST /api/v1/api-keys/{id}/activate` - Activate key
  - `DELETE /api/v1/api-keys/{id}` - Delete key

## Directory Structure

```
core/src/main/java/com/featureflag/core/apikey/
├── domain/
│   ├── model/
│   │   ├── ApiKey.java                    # Rich domain entity
│   │   ├── ApiKeyId.java                  # Value object
│   │   ├── ApiKeyValue.java               # Value object
│   │   └── ApiKeyStatus.java              # Enum
│   └── repository/
│       └── ApiKeyRepository.java          # Repository interface
├── application/
│   ├── service/
│   │   └── ApiKeyApplicationService.java  # Use case orchestration
│   └── dto/
│       ├── CreateApiKeyCommand.java       # Input DTO
│       └── ApiKeyDto.java                 # Output DTO
├── infrastructure/
│   ├── persistence/
│   │   ├── ApiKeyJpaEntity.java          # JPA entity
│   │   ├── ApiKeyJpaRepository.java      # Spring Data repository
│   │   └── ApiKeyRepositoryImpl.java     # Repository adapter
│   └── security/
│       ├── ApiKeyAuthenticationFilter.java    # Security filter
│       └── ApiKeyAuthenticationToken.java     # Auth token
└── presentation/
    └── controller/
        └── ApiKeyController.java          # REST controller
```

## Key DDD Concepts Applied

### 1. **Rich Domain Model**
- Business logic lives in domain entities (`ApiKey`)
- Entities enforce invariants and business rules
- Example: Can't revoke an already revoked key

### 2. **Value Objects**
- Immutable objects defined by their attributes
- `ApiKeyValue`, `ApiKeyId` provide type safety
- Prevent primitive obsession

### 3. **Ubiquitous Language**
- Methods named after business operations: `revoke()`, `activate()`, `recordUsage()`
- Terms from domain: "API Key", "Status", "Expiration"

### 4. **Layered Architecture**
- Clear separation of concerns
- Dependencies point inward (toward domain)
- Domain layer has no external dependencies

### 5. **Repository Pattern**
- Abstract data access through domain interface
- Implementation details hidden in infrastructure
- Easy to test and swap implementations

### 6. **Application Services**
- Coordinate domain objects
- Define transaction boundaries
- Thin orchestration layer

### 7. **Aggregate**
- `ApiKey` is the aggregate root
- All operations go through the aggregate root
- Ensures consistency

## Benefits of DDD Approach

### 1. **Testability**
- Domain logic can be tested without infrastructure
- Mock repositories for unit tests
- Clear boundaries make testing easier

### 2. **Maintainability**
- Business logic centralized in domain
- Changes to business rules only affect domain layer
- Clear structure makes code easier to understand

### 3. **Flexibility**
- Easy to change infrastructure (e.g., switch from PostgreSQL to MongoDB)
- Can add new use cases without modifying domain
- Domain model reusable across different contexts

### 4. **Type Safety**
- Value objects prevent primitive type confusion
- Compiler enforces correct types
- Fewer runtime errors

### 5. **Business Focus**
- Code reflects business concepts
- Easier for domain experts to understand
- Clear separation from technical concerns

## Comparison: Before vs After

### Before (Anemic Domain Model)
```java
// Service contains all logic
public class ApiKeyService {
    public ApiKeyEntity createApiKey(String name) {
        String key = generateKey();
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setApiKey(key);
        entity.setName(name);
        entity.setActive(true);
        return repository.save(entity);
    }

    public void revokeApiKey(Long id) {
        ApiKeyEntity entity = repository.findById(id);
        entity.setActive(false);  // Just setting a field
        repository.save(entity);
    }
}
```

### After (Rich Domain Model)
```java
// Domain model contains logic
public class ApiKey {
    public void revoke() {
        if (status == ApiKeyStatus.REVOKED) {
            throw new IllegalStateException("API key is already revoked");
        }
        this.status = ApiKeyStatus.REVOKED;
    }
}

// Service orchestrates
public class ApiKeyApplicationService {
    public void revokeApiKey(Long id) {
        ApiKey apiKey = repository.findById(id);
        apiKey.revoke();  // Business logic in domain
        repository.save(apiKey);
    }
}
```

## Usage Examples

### Creating an API Key
```java
// Through REST API
POST /api/v1/api-keys
{
    "name": "Production SDK",
    "description": "Main production API key",
    "expiresAt": null
}

// In code
CreateApiKeyCommand command = new CreateApiKeyCommand("Production SDK", "Main production API key", null);
ApiKeyDto dto = apiKeyApplicationService.createApiKey(command);
```

### Using Domain Model Directly
```java
// Create
ApiKey apiKey = ApiKey.create("Test Key", "For testing", null);

// Validate
if (apiKey.isValid()) {
    apiKey.recordUsage();
}

// State transition
apiKey.revoke();
// Throws exception if already revoked

// Query
boolean active = apiKey.isActive();
boolean expired = apiKey.isExpired();
```

### Authentication Flow
```
1. HTTP Request with X-API-Key header
2. ApiKeyAuthenticationFilter intercepts
3. Delegates to ApiKeyApplicationService.findByValue()
4. Gets ApiKey domain object
5. Calls apiKey.isValid() (domain logic)
6. If valid, creates ApiKeyAuthenticationToken
7. Records usage through apiKey.recordUsage()
8. Saves through repository
```

## Testing Strategy

### Unit Tests (Domain Layer)
```java
@Test
void shouldRevokeActiveApiKey() {
    ApiKey apiKey = ApiKey.create("Test", "Test key", null);
    apiKey.revoke();
    assertFalse(apiKey.isActive());
}

@Test
void shouldThrowWhenRevokingRevokedKey() {
    ApiKey apiKey = ApiKey.create("Test", "Test key", null);
    apiKey.revoke();
    assertThrows(IllegalStateException.class, () -> apiKey.revoke());
}
```

### Integration Tests (Application Layer)
```java
@Test
void shouldCreateAndPersistApiKey() {
    CreateApiKeyCommand command = new CreateApiKeyCommand("Test", "Test key", null);
    ApiKeyDto dto = service.createApiKey(command);

    assertNotNull(dto.getId());
    assertNotNull(dto.getApiKey());
}
```

## Best Practices

1. **Keep Domain Pure** - No framework dependencies in domain layer
2. **Validate at Boundaries** - Use validation annotations in DTOs/commands
3. **Fail Fast** - Validate in constructors and throw exceptions
4. **Use Value Objects** - Avoid primitive obsession
5. **Immutability** - Value objects should be immutable
6. **Single Responsibility** - Each layer has one reason to change
7. **Dependency Direction** - Always point toward domain

## Future Enhancements

1. **Domain Events** - Add events for key lifecycle (created, revoked, etc.)
2. **Specifications** - Use specification pattern for complex queries
3. **Domain Services** - Extract cross-aggregate logic if needed
4. **Factories** - Complex object creation logic
5. **Audit Log** - Track all changes to API keys
6. **Rate Limiting** - Per-key rate limits using domain rules
