package com.monesh.venkateswaramotors.utils;

import com.monesh.venkateswaramotors.constants.EmailTemplates;
import com.monesh.venkateswaramotors.global.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final RedisService redisService;
    private final EmailService emailService;

    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates 6-digit OTP
        return String.valueOf(otp);
    }

    public boolean sendOTP(String email) {
        try {
            log.info("Sending OTP to email: {}", email);
            
            String otp = generateOTP();
            redisService.setOTP(email, otp);
            
            // Create email template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("otp", otp);
            templateData.put("email", email);
            templateData.put("validityMinutes", "5");
            templateData.put("sentDateTime", Instant.now().toString());
            
            // Send email using database template
            emailService.sendEmailWithTemplate(
                    email,
                    "Login OTP - Venkateswara Motors",
                    EmailTemplates.OTP_EMAIL,
                    templateData).subscribe(
                            response -> {
                                log.info("OTP email sent successfully to: {}", email);
                            },
                            error -> log.error("Failed to send OTP email to: {}, Error: {}", 
                                    email, error.getMessage()));
            
            return true;
        } catch (Exception e) {
            log.error("Error sending OTP to email: {}, Error: {}", email, e.getMessage());
            return false;
        }
    }

    public boolean verifyOTP(String email, String otp) {
        return redisService.validateOTP(email, otp);
    }

    public void resendOTP(String email) {
        sendOTP(email);
    }
} 