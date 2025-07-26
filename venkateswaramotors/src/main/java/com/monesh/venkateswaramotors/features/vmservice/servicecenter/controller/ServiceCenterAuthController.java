package com.monesh.venkateswaramotors.features.vmservice.servicecenter.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.dto.AuthResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.dto.LoginRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.dto.OtpVerificationRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.dto.SignupRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.entity.User;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service-center/auth")
@CrossOrigin(origins = "*")
public class ServiceCenterAuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            boolean success = userService.signup(signupRequest);
            if (success) {
                return ResponseEntity.ok(new AuthResponse("User registered successfully", true));
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse("Registration failed", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            boolean userExists = userService.login(loginRequest.getEmail());
            if (userExists) {
                // Send OTP for login verification
                boolean otpSent = userService.sendLoginOTP(loginRequest.getEmail());
                if (otpSent) {
                    return ResponseEntity.ok(new AuthResponse("OTP sent to your email", false));
                } else {
                    return ResponseEntity.badRequest().body(new AuthResponse("Failed to send OTP", false));
                }
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse("User not found", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOTP(@Valid @RequestBody OtpVerificationRequest otpRequest,
            HttpServletResponse response) {
        try {
            boolean isValidOTP = userService.verifyLoginOTP(otpRequest.getEmail(), otpRequest.getOtp());
            if (isValidOTP) {
                // Get user details
                User user = userService.getUserByEmail(otpRequest.getEmail()).orElse(null);

                if (user != null) {
                    // Set authentication cookie (no JWT token needed)
                    Cookie authCookie = new Cookie("vm_auth_token", "vm_authenticated_user");
                    authCookie.setHttpOnly(true);
                    authCookie.setSecure(false); // Set to true in production with HTTPS
                    authCookie.setPath("/");
                    authCookie.setMaxAge(24 * 60 * 60); // 24 hours
                    response.addCookie(authCookie);

                    return ResponseEntity.ok(new AuthResponse(
                            "Login successful",
                            true,
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName()));
                } else {
                    return ResponseEntity.badRequest().body(new AuthResponse("User not found", false));
                }
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse("Invalid OTP", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<AuthResponse> resendOTP(@RequestParam String email) {
        try {
            boolean otpSent = userService.sendLoginOTP(email);
            if (otpSent) {
                return ResponseEntity.ok(new AuthResponse("OTP resent successfully", true));
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse("Failed to resend OTP", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        // Clear authentication cookie
        Cookie authCookie = new Cookie("vm_auth_token", null);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false);
        authCookie.setPath("/");
        authCookie.setMaxAge(0); // Delete cookie
        response.addCookie(authCookie);

        return ResponseEntity.ok(new AuthResponse("Logged out successfully", true));
    }
}