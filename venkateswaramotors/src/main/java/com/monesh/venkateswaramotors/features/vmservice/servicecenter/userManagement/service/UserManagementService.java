package com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.entity.User;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.repository.UserRepository;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.CreateUserRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto.CreateUserResponse;
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

    /**
     * Create a new user with role-based access
     */
    public CreateUserResponse createUser(CreateUserRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        // Validate phone number uniqueness
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("User with phone number " + request.getPhoneNumber() + " already exists");
        }

        // Validate role
        try {
            User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + request.getRole() + ". Valid roles are: USER, ADMIN");
        }

        User user = request.toUser();
        userRepository.save(user);
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
                throw new RuntimeException("Invalid role: " + role);
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
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return UserResponse.fromUser(user);
    }

    /**
     * Get user by email
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return UserResponse.fromUser(user);
    }

    /**
     * Update user information
     */
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Update fields if provided
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("User with email " + request.getEmail() + " already exists");
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
                throw new RuntimeException("User with phone number " + request.getPhoneNumber() + " already exists");
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
                throw new RuntimeException("Invalid role: " + request.getRole());
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
        return UserResponse.fromUser(updatedUser);
    }

    /**
     * Delete user
     */
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Enable/Disable user account
     */
    public UserResponse toggleUserStatus(String userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setEnabled(enabled);
        user.onUpdate();
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    /**
     * Lock/Unlock user account
     */
    public UserResponse toggleUserLock(String userId, boolean locked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setAccountNonLocked(!locked);
        user.onUpdate();
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    /**
     * Change user role
     */
    public UserResponse changeUserRole(String userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        try {
            User.Role role = User.Role.valueOf(newRole.toUpperCase());
            user.setRole(role);
            user.onUpdate();
            User updatedUser = userRepository.save(user);
            return UserResponse.fromUser(updatedUser);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + newRole + ". Valid roles are: USER, ADMIN");
        }
    }

    /**
     * Get users by role
     */
    public List<UserResponse> getUsersByRole(String role) {
        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(userRole);
            return users.stream()
                    .map(UserResponse::fromUser)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
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