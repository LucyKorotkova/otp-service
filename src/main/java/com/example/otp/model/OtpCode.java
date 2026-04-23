package com.example.otp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class OtpCode {

    private UUID id;
    private UUID userId;
    private String operationId;
    private String code;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public OtpCode() {}

    public OtpCode(UUID id, UUID userId, String operationId,
                   String code, String status,
                   LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.operationId = operationId;
        this.code = code;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getOperationId() { return operationId; }
    public String getCode() { return code; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}