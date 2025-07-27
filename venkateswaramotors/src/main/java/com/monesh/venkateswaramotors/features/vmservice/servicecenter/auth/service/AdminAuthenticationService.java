package com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class AdminAuthenticationService {

    @Autowired
    private UserService userService;

    /**
     * Check if the current user is authenticated and has ADMIN role
     * 
     * @param request HTTP request containing cookies
     * @return ResponseEntity with error if authentication fails, null if successful
     */
    public ResponseEntity<?> checkAdminAuthentication(HttpServletRequest request) {
        Optional<User> userOptional = userService.checkAuthenticationStatus(request.getCookies());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required. Please login first.");
        }

        User user = userOptional.get();
        if (user.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin privileges required.");
        }

        return null; // Authentication successful
    }
} 