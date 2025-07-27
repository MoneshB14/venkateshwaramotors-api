package com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.entity.User;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.service.UserService;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.CreateUserRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.CreateUserResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UpdateUserRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UserListResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UserResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service-center/user-management")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private UserService userService;

    /**
     * Check if the current user is authenticated and has ADMIN role
     */
    private ResponseEntity<?> checkAdminAuthentication(HttpServletRequest request) {
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

    /**
     * Create a new user
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        CreateUserResponse response = userManagementService.createUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all users with pagination and filtering
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            HttpServletRequest httpRequest) {
        
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        UserListResponse users = userManagementService.getAllUsers(page, size, role, search);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        UserResponse user = userManagementService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Get user by email
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        UserResponse user = userManagementService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Update user
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request,
            HttpServletRequest httpRequest) {
        
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        UserResponse user = userManagementService.updateUser(userId, request);
        return ResponseEntity.ok(user);
    }

    /**
     * Delete user
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        userManagementService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Enable/Disable user account
     */
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> toggleUserStatus(
            @PathVariable String userId,
            @RequestParam boolean enabled,
            HttpServletRequest httpRequest) {
        
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        UserResponse user = userManagementService.toggleUserStatus(userId, enabled);
        return ResponseEntity.ok(user);
    }

    /**
     * Lock/Unlock user account
     */
    @PatchMapping("/users/{userId}/lock")
    public ResponseEntity<?> toggleUserLock(
            @PathVariable String userId,
            @RequestParam boolean locked,
            HttpServletRequest httpRequest) {
        
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        UserResponse user = userManagementService.toggleUserLock(userId, locked);
        return ResponseEntity.ok(user);
    }

    /**
     * Change user role
     */
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<?> changeUserRole(
            @PathVariable String userId,
            @RequestParam String role,
            HttpServletRequest httpRequest) {
        
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        UserResponse user = userManagementService.changeUserRole(userId, role);
        return ResponseEntity.ok(user);
    }

    /**
     * Get users by role
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        List<UserResponse> users = userManagementService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        UserManagementService.UserStats stats = userManagementService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Test endpoint to verify authentication
     */
    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuthentication(HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }
        
        return ResponseEntity.ok("Authentication successful! You have admin access.");
    }

    /**
     * Simple health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Management Service is running!");
    }
}