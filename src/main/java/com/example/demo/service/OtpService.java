package com.example.demo.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.OtpVerification;
import com.example.demo.repository.OtpVerificationRepository;

@Service
public class OtpService {

        private static final int OTP_EXPIRY_MINUTES = 5;
        private static final int MAX_ATTEMPTS = 3;
        private static final int RESEND_COOLDOWN_SECONDS = 60;

        private final OtpVerificationRepository otpRepository;

        private final SecureRandom secureRandom = new SecureRandom();

        public OtpService(OtpVerificationRepository otpRepository) {
                this.otpRepository = otpRepository;
        }

        // ============================================================
        // GENERATE OTP
        // ============================================================

        public String generateOtp(String phone) {

                phone = normalizePhone(phone);

                LocalDateTime now = LocalDateTime.now();

                Optional<OtpVerification> previousOptional = otpRepository.findTopByPhoneOrderByCreatedAtDesc(phone);

                if (previousOptional.isPresent()) {

                        OtpVerification previous = previousOptional.get();

                        if (previous.getLastSentAt() != null
                                        && previous.getLastSentAt()
                                                        .plusSeconds(RESEND_COOLDOWN_SECONDS)
                                                        .isAfter(now)) {

                                throw new RuntimeException(
                                                "Please wait before requesting another OTP.");
                        }
                }

                // Generate 6 digit OTP
                String otp = String.format(
                                "%06d",
                                secureRandom.nextInt(1_000_000));

                OtpVerification verification = new OtpVerification();

                verification.setPhone(phone);
                verification.setOtp(otp);
                verification.setCreatedAt(now);
                verification.setLastSentAt(now);
                verification.setExpiresAt(
                                now.plusMinutes(OTP_EXPIRY_MINUTES));
                verification.setAttempts(0);
                verification.setVerified(false);

                otpRepository.save(verification);

                // ========================================================
                // DEVELOPMENT ONLY
                // ========================================================
                // Production mein SMS service/API connect karenge.

                System.out.println(
                                "=================================");

                System.out.println(
                                "OTP for " + phone + " = " + otp);

                System.out.println(
                                "Expires at: "
                                                + verification.getExpiresAt());

                System.out.println(
                                "=================================");

                return otp;
        }

        // ============================================================
        // VERIFY OTP
        // ============================================================

        public boolean verifyOtp(
                        String phone,
                        String otp) {

                phone = normalizePhone(phone);

                if (otp == null || otp.trim().isEmpty()) {
                        throw new RuntimeException(
                                        "OTP is required.");
                }

                otp = otp.trim();

                OtpVerification verification = otpRepository
                                .findTopByPhoneOrderByCreatedAtDesc(phone)
                                .orElseThrow(() -> new RuntimeException(
                                                "OTP not found."));

                // Already verified
                if (verification.isVerified()) {
                        return true;
                }

                // Expiry check
                if (verification.getExpiresAt() == null) {
                        throw new RuntimeException(
                                        "OTP expiry information is missing.");
                }

                if (LocalDateTime.now()
                                .isAfter(verification.getExpiresAt())) {

                        throw new RuntimeException(
                                        "OTP has expired.");
                }

                // Attempt limit
                if (verification.getAttempts() >= MAX_ATTEMPTS) {

                        throw new RuntimeException(
                                        "Maximum OTP attempts exceeded.");
                }

                // OTP check
                if (!verification.getOtp().equals(otp)) {

                        verification.setAttempts(
                                        verification.getAttempts() + 1);

                        otpRepository.save(verification);

                        throw new RuntimeException(
                                        "Invalid OTP.");
                }

                // Successful verification
                verification.setVerified(true);

                otpRepository.save(verification);

                return true;
        }

        // ============================================================
        // CHECK OTP VERIFIED
        // ============================================================

        public boolean isVerified(String phone) {

                phone = normalizePhone(phone);

                return otpRepository
                                .findTopByPhoneOrderByCreatedAtDesc(phone)
                                .map(verification -> verification.isVerified())
                                .orElse(false);
        }

        // ============================================================
        // NORMALIZE PHONE
        // ============================================================

        private String normalizePhone(String phone) {

                if (phone == null) {
                        throw new RuntimeException(
                                        "Phone number is required.");
                }

                String normalized = phone.replaceAll("\\s+", "");

                if (!normalized.matches("\\d{10}")) {

                        throw new RuntimeException(
                                        "Please enter a valid 10 digit mobile number.");
                }

                return normalized;
        }
}