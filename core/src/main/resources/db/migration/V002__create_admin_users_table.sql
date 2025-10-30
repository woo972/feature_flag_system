-- Create admin_users table for admin authentication
CREATE TABLE IF NOT EXISTS admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Create index on username for faster lookups
CREATE INDEX idx_admin_users_username ON admin_users(username);

-- Create index on enabled status for filtering
CREATE INDEX idx_admin_users_enabled ON admin_users(enabled);

-- Add comments
COMMENT ON TABLE admin_users IS 'Stores admin user accounts for authentication';
COMMENT ON COLUMN admin_users.username IS 'Unique username for admin login';
COMMENT ON COLUMN admin_users.password IS 'BCrypt hashed password';
COMMENT ON COLUMN admin_users.email IS 'Admin email address';
COMMENT ON COLUMN admin_users.role IS 'Admin role (ADMIN, SUPER_ADMIN)';
COMMENT ON COLUMN admin_users.enabled IS 'Whether the account is enabled';
COMMENT ON COLUMN admin_users.created_at IS 'Account creation timestamp';
COMMENT ON COLUMN admin_users.last_login_at IS 'Last successful login timestamp';

-- Create default admin user (password: admin123)
-- Password hash for 'admin123' using BCrypt
INSERT INTO admin_users (username, password, email, role, enabled, created_at)
VALUES ('admin', '$2a$10$rZ5FQQjqLvYO7gIpXfx2.eK8dWj3Ky7xKJH9NqB7xJ5.zVHzH5zXW', 'admin@featureflag.com', 'SUPER_ADMIN', true, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;
