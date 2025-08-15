package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.service.AdminAuthenticationService;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto.*;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.service.InventoryService;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/vm/api/service-center/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private AdminAuthenticationService adminAuthenticationService;

    /**
     * Create a new inventory item
     */
    @PostMapping("/items")
    public ResponseEntity<?> createInventoryItem(@Valid @RequestBody CreateInventoryItemRequest request,
            HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        String createdBy = "admin"; // Get from authentication context
        GenericResponse response = inventoryService.createInventoryItem(request, createdBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all inventory items with pagination and filtering
     */
    @GetMapping("/items")
    public ResponseEntity<?> getAllInventoryItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        InventoryListResponse items = inventoryService.getAllInventoryItems(page, size, category, status, search);
        return ResponseEntity.ok(items);
    }

    /**
     * Get inventory item by ID
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<?> getInventoryItemById(@PathVariable String itemId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = inventoryService.getInventoryItemById(itemId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get inventory item by item code
     */
    @GetMapping("/items/code/{itemCode}")
    public ResponseEntity<?> getInventoryItemByCode(@PathVariable String itemCode, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = inventoryService.getInventoryItemByCode(itemCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Update inventory item
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateInventoryItem(
            @PathVariable String itemId,
            @Valid @RequestBody UpdateInventoryItemRequest request,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        String updatedBy = "admin"; // Get from authentication context
        GenericResponse response = inventoryService.updateInventoryItem(itemId, request, updatedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete inventory item
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteInventoryItem(@PathVariable String itemId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = inventoryService.deleteInventoryItem(itemId);
        return ResponseEntity.ok(response);
    }

    /**
     * Adjust stock
     */
    @PostMapping("/items/stock-adjustment")
    public ResponseEntity<?> adjustStock(@Valid @RequestBody StockAdjustmentRequest request,
            HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        String performedBy = "admin"; // Get from authentication context
        GenericResponse response = inventoryService.adjustStock(request, performedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Get low stock items
     */
    @GetMapping("/items/low-stock")
    public ResponseEntity<?> getLowStockItems(HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = inventoryService.getLowStockItems();
        return ResponseEntity.ok(response);
    }

    /**
     * Get out of stock items
     */
    @GetMapping("/items/out-of-stock")
    public ResponseEntity<?> getOutOfStockItems(HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = inventoryService.getOutOfStockItems();
        return ResponseEntity.ok(response);
    }

    /**
     * Get items by category
     */
    @GetMapping("/items/category/{category}")
    public ResponseEntity<?> getItemsByCategory(@PathVariable String category, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = inventoryService.getItemsByCategory(category);
        return ResponseEntity.ok(response);
    }

    /**
     * Get items by status
     */
    @GetMapping("/items/status/{status}")
    public ResponseEntity<?> getItemsByStatus(@PathVariable String status, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = inventoryService.getItemsByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Get item transaction history
     */
    @GetMapping("/items/{itemId}/transactions")
    public ResponseEntity<?> getItemTransactionHistory(
            @PathVariable String itemId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        LocalDateTime start = null;
        LocalDateTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        if (startDate != null) {
            start = LocalDateTime.parse(startDate, formatter);
        }
        if (endDate != null) {
            end = LocalDateTime.parse(endDate, formatter);
        }

        GenericResponse response = inventoryService.getItemTransactionHistory(itemId, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * Get inventory statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getInventoryStats(HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        InventoryService.InventoryStats stats = inventoryService.getInventoryStats();
        return ResponseEntity.ok(stats);
    }

    // ==================== SUPPLIERS ====================

    /**
     * Create a new supplier
     */
    @PostMapping("/suppliers")
    public ResponseEntity<?> createSupplier(@Valid @RequestBody CreateSupplierRequest request,
            HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        String createdBy = "admin"; // Get from authentication context
        GenericResponse response = supplierService.createSupplier(request, createdBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all suppliers with pagination and filtering
     */
    @GetMapping("/suppliers")
    public ResponseEntity<?> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        Page<SupplierResponse> suppliers = supplierService.getAllSuppliers(page, size, search, isActive);
        return ResponseEntity.ok(suppliers);
    }

    /**
     * Get supplier by ID
     */
    @GetMapping("/suppliers/{supplierId}")
    public ResponseEntity<?> getSupplierById(@PathVariable String supplierId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = supplierService.getSupplierById(supplierId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get supplier by supplier code
     */
    @GetMapping("/suppliers/code/{supplierCode}")
    public ResponseEntity<?> getSupplierByCode(@PathVariable String supplierCode, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = supplierService.getSupplierByCode(supplierCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Update supplier
     */
    @PutMapping("/suppliers/{supplierId}")
    public ResponseEntity<?> updateSupplier(
            @PathVariable String supplierId,
            @Valid @RequestBody CreateSupplierRequest request,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        String updatedBy = "admin"; // Get from authentication context
        GenericResponse response = supplierService.updateSupplier(supplierId, request, updatedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete supplier
     */
    @DeleteMapping("/suppliers/{supplierId}")
    public ResponseEntity<?> deleteSupplier(@PathVariable String supplierId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = supplierService.deleteSupplier(supplierId);
        return ResponseEntity.ok(response);
    }

    /**
     * Toggle supplier status
     */
    @PatchMapping("/suppliers/{supplierId}/status")
    public ResponseEntity<?> toggleSupplierStatus(
            @PathVariable String supplierId,
            @RequestParam boolean isActive,
            HttpServletRequest httpRequest) {

        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        String updatedBy = "admin"; // Get from authentication context
        GenericResponse response = supplierService.toggleSupplierStatus(supplierId, isActive, updatedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Get suppliers by city
     */
    @GetMapping("/suppliers/city/{city}")
    public ResponseEntity<?> getSuppliersByCity(@PathVariable String city, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = supplierService.getSuppliersByCity(city);
        return ResponseEntity.ok(response);
    }

    /**
     * Get suppliers by state
     */
    @GetMapping("/suppliers/state/{state}")
    public ResponseEntity<?> getSuppliersByState(@PathVariable String state, HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = supplierService.getSuppliersByState(state);
        return ResponseEntity.ok(response);
    }

    /**
     * Get suppliers by specialization
     */
    @GetMapping("/suppliers/specialization/{specialization}")
    public ResponseEntity<?> getSuppliersBySpecialization(@PathVariable String specialization,
            HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        GenericResponse response = supplierService.getSuppliersBySpecialization(specialization);
        return ResponseEntity.ok(response);
    }

    /**
     * Get supplier statistics
     */
    @GetMapping("/suppliers/stats")
    public ResponseEntity<?> getSupplierStats(HttpServletRequest httpRequest) {
        ResponseEntity<?> authCheck = adminAuthenticationService.checkAdminAuthentication(httpRequest);
        if (authCheck != null) {
            return authCheck;
        }

        SupplierService.SupplierStats stats = supplierService.getSupplierStats();
        return ResponseEntity.ok(stats);
    }
}