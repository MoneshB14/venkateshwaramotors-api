package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto.CreateSupplierRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto.GenericResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto.SupplierResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.Supplier;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    /**
     * Create a new supplier
     */
    public GenericResponse createSupplier(CreateSupplierRequest request, String createdBy) {
        // Validate supplier code uniqueness
        if (supplierRepository.existsBySupplierCode(request.getSupplierCode())) {
            return new GenericResponse("Supplier with code " + request.getSupplierCode() + " already exists", false);
        }

        // Validate email uniqueness
        if (supplierRepository.findByEmail(request.getEmail()).isPresent()) {
            return new GenericResponse("Supplier with email " + request.getEmail() + " already exists", false);
        }

        // Validate phone uniqueness
        if (supplierRepository.findByPhone(request.getPhone()).isPresent()) {
            return new GenericResponse("Supplier with phone " + request.getPhone() + " already exists", false);
        }

        Supplier supplier = request.toSupplier();
        supplier.setCreatedBy(createdBy);
        supplierRepository.save(supplier);
        return new GenericResponse("Supplier created successfully", true);
    }

    /**
     * Get all suppliers with pagination and filtering
     */
    public Page<SupplierResponse> getAllSuppliers(int page, int size, String search, Boolean isActive) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Supplier> supplierPage;

        if (search != null && !search.trim().isEmpty()) {
            supplierPage = supplierRepository.searchSuppliers(search.trim(), pageable);
        } else if (isActive != null) {
            if (search != null && !search.trim().isEmpty()) {
                supplierPage = supplierRepository.findByIsActiveAndSearch(isActive, search.trim(), pageable);
            } else {
                List<Supplier> suppliers = supplierRepository.findByIsActive(isActive);
                supplierPage = createPageFromList(suppliers, pageable);
            }
        } else {
            supplierPage = supplierRepository.findAll(pageable);
        }

        return supplierPage.map(SupplierResponse::fromSupplier);
    }

    /**
     * Get supplier by ID
     */
    public GenericResponse getSupplierById(String supplierId) {
        Optional<Supplier> supplier = supplierRepository.findById(supplierId);
        if (supplier.isEmpty()) {
            return new GenericResponse("Supplier not found with ID: " + supplierId, false);
        }
        return new GenericResponse("Supplier found successfully", true, SupplierResponse.fromSupplier(supplier.get()));
    }

    /**
     * Get supplier by supplier code
     */
    public GenericResponse getSupplierByCode(String supplierCode) {
        Optional<Supplier> supplier = supplierRepository.findBySupplierCode(supplierCode);
        if (supplier.isEmpty()) {
            return new GenericResponse("Supplier not found with code: " + supplierCode, false);
        }
        return new GenericResponse("Supplier found successfully", true, SupplierResponse.fromSupplier(supplier.get()));
    }

    /**
     * Update supplier
     */
    public GenericResponse updateSupplier(String supplierId, CreateSupplierRequest request, String updatedBy) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        if (optionalSupplier.isEmpty()) {
            return new GenericResponse("Supplier not found with ID: " + supplierId, false);
        }

        Supplier supplier = optionalSupplier.get();
        
        // Check if email is being changed and if it's already taken
        if (request.getEmail() != null && !request.getEmail().equals(supplier.getEmail())) {
            if (supplierRepository.findByEmail(request.getEmail()).isPresent()) {
                return new GenericResponse("Supplier with email " + request.getEmail() + " already exists", false);
            }
        }

        // Check if phone is being changed and if it's already taken
        if (request.getPhone() != null && !request.getPhone().equals(supplier.getPhone())) {
            if (supplierRepository.findByPhone(request.getPhone()).isPresent()) {
                return new GenericResponse("Supplier with phone " + request.getPhone() + " already exists", false);
            }
        }

        // Update fields
        if (request.getSupplierCode() != null) supplier.setSupplierCode(request.getSupplierCode());
        if (request.getName() != null) supplier.setName(request.getName());
        if (request.getContactPerson() != null) supplier.setContactPerson(request.getContactPerson());
        if (request.getEmail() != null) supplier.setEmail(request.getEmail());
        if (request.getPhone() != null) supplier.setPhone(request.getPhone());
        if (request.getAlternatePhone() != null) supplier.setAlternatePhone(request.getAlternatePhone());
        if (request.getAddress() != null) supplier.setAddress(request.getAddress());
        if (request.getCity() != null) supplier.setCity(request.getCity());
        if (request.getState() != null) supplier.setState(request.getState());
        if (request.getPincode() != null) supplier.setPincode(request.getPincode());
        if (request.getCountry() != null) supplier.setCountry(request.getCountry());
        if (request.getGstNumber() != null) supplier.setGstNumber(request.getGstNumber());
        if (request.getPanNumber() != null) supplier.setPanNumber(request.getPanNumber());
        if (request.getBankName() != null) supplier.setBankName(request.getBankName());
        if (request.getBankAccountNumber() != null) supplier.setBankAccountNumber(request.getBankAccountNumber());
        if (request.getIfscCode() != null) supplier.setIfscCode(request.getIfscCode());
        if (request.getPaymentTerms() != null) supplier.setPaymentTerms(request.getPaymentTerms());
        if (request.getCreditLimit() != null) supplier.setCreditLimit(request.getCreditLimit());
        if (request.getRating() != null) supplier.setRating(request.getRating());
        if (request.getSpecializations() != null) supplier.setSpecializations(request.getSpecializations());
        if (request.getNotes() != null) supplier.setNotes(request.getNotes());

        supplier.setUpdatedBy(updatedBy);
        supplier.onUpdate();
        supplierRepository.save(supplier);
        return new GenericResponse("Supplier updated successfully", true);
    }

    /**
     * Delete supplier
     */
    public GenericResponse deleteSupplier(String supplierId) {
        Optional<Supplier> supplier = supplierRepository.findById(supplierId);
        if (supplier.isEmpty()) {
            return new GenericResponse("Supplier not found with ID: " + supplierId, false);
        }
        supplierRepository.deleteById(supplierId);
        return new GenericResponse("Supplier deleted successfully", true);
    }

    /**
     * Toggle supplier status
     */
    public GenericResponse toggleSupplierStatus(String supplierId, boolean isActive, String updatedBy) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        if (optionalSupplier.isEmpty()) {
            return new GenericResponse("Supplier not found with ID: " + supplierId, false);
        }

        Supplier supplier = optionalSupplier.get();
        supplier.setActive(isActive);
        supplier.setUpdatedBy(updatedBy);
        supplier.onUpdate();
        supplierRepository.save(supplier);
        
        String status = isActive ? "activated" : "deactivated";
        return new GenericResponse("Supplier " + status + " successfully", true);
    }

    /**
     * Get suppliers by city
     */
    public GenericResponse getSuppliersByCity(String city) {
        List<Supplier> suppliers = supplierRepository.findByCity(city);
        List<SupplierResponse> responses = suppliers.stream()
                .map(SupplierResponse::fromSupplier)
                .collect(Collectors.toList());
        return new GenericResponse("Suppliers retrieved successfully", true, responses);
    }

    /**
     * Get suppliers by state
     */
    public GenericResponse getSuppliersByState(String state) {
        List<Supplier> suppliers = supplierRepository.findByState(state);
        List<SupplierResponse> responses = suppliers.stream()
                .map(SupplierResponse::fromSupplier)
                .collect(Collectors.toList());
        return new GenericResponse("Suppliers retrieved successfully", true, responses);
    }

    /**
     * Get suppliers by specialization
     */
    public GenericResponse getSuppliersBySpecialization(String specialization) {
        List<Supplier> suppliers = supplierRepository.findBySpecializationsContaining(specialization);
        List<SupplierResponse> responses = suppliers.stream()
                .map(SupplierResponse::fromSupplier)
                .collect(Collectors.toList());
        return new GenericResponse("Suppliers retrieved successfully", true, responses);
    }

    /**
     * Get supplier statistics
     */
    public SupplierStats getSupplierStats() {
        long totalSuppliers = supplierRepository.count();
        long activeSuppliers = supplierRepository.countByIsActive(true);
        long inactiveSuppliers = supplierRepository.countByIsActive(false);
        
        return new SupplierStats(totalSuppliers, activeSuppliers, inactiveSuppliers);
    }

    // Helper method to create Page from List
    private Page<Supplier> createPageFromList(List<Supplier> suppliers, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), suppliers.size());
        
        if (start > suppliers.size()) {
            return Page.empty(pageable);
        }
        
        return new org.springframework.data.domain.PageImpl<>(
                suppliers.subList(start, end), 
                pageable, 
                suppliers.size()
        );
    }

    // Statistics class
    public static class SupplierStats {
        private final long totalSuppliers;
        private final long activeSuppliers;
        private final long inactiveSuppliers;

        public SupplierStats(long totalSuppliers, long activeSuppliers, long inactiveSuppliers) {
            this.totalSuppliers = totalSuppliers;
            this.activeSuppliers = activeSuppliers;
            this.inactiveSuppliers = inactiveSuppliers;
        }

        // Getters
        public long getTotalSuppliers() { return totalSuppliers; }
        public long getActiveSuppliers() { return activeSuppliers; }
        public long getInactiveSuppliers() { return inactiveSuppliers; }
    }
} 