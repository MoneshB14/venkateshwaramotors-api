package com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.repository;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    // User management methods
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    List<User> findByRole(User.Role role);
    
    Page<User> findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrPhoneNumberContaining(
            String email, String firstName, String lastName, String phoneNumber, Pageable pageable);
    
    long countByRole(User.Role role);
    
    long countByEnabledTrue();
    
    long countByAccountNonLockedFalse();
} 