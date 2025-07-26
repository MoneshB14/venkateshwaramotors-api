package com.monesh.venkateswaramotors.global.service;

import com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto.ServiceBookingRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    private static final String AUTH_COOKIE_NAME = "vm_auth_token";
    private static final String VALID_AUTH_TOKEN = "vm_valid_token_2024"; // In production, use proper JWT tokens

    /**
     * Authenticate request using cookie-based authentication
     * 
     * @param request HTTP request containing cookies
     * @return true if authenticated, false otherwise
     */
    public boolean authenticateRequest(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                log.warn("No cookies found in request");
                return false;
            }

            Optional<Cookie> authCookie = Arrays.stream(cookies)
                    .filter(cookie -> AUTH_COOKIE_NAME.equals(cookie.getName()))
                    .findFirst();

            if (authCookie.isEmpty()) {
                log.warn("Authentication cookie not found");
                return false;
            }

            String token = authCookie.get().getValue();
            boolean isValid = VALID_AUTH_TOKEN.equals(token);

            if (isValid) {
                log.info("Request authenticated successfully");
            } else {
                log.warn("Invalid authentication token");
            }

            return isValid;

        } catch (Exception e) {
            log.error("Error during authentication: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate service booking request data (without authentication)
     * 
     * @param request Service booking request
     * @return true if valid, false otherwise
     */
    public boolean validateServiceBookingData(ServiceBookingRequest request) {
        try {
            // Validate contact number
            if (!isValidPhoneNumber(request.getContact())) {
                log.warn("Invalid phone number: {}", request.getContact());
                return false;
            }

            // Validate name
            if (request.getName() == null || request.getName().trim().isEmpty() || request.getName().length() < 2) {
                log.warn("Invalid name: {}", request.getName());
                return false;
            }

            // Validate preferred date
            if (!isValidDate(request.getPreferredDate())) {
                log.warn("Invalid preferred date: {}", request.getPreferredDate());
                return false;
            }

            // Validate preferred time
            if (!isValidTime(request.getPreferredTime())) {
                log.warn("Invalid preferred time: {}", request.getPreferredTime());
                return false;
            }

            // Validate vehicle registration number
            if (!isValidVehicleRegNumber(request.getRegNo())) {
                log.warn("Invalid vehicle registration number: {}", request.getRegNo());
                return false;
            }

            // Validate service type
            if (!isValidServiceType(request.getServiceType())) {
                log.warn("Invalid service type: {}", request.getServiceType());
                return false;
            }

            // Validate vehicle model
            if (request.getVehicleModel() == null || request.getVehicleModel().trim().isEmpty()) {
                log.warn("Invalid vehicle model: {}", request.getVehicleModel());
                return false;
            }

            log.info("Service booking data validated successfully for: {}", request.getName());
            return true;

        } catch (Exception e) {
            log.error("Error during data validation: {}", e.getMessage());
            return false;
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^[6-9]\\d{9}$");
    }

    private boolean isValidDate(String date) {
        try {
            java.time.LocalDate.parse(date);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String time) {
        return time != null && time.matches("^(0?[1-9]|1[0-2]):[0-5][0-9]\\s?(AM|PM)$");
    }

    private boolean isValidVehicleRegNumber(String regNo) {
        return regNo != null && regNo.matches("^[A-Z]{2}\\s\\d{1,2}\\s[A-Z]{1,2}\\s\\d{4}$");
    }

    private boolean isValidServiceType(String serviceType) {
        if (serviceType == null || serviceType.trim().isEmpty()) {
            return false;
        }

        String[] validServiceTypes = { "general", "chassis", "engine", "other" };
        for (String validType : validServiceTypes) {
            if (validType.equalsIgnoreCase(serviceType.trim())) {
                return true;
            }
        }
        return false;
    }
}