package com.example.otp.dao;

import com.example.otp.model.OtpCode;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OtpDao {

    private final DataSource dataSource;

    public OtpDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(OtpCode otp) throws SQLException {

        String sql = """
                INSERT INTO otp_codes
                (id, user_id, operation_id, code, status, created_at, expires_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, otp.getId());
            stmt.setObject(2, otp.getUserId());
            stmt.setString(3, otp.getOperationId());
            stmt.setString(4, otp.getCode());
            stmt.setString(5, otp.getStatus());
            stmt.setTimestamp(6, Timestamp.valueOf(otp.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(otp.getExpiresAt()));

            stmt.executeUpdate();
        }
    }

    public Optional<OtpCode> findByOperationId(String operationId) throws SQLException {

        String sql = "SELECT * FROM otp_codes WHERE operation_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, operationId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    OtpCode otp = new OtpCode(
                            UUID.fromString(rs.getString("id")),
                            UUID.fromString(rs.getString("user_id")),
                            rs.getString("operation_id"),
                            rs.getString("code"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("expires_at").toLocalDateTime()
                    );
                    return Optional.of(otp);
                }
            }
        }

        return Optional.empty();
    }

    public void updateStatus(UUID id, String status) throws SQLException {

        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setObject(2, id);

            stmt.executeUpdate();
        }
    }

    public void expireOldOtps() throws SQLException {

        String sql = """
                UPDATE otp_codes
                SET status = 'EXPIRED'
                WHERE status = 'ACTIVE'
                AND expires_at < NOW()
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        }
    }
}