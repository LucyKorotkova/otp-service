CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE otp_config (
    id BOOLEAN PRIMARY KEY DEFAULT TRUE,
    code_length INT NOT NULL,
    ttl_seconds INT NOT NULL
);

INSERT INTO otp_config (id, code_length, ttl_seconds)
VALUES (TRUE, 6, 300);

CREATE TABLE otp_codes (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    operation_id VARCHAR(255),
    code VARCHAR(20),
    status VARCHAR(20),
    created_at TIMESTAMP,
    expires_at TIMESTAMP
);