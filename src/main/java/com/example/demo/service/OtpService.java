package com.example.demo.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.OtpVerification;
import com.example.demo.repository.OtpVerificationRepository;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;
    private static final int RESEND_COOLDOWN_SECONDS = 60;

    private final OtpVerificationRepository otpRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    private final String twilioAccountSid;
    private final String twilioAuthToken;
    private final String twilioFromNumber;

    public OtpService(OtpVerificationRepository otpRepository) {

        this.otpRepository = otpRepository;

        this.twilioAccountSid =
                getRequiredEnv("TWILIO_ACCOUNT_SID");

        this.twilioAuthToken =
                getRequiredEnv("TWILIO_AUTH_TOKEN");

        this.twilioFromNumber =
                getRequiredEnv("TWILIO_FROM_NUMBER");

        // Initialize Twilio
        Twilio.init(
                twilioAccountSid,
                twilioAuthToken
        );
    }

    // ============================================================
    // GENERATE + SEND OTP
    // ============================================================

    public String generateOtp(String phone) {

        phone = normalizePhone(phone);

        LocalDateTime now = LocalDateTime.now();

        // --------------------------------------------------------
        // RESEND COOLDOWN
        // --------------------------------------------------------

        Optional<OtpVerification> previousOptional =
                otpRepository
                        .findTopByPhoneOrderByCreatedAtDesc(phone);

        if (previousOptional.isPresent()) {

            OtpVerification previous =
                    previousOptional.get();

            if (previous.getLastSentAt() != null
                    && previous.getLastSentAt()
                    .plusSeconds(RESEND_COOLDOWN_SECONDS)
                    .isAfter(now)) {

                long secondsLeft =
                        java.time.Duration.between(
                                now,
                                previous.getLastSentAt()
                                        .plusSeconds(
                                                RESEND_COOLDOWN_SECONDS)
                        ).getSeconds();

                throw new RuntimeException(
                        "Please wait "
                                + Math.max(1, secondsLeft)
                                + " seconds before requesting another OTP."
                );
            }
        }

        // --------------------------------------------------------
        // GENERATE 6 DIGIT OTP
        // --------------------------------------------------------

        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        OtpVerification verification =
                new OtpVerification();

        verification.setPhone(phone);
        verification.setOtp(otp);
        verification.setCreatedAt(now);
        verification.setLastSentAt(now);
        verification.setExpiresAt(
                now.plusMinutes(OTP_EXPIRY_MINUTES)
        );
        verification.setAttempts(0);
        verification.setVerified(false);

        // Save OTP before sending
        otpRepository.save(verification);

        // --------------------------------------------------------
        // SMS MESSAGE
        // --------------------------------------------------------

        String messageText =
                "Your AakilSkin Hub verification OTP is "
                        + otp
                        + ". This OTP is valid for "
                        + OTP_EXPIRY_MINUTES
                        + " minutes. Do not share it with anyone.";

        // --------------------------------------------------------
        // TWILIO SEND
        // --------------------------------------------------------

        String recipientNumber = "+91" + phone;

        try {

            System.out.println(
                    "========== AAKILSKIN OTP =========="
            );

            System.out.println(
                    "Sending OTP SMS to: "
                            + maskPhone(phone)
            );

            System.out.println(
                    "Twilio From Number: "
                            + twilioFromNumber
            );

            Message message = Message.creator(
                    new PhoneNumber(recipientNumber),
                    new PhoneNumber(twilioFromNumber),
                    messageText
            ).create();

            System.out.println(
                    "OTP SMS sent successfully."
            );

            System.out.println(
                    "Twilio Message SID: "
                            + message.getSid()
            );

            System.out.println(
                    "Twilio Message Status: "
                            + message.getStatus()
            );

            System.out.println(
                    "==================================="
            );

        } catch (ApiException e) {

            // Remove OTP because SMS was not successfully submitted
            otpRepository.delete(verification);

            System.err.println(
                    "========== TWILIO OTP ERROR =========="
            );

            System.err.println(
                    "Twilio Error Code: "
                            + e.getCode()
            );

            System.err.println(
                    "Twilio Error Message: "
                            + e.getMessage()
            );

            System.err.println(
                    "Twilio HTTP Status: "
                            + e.getStatusCode()
            );

            System.err.println(
                    "Recipient: "
                            + maskPhone(phone)
            );

            System.err.println(
                    "From Number: "
                            + twilioFromNumber
            );

            System.err.println(
                    "======================================"
            );

            throw new RuntimeException(
                    "OTP could not be sent: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            // Remove OTP because SMS failed
            otpRepository.delete(verification);

            System.err.println(
                    "========== OTP SEND ERROR =========="
            );

            System.err.println(
                    "Error Type: "
                            + e.getClass().getName()
            );

            System.err.println(
                    "Error Message: "
                            + e.getMessage()
            );

            e.printStackTrace();

            System.err.println(
                    "===================================="
            );

            throw new RuntimeException(
                    "OTP could not be sent: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------------
        // SUCCESS LOG
        // --------------------------------------------------------

        System.out.println(
                "OTP sent successfully to mobile ending with "
                        + phone.substring(phone.length() - 4)
        );

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
                    "OTP is required."
            );
        }

        otp = otp.trim();

        OtpVerification verification =
                otpRepository
                        .findTopByPhoneOrderByCreatedAtDesc(phone)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "OTP not found. Please request a new OTP."
                                )
                        );

        // --------------------------------------------------------
        // ALREADY VERIFIED
        // --------------------------------------------------------

        if (verification.isVerified()) {
            return true;
        }

        // --------------------------------------------------------
        // EXPIRY CHECK
        // --------------------------------------------------------

        if (verification.getExpiresAt() == null) {

            throw new RuntimeException(
                    "OTP expiry information is missing."
            );
        }

        if (LocalDateTime.now()
                .isAfter(verification.getExpiresAt())) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new OTP."
            );
        }

        // --------------------------------------------------------
        // ATTEMPT LIMIT
        // --------------------------------------------------------

        if (verification.getAttempts() >= MAX_ATTEMPTS) {

            throw new RuntimeException(
                    "Maximum OTP attempts exceeded. Please request a new OTP."
            );
        }

        // --------------------------------------------------------
        // OTP CHECK
        // --------------------------------------------------------

        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            int remaining =
                    MAX_ATTEMPTS - verification.getAttempts();

            throw new RuntimeException(
                    "Invalid OTP. "
                            + Math.max(0, remaining)
                            + " attempt(s) remaining."
            );
        }

        // --------------------------------------------------------
        // SUCCESS
        // --------------------------------------------------------

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
                .map(OtpVerification::isVerified)
                .orElse(false);
    }

    // ============================================================
    // NORMALIZE PHONE
    // ============================================================

    private String normalizePhone(String phone) {

        if (phone == null) {

            throw new RuntimeException(
                    "Phone number is required."
            );
        }

        String normalized =
                phone.trim()
                        .replaceAll("\\s+", "")
                        .replaceAll("-", "")
                        .replaceAll("\\(", "")
                        .replaceAll("\\)", "");

        // --------------------------------------------------------
        // Accept +91XXXXXXXXXX
        // --------------------------------------------------------

        if (normalized.startsWith("+91")) {

            normalized =
                    normalized.substring(3);
        }

        // --------------------------------------------------------
        // Accept 91XXXXXXXXXX
        // --------------------------------------------------------

        else if (normalized.startsWith("91")
                && normalized.length() == 12) {

            normalized =
                    normalized.substring(2);
        }

        // --------------------------------------------------------
        // Validate Indian 10 digit mobile
        // --------------------------------------------------------

        if (!normalized.matches("[6-9]\\d{9}")) {

            throw new RuntimeException(
                    "Please enter a valid 10 digit Indian mobile number."
            );
        }

        return normalized;
    }

    // ============================================================
    // MASK PHONE FOR LOGS
    // ============================================================

    private String maskPhone(String phone) {

        if (phone == null || phone.length() < 4) {
            return "****";
        }

        return "******"
                + phone.substring(phone.length() - 4);
    }

    // ============================================================
    // READ REQUIRED ENVIRONMENT VARIABLE
    // ============================================================

    private String getRequiredEnv(String name) {

        String value = System.getenv(name);

        if (value == null || value.trim().isEmpty()) {

            throw new IllegalStateException(
                    "Missing required environment variable: "
                            + name
            );
        }

        return value.trim();
    }
}