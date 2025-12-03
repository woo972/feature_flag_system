# Local Development Guide

## Running with H2 Database

The application is now configured to use H2 in-memory database for local development.

### Configuration Changes Made

**Fixed Issue**: Removed `app.database.*` property references from base `application.yml` that were causing errors when running with local profile.

**Files Modified**:
1. `core/src/main/resources/application.yml` - Simplified to minimal configuration
2. `core/src/main/resources/application-prod.yml` - Contains PostgreSQL configuration
3. `core/src/main/resources/application-local.yml` - Contains H2 configuration (unchanged)

### Starting the Application Locally

#### Option 1: Using Gradle
```bash
cd core
./gradlew bootRun --args='--spring.profiles.active=local'
```

#### Option 2: Using IDE
Set the active profile to `local`:
- **IntelliJ IDEA**: Run Configuration → Active Profiles → `local`
- **Eclipse**: Run Configurations → Arguments → VM Arguments → `-Dspring.profiles.active=local`

#### Option 3: Environment Variable
```bash
export SPRING_PROFILES_ACTIVE=local
cd core
./gradlew bootRun
```

### H2 Database Access

Once the application starts:

**H2 Console**:
- URL: http://localhost:8082/h2-console
- JDBC URL: `jdbc:h2:mem:featureflag`
- Username: `sa`
- Password: (leave empty)

**Database Configuration**:
- Mode: PostgreSQL compatibility mode
- Schema: Auto-created on startup
- Persistence: In-memory (data lost on restart)
- DDL: `create-drop` (fresh schema on each start)

### Application Configuration by Profile

#### Local Profile (`application-local.yml`)
```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:featureflag;MODE=PostgreSQL
    username: sa
    password: (empty)
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

**Features**:
- H2 in-memory database
- SQL logging enabled
- H2 console enabled
- Redis auto-configuration disabled
- Fresh schema on each start

#### Production Profile (`application-prod.yml`)
```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DATABASE_HOST}:${DATABASE_PORT}/${DATABASE_NAME}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
```

**Features**:
- PostgreSQL database
- Environment-driven configuration
- Redis enabled
- Schema updates only

### Switching Profiles

#### Development (H2)
```bash
# Default - automatically uses local profile
./gradlew bootRun

# Explicit
./gradlew bootRun --args='--spring.profiles.active=local'
```

#### Production (PostgreSQL)
```bash
# Set environment variables first
export DATABASE_HOST=localhost
export DATABASE_PORT=5432
export DATABASE_NAME=featureflag
export DATABASE_USERNAME=featureflag_user
export DATABASE_PASSWORD=featureflag_pass

# Run with prod profile
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Verifying Configuration

After starting the application, you should see:

```
✅ Spring profile: local
✅ H2 console available at '/h2-console'
✅ Database available at 'jdbc:h2:mem:featureflag'
✅ Tables created: admin_users, api_keys, feature_flags, pre_defined_targeting_rule, targeting_rule
✅ Tomcat started on port 8082
```

### Troubleshooting

#### Port Already in Use
```bash
# Find and kill process on port 8082
lsof -ti:8082 | xargs kill -9

# Or change port
./gradlew bootRun --args='--spring.profiles.active=local --server.port=8083'
```

#### Profile Not Active
Check logs for: `The following 1 profile is active: "local"`

If missing:
```bash
# Verify SPRING_PROFILES_ACTIVE is set
echo $SPRING_PROFILES_ACTIVE

# Or explicitly set it
export SPRING_PROFILES_ACTIVE=local
```

#### Database Connection Error
- Verify H2 driver is in dependencies (it is)
- Check application-local.yml syntax
- Ensure no conflicting datasource configuration

### Testing Database Connection

```bash
# Start application
./gradlew bootRun --args='--spring.profiles.active=local'

# In another terminal, test H2 console access
curl -I http://localhost:8082/h2-console
# Should return HTTP 302 (redirect to login)

# Or open in browser
open http://localhost:8082/h2-console
```

### Sample Data

The application starts with an empty database. To add sample data:

1. Access H2 console at http://localhost:8082/h2-console
2. Run SQL inserts manually, or
3. Use the admin API to create feature flags

Example SQL:
```sql
-- Insert admin user
INSERT INTO admin_users (username, password, email, role, enabled, created_at)
VALUES ('admin', '$2a$10$...', 'admin@example.com', 'SUPER_ADMIN', true, CURRENT_TIMESTAMP);

-- Insert feature flag
INSERT INTO feature_flags (name, description, status, created_at)
VALUES ('test-flag', 'Test feature flag', 'ON', CURRENT_TIMESTAMP);
```

### Next Steps

After verifying local setup works:
1. Implement security authorization (see `.claude/security-authorization-plan.md`)
2. Add unit tests
3. Add integration tests

---

**Last Updated**: 2025-12-03
**Status**: ✅ Configuration Fixed - Ready for Local Development
