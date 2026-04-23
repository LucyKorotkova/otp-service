package com.example.otp.service;

import com.example.otp.dao.OtpDao;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OtpScheduler {

    private final OtpDao otpDao;

    public OtpScheduler(OtpDao otpDao) {
        this.otpDao = otpDao;
    }

    @Scheduled(fixedRate = 60000)
    public void expireOtps() {
        try {
            otpDao.expireOldOtps();
            System.out.println("Expired OTP check executed");
        } catch (Exception e) {
            System.out.println("Error expiring OTPs: " + e.getMessage());
        }
    }
}