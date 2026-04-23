package com.example.otp.service;

import com.example.otp.dao.OtpDao;
import com.example.otp.dao.UserDao;
import com.example.otp.model.OtpCode;
import com.example.otp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    private final OtpDao otpDao;
    private final UserDao userDao;
    private final FileOtpSender fileOtpSender;
    private final EmailSender emailSender;
    private final TelegramSender telegramSender;
    private final SmsSender smsSender;

    public OtpService(OtpDao otpDao,
                      UserDao userDao,
                      FileOtpSender fileOtpSender,
                      EmailSender emailSender,
                      TelegramSender telegramSender,
                      SmsSender smsSender) {
        this.otpDao = otpDao;
        this.userDao = userDao;
        this.fileOtpSender = fileOtpSender;
        this.emailSender = emailSender;
        this.telegramSender = telegramSender;
        this.smsSender = smsSender;
    }


    public String generateOtp(String username, String operationId) throws SQLException {

        logger.info("Generating OTP for user: {} and operation: {}", username, operationId);

        Optional<User> optionalUser = userDao.findByUsername(username);

        if (optionalUser.isEmpty()) {
            logger.warn("User not found: {}", username);
            return "User not found";
        }

        User user = optionalUser.get();

        String code = generateCode(6);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusMinutes(5);

        OtpCode otp = new OtpCode(
                UUID.randomUUID(),
                user.getId(),
                operationId,
                code,
                "ACTIVE",
                now,
                expires
        );

        otpDao.save(otp);



        try {
            fileOtpSender.send(username, operationId, code);
        } catch (Exception e) {
            logger.warn("File saving failed: {}", e.getMessage());
        }

        try {
            emailSender.send("lucykorotkovas@yandex.ru", code);
        } catch (Exception e) {
            logger.warn("Email sending failed: {}", e.getMessage());
        }

        try {
            telegramSender.send(code);
        } catch (Exception e) {
            logger.warn("Telegram sending failed: {}", e.getMessage());
        }

        try {
            smsSender.send("79123456789", code);
        } catch (Exception e) {
            logger.warn("SMS sending failed: {}", e.getMessage());
        }

        logger.info("OTP generated successfully for operation: {}", operationId);

        return code;
    }


    public String validateOtp(String operationId, String inputCode) throws SQLException {

        logger.info("Validating OTP for operation: {}", operationId);

        Optional<OtpCode> optionalOtp = otpDao.findByOperationId(operationId);

        if (optionalOtp.isEmpty()) {
            logger.warn("OTP not found for operation: {}", operationId);
            return "OTP not found";
        }

        OtpCode otp = optionalOtp.get();

        if (!otp.getStatus().equals("ACTIVE")) {
            logger.warn("OTP already used or expired for operation: {}", operationId);
            return "OTP already used or expired";
        }

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            otpDao.updateStatus(otp.getId(), "EXPIRED");
            logger.warn("OTP expired for operation: {}", operationId);
            return "OTP expired";
        }

        if (!otp.getCode().equals(inputCode)) {
            logger.warn("Invalid OTP attempt for operation: {}", operationId);
            return "Invalid code";
        }

        otpDao.updateStatus(otp.getId(), "USED");

        logger.info("OTP validated successfully for operation: {}", operationId);

        return "OTP validated successfully";
    }


    private String generateCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}