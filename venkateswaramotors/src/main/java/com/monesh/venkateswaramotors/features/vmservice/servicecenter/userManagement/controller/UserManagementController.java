package com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.service.AdminAuthenticationService;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.CreateUserRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.CreateUserResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.GenericResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UpdateUserRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UserListResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UserResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/service-center/user-management")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private AdminAuthenticationService adminAuthenticationService;

    /**
     * Create a new user
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
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

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
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
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = userManagementService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by email
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = userManagementService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Update user
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = userManagementService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = userManagementService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Enable/Disable user account
     */
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> toggleUserStatus(
            @PathVariable String userId,
            @RequestParam boolean enabled,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = userManagementService.toggleUserStatus(userId, enabled);
        return ResponseEntity.ok(response);
    }

    /**
     * Lock/Unlock user account
     */
    @PatchMapping("/users/{userId}/lock")
    public ResponseEntity<?> toggleUserLock(
            @PathVariable String userId,
            @RequestParam boolean locked,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = userManagementService.toggleUserLock(userId, locked);
        return ResponseEntity.ok(response);
    }

    /**
     * Change user role
     */
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<?> changeUserRole(
            @PathVariable String userId,
            @RequestParam String role,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = userManagementService.changeUserRole(userId, role);
        return ResponseEntity.ok(response);
    }

    /**
     * Get users by role
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = userManagementService.getUsersByRole(role);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        UserManagementService.UserStats stats = userManagementService.getUserStats();
        return ResponseEntity.ok(stats);
    }

}