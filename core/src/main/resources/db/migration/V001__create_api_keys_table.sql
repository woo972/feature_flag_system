-- Create api_keys table for API key authentication
CREATE TABLE IF NOT EXISTS api_keys (
    id BIGSERIAL PRIMARY KEY,
    api_key VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    expires_at TIMESTAMP
);

-- Create index on api_key for faster lookups
CREATE INDEX idx_api_keys_api_key ON api_keys(api_key);

-- Create index on active status for filtering
CREATE INDEX idx_api_keys_active ON api_keys(active);

-- Add comments for documentation
COMMENT ON TABLE api_keys IS 'Stores API keys for SDK client authentication';
COMMENT ON COLUMN api_keys.api_key IS 'The actual API key value (base64 encoded)';
COMMENT ON COLUMN api_keys.name IS 'Human-readable name for the API key';
COMMENT ON COLUMN api_keys.description IS 'Optional description of the API key purpose';
COMMENT ON COLUMN api_keys.active IS 'Whether the API key is currently active';
COMMENT ON COLUMN api_keys.created_at IS 'Timestamp when the API key was created';
COMMENT ON COLUMN api_keys.last_used_at IS 'Timestamp when the API key was last used';
COMMENT ON COLUMN api_keys.expires_at IS 'Optional expiration timestamp for the API key';
