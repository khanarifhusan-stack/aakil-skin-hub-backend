package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.OtpService;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "*")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(
            @RequestBody Map<String, String> request) {

        String phone = request.get("phone");

        try {

            otpService.generateOtp(phone);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "OTP generated successfully."));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestBody Map<String, String> request) {

        String phone = request.get("phone");
        String otp = request.get("otp");

        try {

            boolean verified = otpService.verifyOtp(phone, otp);

            return ResponseEntity.ok(
                    Map.of(
                            "success", verified,
                            "verified", verified,
                            "message",
                            "Mobile number verified successfully."));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "verified", false,
                            "message", e.getMessage()));
        }
    }
}