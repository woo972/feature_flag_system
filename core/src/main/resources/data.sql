-- Initial test data for local development
-- This file is automatically loaded by Spring Boot for the local profile

-- Admin Users
-- Password: admin123 (BCrypt encoded)
INSERT INTO admin_users (id, username, password, email, role, enabled, created_at, last_login_at) VALUES
(1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AKUXWgEpZKgW0y', 'admin@featureflag.com', 'SUPER_ADMIN', true, CURRENT_TIMESTAMP, NULL),
(2, 'testuser', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AKUXWgEpZKgW0y', 'testuser@featureflag.com', 'ADMIN', true, CURRENT_TIMESTAMP, NULL),
(3, 'disabled_user', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AKUXWgEpZKgW0y', 'disabled@featureflag.com', 'ADMIN', false, CURRENT_TIMESTAMP, NULL);

-- API Keys
INSERT INTO api_keys (id, api_key, name, description, status, created_at, last_used_at, expires_at) VALUES
(1, 'test_key_1234567890abcdef1234567890abcdef1234567890abcdef1234', 'Development Key', 'API key for local development and testing', 'ACTIVE', CURRENT_TIMESTAMP, NULL, NULL),
(2, 'test_key_prod_1234567890abcdef1234567890abcdef1234567890ab', 'Production Test Key', 'Simulates production API key', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', 90, CURRENT_TIMESTAMP)),
(3, 'test_key_revoked_1234567890abcdef1234567890abcdef12345678', 'Revoked Key', 'This key has been revoked for testing', 'REVOKED', DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -15, CURRENT_TIMESTAMP), NULL);

-- Feature Flags
INSERT INTO feature_flags (id, name, description, status, created_at, updated_at, archived_at) VALUES
(1, 'new_dashboard', 'New dashboard UI with enhanced analytics', 'ON', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(2, 'dark_mode', 'Dark mode theme support', 'ON', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(3, 'beta_features', 'Access to beta features for testing', 'OFF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(4, 'advanced_analytics', 'Advanced analytics and reporting', 'ON', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(5, 'archived_feature', 'This feature has been archived', 'OFF', DATEADD('DAY', -60, CURRENT_TIMESTAMP), DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -30, CURRENT_TIMESTAMP));

-- Targeting Rules for Feature Flags
-- Rules for 'new_dashboard' - only for premium users
INSERT INTO targeting_rule (id, feature_flag_id, name, operator, rule_values, created_at, updated_at) VALUES
(1, 1, 'user_tier', 'IN', '["premium", "enterprise"]', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 'region', 'NOT_IN', '["cn", "ru"]', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Rules for 'dark_mode' - available to all users with version >= 2.0
INSERT INTO targeting_rule (id, feature_flag_id, name, operator, rule_values, created_at, updated_at) VALUES
(3, 2, 'app_version', 'GREATER_THAN_EQUAL', '["2.0"]', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Rules for 'beta_features' - only for beta testers
INSERT INTO targeting_rule (id, feature_flag_id, name, operator, rule_values, created_at, updated_at) VALUES
(4, 3, 'user_group', 'EQUAL', '["beta_tester"]', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 3, 'account_age_days', 'GREATER_THAN', '["30"]', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Rules for 'advanced_analytics' - for enterprise tier only
INSERT INTO targeting_rule (id, feature_flag_id, name, operator, rule_values, created_at, updated_at) VALUES
(6, 4, 'user_tier', 'EQUAL', '["enterprise"]', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Pre-defined Targeting Rules (reusable rule templates)
INSERT INTO pre_defined_targeting_rule (id, name, description, operator, rule_values, created_at, updated_at) VALUES
(1, 'premium_users', 'Target premium tier users', 'IN', '["premium", "enterprise"]', CURRENT_TIMESTAMP, NULL),
(2, 'us_region', 'Target users in US region', 'EQUAL', '["us"]', CURRENT_TIMESTAMP, NULL),
(3, 'beta_testers', 'Target beta testing group', 'EQUAL', '["beta_tester"]', CURRENT_TIMESTAMP, NULL),
(4, 'exclude_free_tier', 'Exclude free tier users', 'NOT_EQUAL', '["free"]', CURRENT_TIMESTAMP, NULL),
(5, 'high_usage_users', 'Users with high API usage', 'GREATER_THAN', '["1000"]', CURRENT_TIMESTAMP, NULL);
