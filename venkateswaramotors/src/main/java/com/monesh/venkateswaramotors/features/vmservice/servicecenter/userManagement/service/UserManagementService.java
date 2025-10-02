package com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.entity.User;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.repository.UserRepository;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.entity.Notification;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.service.NotificationService;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.CreateUserRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.CreateUserResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.GenericResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UpdateUserRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UserListResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Create a new user with role-based access
     */
    public CreateUserResponse createUser(CreateUserRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            return new CreateUserResponse("User with email " + request.getEmail() + " already exists", false);
        }

        // Validate phone number uniqueness
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return new CreateUserResponse("User with phone number " + request.getPhoneNumber() + " already exists", false);
        }

        // Validate role
        try {
            User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new CreateUserResponse("Invalid role: " + request.getRole() + ". Valid roles are: USER, ADMIN", false);
        }

        User user = request.toUser();
        User savedUser = userRepository.save(user);

        // Create notification for new user creation
        notificationService.createNotificationWithReferenceAndCustomer(
                "monesh141001@gmail.com",
                savedUser.getFirstName() + " " + savedUser.getLastName(),
                "New User Created",
                String.format("New user %s %s (%s) created with role %s",
                        savedUser.getFirstName(), savedUser.getLastName(),
                        savedUser.getEmail(), savedUser.getRole()),
                Notification.NotificationType.USER_CREATED,
                savedUser.getId(),
                "USER",
                Notification.NotificationPriority.MEDIUM
        );

        // Also notify the new user
        notificationService.createSimpleNotification(
                savedUser.getEmail(),
                "Welcome to Venkateswara Motors",
                String.format("Your account has been created with role: %s", savedUser.getRole()),
                Notification.NotificationType.USER_CREATED
        );

        return new CreateUserResponse("User created successfully", true);
    }

    /**
     * Get all users with pagination and filtering
     */
    public UserListResponse getAllUsers(int page, int size, String role, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> userPage;

        if (search != null && !search.trim().isEmpty()) {
            // Search by email, firstName, lastName, or phoneNumber
            userPage = userRepository
                    .findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrPhoneNumberContaining(
                            search.trim(), search.trim(), search.trim(), search.trim(), pageable);
        } else if (role != null && !role.trim().isEmpty()) {
            // Filter by role
            try {
                User.Role userRole = User.Role.valueOf(role.toUpperCase());
                userPage = userRepository.findByRole(userRole, pageable);
            } catch (IllegalArgumentException e) {
                // Return empty result for invalid role instead of throwing exception
                userPage = userRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
            }
        } else {
            // Get all users
            userPage = userRepository.findAll(pageable);
        }

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());

        return new UserListResponse(
                userResponses,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber(),
                userPage.getSize());
    }

    /**
     * Get user by ID
     */
    public GenericResponse getUserById(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new GenericResponse("User not found with ID: " + userId, false);
        }
        return new GenericResponse("User found successfully", true, UserResponse.fromUser(user));
    }

    /**
     * Get user by email
     */
    public GenericResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new GenericResponse("User not found with email: " + email, false);
        }
        return new GenericResponse("User found successfully", true, UserResponse.fromUser(user));
    }

    /**
     * Update user information
     */
    public GenericResponse updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new GenericResponse("User not found with ID: " + userId, false);
        }

        // Update fields if provided
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                return new GenericResponse("User with email " + request.getEmail() + " already exists", false);
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                return new GenericResponse("User with phone number " + request.getPhoneNumber() + " already exists", false);
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }

        if (request.getState() != null) {
            user.setState(request.getState());
        }

        if (request.getPincode() != null) {
            user.setPincode(request.getPincode());
        }

        if (request.getRole() != null) {
            try {
                User.Role newRole = User.Role.valueOf(request.getRole().toUpperCase());
                user.setRole(newRole);
            } catch (IllegalArgumentException e) {
                return new GenericResponse("Invalid role: " + request.getRole() + ". Valid roles are: USER, ADMIN", false);
            }
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        if (request.getAccountNonLocked() != null) {
            user.setAccountNonLocked(request.getAccountNonLocked());
        }

        user.onUpdate();
        User updatedUser = userRepository.save(user);
        return new GenericResponse("User updated successfully", true, UserResponse.fromUser(updatedUser));
    }

    /**
     * Delete user
     */
    public GenericResponse deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            return new GenericResponse("User not found with ID: " + userId, false);
        }
        userRepository.deleteById(userId);
        return new GenericResponse("User deleted successfully", true);
    }

    /**
     * Enable/Disable user account
     */
    public GenericResponse toggleUserStatus(String userId, boolean enabled) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new GenericResponse("User not found with ID: " + userId, false);
        }

        user.setEnabled(enabled);
        user.onUpdate();
        User updatedUser = userRepository.save(user);
        return new GenericResponse("User status updated successfully", true, UserResponse.fromUser(updatedUser));
    }

    /**
     * Lock/Unlock user account
     */
    public GenericResponse toggleUserLock(String userId, boolean locked) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new GenericResponse("User not found with ID: " + userId, false);
        }

        user.setAccountNonLocked(!locked);
        user.onUpdate();
        User updatedUser = userRepository.save(user);
        return new GenericResponse("User lock status updated successfully", true, UserResponse.fromUser(updatedUser));
    }

    /**
     * Change user role
     */
    public GenericResponse changeUserRole(String userId, String newRole) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new GenericResponse("User not found with ID: " + userId, false);
        }

        try {
            User.Role role = User.Role.valueOf(newRole.toUpperCase());
            user.setRole(role);
            user.onUpdate();
            User updatedUser = userRepository.save(user);
            return new GenericResponse("User role updated successfully", true, UserResponse.fromUser(updatedUser));
        } catch (IllegalArgumentException e) {
            return new GenericResponse("Invalid role: " + newRole + ". Valid roles are: USER, ADMIN", false);
        }
    }

    /**
     * Get users by role
     */
    public GenericResponse getUsersByRole(String role) {
        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(userRole);
            List<UserResponse> userResponses = users.stream()
                    .map(UserResponse::fromUser)
                    .collect(Collectors.toList());
            return new GenericResponse("Users retrieved successfully", true, userResponses);
        } catch (IllegalArgumentException e) {
            return new GenericResponse("Invalid role: " + role + ". Valid roles are: USER, ADMIN", false);
        }
    }

    /**
     * Get user statistics
     */
    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long adminUsers = userRepository.countByRole(User.Role.ADMIN);
        long regularUsers = userRepository.countByRole(User.Role.USER);
        long enabledUsers = userRepository.countByEnabledTrue();
        long lockedUsers = userRepository.countByAccountNonLockedFalse();

        return new UserStats(totalUsers, adminUsers, regularUsers, enabledUsers, lockedUsers);
    }

    public static class UserStats {
        private final long totalUsers;
        private final long adminUsers;
        private final long regularUsers;
        private final long enabledUsers;
        private final long lockedUsers;

        public UserStats(long totalUsers, long adminUsers, long regularUsers, long enabledUsers, long lockedUsers) {
            this.totalUsers = totalUsers;
            this.adminUsers = adminUsers;
            this.regularUsers = regularUsers;
            this.enabledUsers = enabledUsers;
            this.lockedUsers = lockedUsers;
        }

        // Getters
        public long getTotalUsers() {
            return totalUsers;
        }

        public long getAdminUsers() {
            return adminUsers;
        }

        public long getRegularUsers() {
            return regularUsers;
        }

        public long getEnabledUsers() {
            return enabledUsers;
        }

        public long getLockedUsers() {
            return lockedUsers;
        }
    }
}