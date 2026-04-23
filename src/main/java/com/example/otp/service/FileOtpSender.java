package com.example.otp.service;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class FileOtpSender {

    public void send(String username, String operationId, String code) {

        try (FileWriter writer = new FileWriter("otp_codes.txt", true)) {

            writer.write(
                    "Time: " + LocalDateTime.now() +
                            ", User: " + username +
                            ", Operation: " + operationId +
                            ", Code: " + code +
                            System.lineSeparator()
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to write OTP to file", e);
        }
    }
}