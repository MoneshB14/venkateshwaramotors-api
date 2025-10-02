package com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.dto.SignupRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.entity.User;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.repository.UserRepository;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.entity.Notification;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.service.NotificationService;
import com.monesh.venkateswaramotors.utils.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public boolean signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        if (userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            throw new RuntimeException("User with this phone number already exists");
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setRole(User.Role.ADMIN);
        user.onCreate();

        User savedUser = userRepository.save(user);

        // Create welcome notification
        notificationService.createSimpleNotification(
                savedUser.getEmail(),
                "Welcome to Venkateswara Motors!",
                "Your account has been successfully created. Welcome aboard!",
                Notification.NotificationType.SIGNUP);

        return true;
    }

    public boolean login(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    public boolean sendLoginOTP(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        return otpService.sendOTP(email);
    }

    public boolean verifyLoginOTP(String email, String otp) {
        return otpService.verifyOTP(email, otp);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> checkAuthenticationStatus(Cookie[] cookies) {
        if (cookies == null) {
            return Optional.empty();
        }

        String authToken = null;
        String userEmail = null;

        for (Cookie cookie : cookies) {
            if ("vm_auth_token".equals(cookie.getName())) {
                authToken = cookie.getValue();
            }
            if ("vm_user_email".equals(cookie.getName())) {
                userEmail = cookie.getValue();
            }
        }

        // Check if both authentication token and user email are present and valid
        if (authToken != null && "vm_authenticated_user".equals(authToken) && userEmail != null) {
            // Verify user exists in database
            return userRepository.findByEmail(userEmail);
        }

        return Optional.empty();
    }
}