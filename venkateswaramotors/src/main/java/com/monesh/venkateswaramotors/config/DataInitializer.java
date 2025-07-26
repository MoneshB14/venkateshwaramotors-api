package com.monesh.venkateswaramotors.config;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.entity.User;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userRepository.existsByEmail("admin@venkateswaramotors.com")) {
            User adminUser = new User();
            adminUser.setEmail("admin@venkateswaramotors.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setPhoneNumber("9999999999");
            adminUser.setRole(User.Role.ADMIN);
            adminUser.onCreate();
            
            userRepository.save(adminUser);
            System.out.println("Default admin user created: admin@venkateswaramotors.com");
        }
    }
} 