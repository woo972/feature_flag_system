# Security Configuration

## Overview

The Feature Flag System uses Spring Security with API key-based authentication to secure communication between the SDK clients and the Core module.

## Architecture

### Authentication Mechanism

The system implements API key authentication using the `X-API-Key` header. SDK clients must include a valid API key in all requests to protected endpoints.

### Components

1. **ApiKeyEntity** (`core/entity/ApiKeyEntity.java`)
   - Stores API key information in the database
   - Fields: id, apiKey, name, description, active, createdAt, lastUsedAt, expiresAt

2. **ApiKeyAuthenticationFilter** (`core/security/ApiKeyAuthenticationFilter.java`)
   - Intercepts incoming requests
   - Validates API keys from the `X-API-Key` header
   - Updates last used timestamp
   - Checks for expiration

3. **SecurityConfig** (`core/config/SecurityConfig.java`)
   - Configures Spring Security
   - Defines endpoint authorization rules
   - Disables CSRF for stateless API
   - Configures session management

4. **ApiKeyService** (`core/service/ApiKeyService.java`)
   - Manages API key lifecycle (create, revoke, activate, delete)
   - Generates secure random API keys using SecureRandom

## API Endpoints

### Protected Endpoints (Require API Key)

- `GET /api/v1/feature-flags` - List all feature flags
- `GET /api/v1/feature-flags/{flag-id}` - Get specific feature flag
- `GET /api/v1/feature-flags/evaluate/**` - Evaluate feature flags
- `GET /api/v1/feature-flags/event-stream` - SSE event stream

### Management Endpoints (Currently Public - Should be protected by Admin Auth)

- `POST /api/v1/feature-flags` - Register new feature flag
- `POST /api/v1/feature-flags/**` - Feature flag operations
- `GET /api/v1/feature-flags/page` - Paginated list
- `GET /api/v1/feature-flags/cache/refresh` - Refresh cache

### API Key Management Endpoints

- `POST /api/v1/api-keys` - Create new API key
- `GET /api/v1/api-keys` - List all API keys
- `GET /api/v1/api-keys/{id}` - Get specific API key
- `POST /api/v1/api-keys/{id}/revoke` - Revoke API key
- `POST /api/v1/api-keys/{id}/activate` - Activate API key
- `DELETE /api/v1/api-keys/{id}` - Delete API key

## Database Schema

The `api_keys` table stores API key information:

```sql
CREATE TABLE api_keys (
    id BIGSERIAL PRIMARY KEY,
    api_key VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    expires_at TIMESTAMP
);
```

Run the migration script: `core/src/main/resources/db/migration/V001__create_api_keys_table.sql`

## Usage

### Creating an API Key

```bash
curl -X POST http://localhost:8082/api/v1/api-keys \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My SDK Client",
    "description": "API key for production SDK",
    "expiresAt": null
  }'
```

Response includes the full API key (only shown once):
```json
{
  "id": 1,
  "apiKey": "abc123xyz...",
  "name": "My SDK Client",
  "description": "API key for production SDK",
  "active": true,
  "createdAt": "2025-10-26T10:00:00",
  "lastUsedAt": null,
  "expiresAt": null
}
```

### Using the API Key in SDK

Set the API key in your SDK configuration:

```java
// Static configuration
FeatureFlagProperty.API_KEY = "your-api-key-here";

// Create client
FeatureFlagClient client = DefaultFeatureFlagClient.builder().build();
client.initialize();
```

Or when creating the CoreFeatureFlagClient directly:

```java
CoreFeatureFlagClient coreClient = new CoreFeatureFlagClient("your-api-key-here");
```

### Making Authenticated Requests

The SDK automatically includes the API key in the `X-API-Key` header for all requests:

```bash
curl -X GET http://localhost:8082/api/v1/feature-flags \
  -H "X-API-Key: your-api-key-here"
```

### Managing API Keys

**List all API keys:**
```bash
curl -X GET http://localhost:8082/api/v1/api-keys
```

**Revoke an API key:**
```bash
curl -X POST http://localhost:8082/api/v1/api-keys/1/revoke
```

**Activate an API key:**
```bash
curl -X POST http://localhost:8082/api/v1/api-keys/1/activate
```

**Delete an API key:**
```bash
curl -X DELETE http://localhost:8082/api/v1/api-keys/1
```

## Security Best Practices

1. **Secure Storage**: Store API keys securely using environment variables or secret management systems
2. **Rotation**: Regularly rotate API keys, especially after key personnel changes
3. **Expiration**: Set expiration dates for temporary or testing API keys
4. **Monitoring**: Monitor the `last_used_at` field to identify unused keys
5. **Least Privilege**: Create separate API keys for different environments/purposes
6. **Revocation**: Immediately revoke compromised keys
7. **HTTPS**: Always use HTTPS in production to protect API keys in transit

## Future Enhancements

1. **Admin Authentication**: Add separate authentication for admin endpoints (JWT, OAuth2, etc.)
2. **Rate Limiting**: Implement rate limiting per API key
3. **Audit Logging**: Log all API key usage for security auditing
4. **Key Scopes**: Add permission scopes to limit what each API key can access
5. **Multi-tenancy**: Support organization-based API key isolation
6. **API Key Rotation**: Implement automatic key rotation policies

## Troubleshooting

### 401 Unauthorized Error

- Check that the `X-API-Key` header is included in the request
- Verify the API key is active: `GET /api/v1/api-keys/{id}`
- Check if the API key has expired
- Ensure the API key value is correct (no extra spaces or characters)

### API Key Not Working After Creation

- Make sure the API key was copied correctly from the creation response
- The full API key is only shown once during creation
- Verify the key is active in the database

### Last Used Timestamp Not Updating

- Check database connectivity
- Review application logs for errors
- The update is non-blocking and failures are logged but don't affect authentication
