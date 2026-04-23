package com.example.otp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class TelegramSender {

    private static final Logger logger = LoggerFactory.getLogger(TelegramSender.class);

    private static final String BOT_TOKEN = "8734162801:AAELYX5GkKpB8RTEwLJEpq2-0n6gmkrp1mU";

    private static final String CHAT_ID = "358860448";

    public void send(String code) {

        try {
            String message = "Your OTP code is: " + code;

            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                    BOT_TOKEN,
                    CHAT_ID,
                    URLEncoder.encode(message, StandardCharsets.UTF_8)
            );

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("Telegram response: {}", response.body());

        } catch (Exception e) {
            logger.error("Failed to send Telegram message: {}", e.getMessage());
            throw new RuntimeException("Telegram sending failed", e);
        }
    }
}