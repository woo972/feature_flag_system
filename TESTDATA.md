# Test Data for Local Development

This document describes the initial test data that is automatically loaded when running the application with the `local` profile.

## How to Use

Start the application with the local profile:
```bash
./gradlew :core:bootRun --args='--spring.profiles.active=local'
```

The data is automatically loaded from `core/src/main/resources/data.sql` after the database schema is created.

## Test Data

### Admin Users

| Username | Password | Email | Role | Status |
|----------|----------|-------|------|--------|
| admin | admin123 | admin@featureflag.com | SUPER_ADMIN | Enabled |
| testuser | admin123 | testuser@featureflag.com | ADMIN | Enabled |
| disabled_user | admin123 | disabled@featureflag.com | ADMIN | Disabled |

### API Keys

| Name | API Key | Status | Description |
|------|---------|--------|-------------|
| Development Key | test_key_1234567890abcdef... | ACTIVE | API key for local development and testing |
| Production Test Key | test_key_prod_1234567890... | ACTIVE | Simulates production API key (expires in 90 days) |
| Revoked Key | test_key_revoked_1234567... | REVOKED | This key has been revoked for testing |

### Feature Flags

| Name | Description | Status | Targeting Rules |
|------|-------------|--------|-----------------|
| new_dashboard | New dashboard UI with enhanced analytics | ON | user_tier IN [premium, enterprise], region NOT_IN [cn, ru] |
| dark_mode | Dark mode theme support | ON | app_version >= 2.0 |
| beta_features | Access to beta features for testing | OFF | user_group = beta_tester, account_age_days > 30 |
| advanced_analytics | Advanced analytics and reporting | ON | user_tier = enterprise |
| archived_feature | This feature has been archived | OFF | (archived 30 days ago) |

### Pre-defined Targeting Rules

Reusable targeting rule templates:

| Name | Description | Operator | Values |
|------|-------------|----------|--------|
| premium_users | Target premium tier users | IN | [premium, enterprise] |
| us_region | Target users in US region | EQUAL | [us] |
| beta_testers | Target beta testing group | EQUAL | [beta_tester] |
| exclude_free_tier | Exclude free tier users | NOT_EQUAL | [free] |
| high_usage_users | Users with high API usage | GREATER_THAN | [1000] |

## Accessing the H2 Console

The H2 console is available at: http

://localhost:8082/h2-console

Connection settings:
- **JDBC URL**: `jdbc:h2:mem:featureflag`
- **Username**: `sa`
- **Password**: (leave empty)

## Testing with the Data

### Example: Login as admin
```bash
curl -X POST http://localhost:8082/api/v1/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Example: Use API key to access feature flags
```bash
curl -H "X-API-Key: test_key_1234567890abcdef1234567890abcdef1234567890abcdef1234" \
  http://localhost:8082/api/v1/feature-flags
```

## Notes

- The test data is only loaded in the `local` profile
- The database is recreated on each application restart (ddl-auto: create-drop)
- All passwords are BCrypt encoded
- Test data is NOT loaded during unit tests (tests use `spring.sql.init.mode: never`)
