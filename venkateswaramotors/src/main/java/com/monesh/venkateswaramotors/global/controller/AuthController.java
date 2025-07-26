package com.monesh.venkateswaramotors.global.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/global/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private static final String AUTH_COOKIE_NAME = "vm_auth_token";
    private static final String VALID_AUTH_TOKEN = "vm_authenticated_user";

    /**
     * Login endpoint to set authentication cookie
     * 
     * @param response HTTP response to set cookie
     * @return Login response
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(HttpServletResponse response) {
        try {
            // Create authentication cookie
            Cookie authCookie = new Cookie(AUTH_COOKIE_NAME, VALID_AUTH_TOKEN);
            authCookie.setPath("/");
            authCookie.setMaxAge(24 * 60 * 60); // 24 hours
            authCookie.setHttpOnly(true);
            authCookie.setSecure(false); // Set to true in production with HTTPS

            response.addCookie(authCookie);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Login successful");

            log.info("User logged in successfully");

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", false);
            responseBody.put("message", "Login failed");

            return ResponseEntity.badRequest().body(responseBody);
        }
    }

    /**
     * Logout endpoint to clear authentication cookie
     * 
     * @param response HTTP response to clear cookie
     * @return Logout response
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        try {
            // Clear authentication cookie
            Cookie authCookie = new Cookie(AUTH_COOKIE_NAME, "");
            authCookie.setPath("/");
            authCookie.setMaxAge(0);
            authCookie.setHttpOnly(true);

            response.addCookie(authCookie);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Logout successful");

            log.info("User logged out successfully");

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", false);
            responseBody.put("message", "Logout failed");

            return ResponseEntity.badRequest().body(responseBody);
        }
    }

}