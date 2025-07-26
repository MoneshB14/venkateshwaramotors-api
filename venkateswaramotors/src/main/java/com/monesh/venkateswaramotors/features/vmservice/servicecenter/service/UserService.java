package com.monesh.venkateswaramotors.features.vmservice.servicecenter.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.dto.SignupRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.entity.User;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.repository.UserRepository;
import com.monesh.venkateswaramotors.utils.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public boolean signup(SignupRequest signupRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        if (userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            throw new RuntimeException("User with this phone number already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setRole(User.Role.USER);
        user.onCreate();

        userRepository.save(user);
        return true;
    }

    public boolean login(String email) {
        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    public boolean sendLoginOTP(String email) {
        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // Send OTP
        return otpService.sendOTP(email);
    }

    public boolean verifyLoginOTP(String email, String otp) {
        return otpService.verifyOTP(email, otp);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}