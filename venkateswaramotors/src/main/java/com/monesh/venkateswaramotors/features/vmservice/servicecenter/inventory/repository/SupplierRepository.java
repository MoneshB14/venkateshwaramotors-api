package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.repository;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends MongoRepository<Supplier, String> {
    
    Optional<Supplier> findBySupplierCode(String supplierCode);
    
    boolean existsBySupplierCode(String supplierCode);
    
    Optional<Supplier> findByEmail(String email);
    
    Optional<Supplier> findByPhone(String phone);
    
    List<Supplier> findByIsActive(boolean isActive);
    
    List<Supplier> findByCity(String city);
    
    List<Supplier> findByState(String state);
    
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'supplierCode': {$regex: ?0, $options: 'i'}}, {'contactPerson': {$regex: ?0, $options: 'i'}}, {'email': {$regex: ?0, $options: 'i'}}]}")
    Page<Supplier> searchSuppliers(String searchTerm, Pageable pageable);
    
    @Query("{'$and': [{'isActive': ?0}, {'$or': [{'name': {$regex: ?1, $options: 'i'}}, {'supplierCode': {$regex: ?1, $options: 'i'}}, {'contactPerson': {$regex: ?1, $options: 'i'}}]}]}")
    Page<Supplier> findByIsActiveAndSearch(boolean isActive, String searchTerm, Pageable pageable);
    
    List<Supplier> findBySpecializationsContaining(String specialization);
    
    long countByIsActive(boolean isActive);
    
    long countByCity(String city);
    
    long countByState(String state);
} 