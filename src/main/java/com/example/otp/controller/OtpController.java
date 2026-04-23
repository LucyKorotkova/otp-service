package com.example.otp.controller;

import com.example.otp.service.OtpService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public String generate(@RequestParam String username,
                           @RequestParam String operationId) throws Exception {

        return otpService.generateOtp(username, operationId);
    }

    @PostMapping("/validate")
    public String validate(@RequestParam String operationId,
                           @RequestParam String code) throws Exception {

        return otpService.validateOtp(operationId, code);
    }
}