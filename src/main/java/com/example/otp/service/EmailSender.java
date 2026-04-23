package com.example.otp.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;

@Component
public class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private final Session session;
    private final String fromEmail;

    public EmailSender() {
        try {
            Properties config = new Properties();

            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("email.properties");

            config.load(input);

            String username = config.getProperty("email.username").trim();
            String password = config.getProperty("email.password").trim();
            this.fromEmail = config.getProperty("email.from").trim();

            this.session = Session.getInstance(config, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Failed to load email configuration", e);
        }
    }

    public void send(String toEmail, String code) {

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Your OTP Code");
            message.setText("Your verification code is: " + code);

            Transport.send(message);

            logger.info("Email sent successfully to {}", toEmail);

        } catch (Exception e) {
            logger.error("Email sending failed: {}", e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}