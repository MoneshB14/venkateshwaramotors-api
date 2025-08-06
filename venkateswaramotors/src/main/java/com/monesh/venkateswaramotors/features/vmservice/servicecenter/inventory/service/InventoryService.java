package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto.*;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.InventoryItem;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.InventoryTransaction;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.repository.InventoryItemRepository;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.repository.InventoryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    /**
     * Create a new inventory item
     */
    public GenericResponse createInventoryItem(CreateInventoryItemRequest request, String createdBy) {
        // Validate item code uniqueness
        if (inventoryItemRepository.existsByItemCode(request.getItemCode())) {
            return new GenericResponse("Item with code " + request.getItemCode() + " already exists", false);
        }

        // Validate category
        try {
            InventoryItem.Category.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new GenericResponse("Invalid category: " + request.getCategory(), false);
        }

        InventoryItem item = request.toInventoryItem();
        item.setCreatedBy(createdBy);
        inventoryItemRepository.save(item);
        return new GenericResponse("Inventory item created successfully", true);
    }

    /**
     * Get all inventory items with pagination and filtering
     */
    public InventoryListResponse getAllInventoryItems(int page, int size, String category, String status,
            String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<InventoryItem> itemPage;

        if (search != null && !search.trim().isEmpty()) {
            itemPage = inventoryItemRepository.searchItems(search.trim(), pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            if (search != null && !search.trim().isEmpty()) {
                itemPage = inventoryItemRepository.findByCategoryAndSearch(category.trim(), search.trim(), pageable);
            } else {
                List<InventoryItem> items = inventoryItemRepository.findByCategory(category.trim());
                itemPage = createPageFromList(items, pageable);
            }
        } else if (status != null && !status.trim().isEmpty()) {
            try {
                InventoryItem.Status itemStatus = InventoryItem.Status.valueOf(status.toUpperCase());
                if (search != null && !search.trim().isEmpty()) {
                    itemPage = inventoryItemRepository.findByStatusAndSearch(itemStatus, search.trim(), pageable);
                } else {
                    List<InventoryItem> items = inventoryItemRepository.findByStatus(itemStatus);
                    itemPage = createPageFromList(items, pageable);
                }
            } catch (IllegalArgumentException e) {
                itemPage = inventoryItemRepository.findAll(pageable);
            }
        } else {
            itemPage = inventoryItemRepository.findAll(pageable);
        }

        List<InventoryItemResponse> itemResponses = itemPage.getContent().stream()
                .map(InventoryItemResponse::fromInventoryItem)
                .collect(Collectors.toList());

        return new InventoryListResponse(
                itemResponses,
                itemPage.getTotalElements(),
                itemPage.getTotalPages(),
                itemPage.getNumber(),
                itemPage.getSize());
    }

    /**
     * Get inventory item by ID
     */
    public GenericResponse getInventoryItemById(String itemId) {
        Optional<InventoryItem> item = inventoryItemRepository.findById(itemId);
        if (item.isEmpty()) {
            return new GenericResponse("Inventory item not found with ID: " + itemId, false);
        }
        return new GenericResponse("Inventory item found successfully", true,
                InventoryItemResponse.fromInventoryItem(item.get()));
    }

    /**
     * Get inventory item by item code
     */
    public GenericResponse getInventoryItemByCode(String itemCode) {
        Optional<InventoryItem> item = inventoryItemRepository.findByItemCode(itemCode);
        if (item.isEmpty()) {
            return new GenericResponse("Inventory item not found with code: " + itemCode, false);
        }
        return new GenericResponse("Inventory item found successfully", true,
                InventoryItemResponse.fromInventoryItem(item.get()));
    }

    /**
     * Update inventory item
     */
    public GenericResponse updateInventoryItem(String itemId, UpdateInventoryItemRequest request, String updatedBy) {
        Optional<InventoryItem> optionalItem = inventoryItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            return new GenericResponse("Inventory item not found with ID: " + itemId, false);
        }

        InventoryItem item = optionalItem.get();

        if (request.getName() != null)
            item.setName(request.getName());
        if (request.getDescription() != null)
            item.setDescription(request.getDescription());
        if (request.getCategory() != null) {
            try {
                InventoryItem.Category.valueOf(request.getCategory().toUpperCase());
                item.setCategory(request.getCategory());
            } catch (IllegalArgumentException e) {
                return new GenericResponse("Invalid category: " + request.getCategory(), false);
            }
        }
        if (request.getBrand() != null)
            item.setBrand(request.getBrand());
        if (request.getModel() != null)
            item.setModel(request.getModel());
        if (request.getPartNumber() != null)
            item.setPartNumber(request.getPartNumber());
        if (request.getManufacturer() != null)
            item.setManufacturer(request.getManufacturer());
        if (request.getCostPrice() != null)
            item.setCostPrice(request.getCostPrice());
        if (request.getSellingPrice() != null)
            item.setSellingPrice(request.getSellingPrice());
        if (request.getMrp() != null)
            item.setMrp(request.getMrp());
        if (request.getCurrentStock() != null)
            item.setCurrentStock(request.getCurrentStock());
        if (request.getMinimumStock() != null)
            item.setMinimumStock(request.getMinimumStock());
        if (request.getMaximumStock() != null)
            item.setMaximumStock(request.getMaximumStock());
        if (request.getUnit() != null)
            item.setUnit(request.getUnit());
        if (request.getLocation() != null)
            item.setLocation(request.getLocation());
        if (request.getShelfNumber() != null)
            item.setShelfNumber(request.getShelfNumber());
        if (request.getSupplierName() != null)
            item.setSupplierName(request.getSupplierName());
        if (request.getSupplierContact() != null)
            item.setSupplierContact(request.getSupplierContact());
        if (request.getSupplierEmail() != null)
            item.setSupplierEmail(request.getSupplierEmail());
        if (request.getWarrantyPeriod() != null)
            item.setWarrantyPeriod(request.getWarrantyPeriod());
        if (request.getWarrantyTerms() != null)
            item.setWarrantyTerms(request.getWarrantyTerms());
        if (request.getImageUrl() != null)
            item.setImageUrl(request.getImageUrl());
        if (request.getBarcode() != null)
            item.setBarcode(request.getBarcode());
        if (request.getQrCode() != null)
            item.setQrCode(request.getQrCode());
        if (request.getNotes() != null)
            item.setNotes(request.getNotes());
        if (request.getIsActive() != null)
            item.setActive(request.getIsActive());

        item.setUpdatedBy(updatedBy);
        item.onUpdate();
        inventoryItemRepository.save(item);
        return new GenericResponse("Inventory item updated successfully", true);
    }

    /**
     * Delete inventory item
     */
    public GenericResponse deleteInventoryItem(String itemId) {
        Optional<InventoryItem> item = inventoryItemRepository.findById(itemId);
        if (item.isEmpty()) {
            return new GenericResponse("Inventory item not found with ID: " + itemId, false);
        }
        inventoryItemRepository.deleteById(itemId);
        return new GenericResponse("Inventory item deleted successfully", true);
    }

    /**
     * Adjust stock
     */
    public GenericResponse adjustStock(StockAdjustmentRequest request, String performedBy) {
        Optional<InventoryItem> optionalItem = inventoryItemRepository.findById(request.getItemId());
        if (optionalItem.isEmpty()) {
            return new GenericResponse("Inventory item not found with ID: " + request.getItemId(), false);
        }

        InventoryItem item = optionalItem.get();
        int previousStock = item.getCurrentStock();
        int newStock;

        try {
            InventoryTransaction.TransactionType transactionType = InventoryTransaction.TransactionType
                    .valueOf(request.getAdjustmentType().toUpperCase());

            switch (transactionType) {
                case IN:
                    newStock = previousStock + request.getQuantity();
                    break;
                case OUT:
                    if (previousStock < request.getQuantity()) {
                        return new GenericResponse("Insufficient stock. Available: " + previousStock + ", Requested: "
                                + request.getQuantity(), false);
                    }
                    newStock = previousStock - request.getQuantity();
                    break;
                case ADJUSTMENT:
                    newStock = request.getQuantity();
                    break;
                default:
                    return new GenericResponse("Invalid adjustment type: " + request.getAdjustmentType(), false);
            }

            // Update item stock
            item.setCurrentStock(newStock);
            item.setUpdatedBy(performedBy);
            item.onUpdate();
            inventoryItemRepository.save(item);

            // Create transaction record
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setItemId(item.getId());
            transaction.setItemCode(item.getItemCode());
            transaction.setItemName(item.getName());
            transaction.setTransactionType(transactionType);
            transaction.setQuantity(request.getQuantity());
            transaction.setUnitPrice(item.getCostPrice());
            transaction.setTotalAmount(item.getCostPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            transaction.setReferenceNumber(request.getReferenceNumber());
            transaction.setReferenceType(request.getReferenceType());
            transaction.setReason(request.getReason());
            transaction.setNotes(request.getNotes());
            transaction.setLocation(request.getLocation());
            transaction.setDestinationLocation(request.getDestinationLocation());
            transaction.setPreviousStock(BigDecimal.valueOf(previousStock));
            transaction.setNewStock(BigDecimal.valueOf(newStock));
            transaction.setPerformedBy(performedBy);
            transaction.onCreate();
            transactionRepository.save(transaction);

            return new GenericResponse("Stock adjusted successfully. New stock: " + newStock, true);

        } catch (IllegalArgumentException e) {
            return new GenericResponse("Invalid adjustment type: " + request.getAdjustmentType(), false);
        }
    }

    /**
     * Get low stock items
     */
    public GenericResponse getLowStockItems() {
        List<InventoryItem> lowStockItems = inventoryItemRepository.findLowStockItems();
        List<InventoryItemResponse> responses = lowStockItems.stream()
                .map(InventoryItemResponse::fromInventoryItem)
                .collect(Collectors.toList());
        return new GenericResponse("Low stock items retrieved successfully", true, responses);
    }

    /**
     * Get out of stock items
     */
    public GenericResponse getOutOfStockItems() {
        List<InventoryItem> outOfStockItems = inventoryItemRepository.findOutOfStockItems();
        List<InventoryItemResponse> responses = outOfStockItems.stream()
                .map(InventoryItemResponse::fromInventoryItem)
                .collect(Collectors.toList());
        return new GenericResponse("Out of stock items retrieved successfully", true, responses);
    }

    /**
     * Get inventory statistics
     */
    public InventoryStats getInventoryStats() {
        long totalItems = inventoryItemRepository.count();
        long activeItems = inventoryItemRepository.countByIsActive(true);
        long lowStockItems = inventoryItemRepository.findLowStockItems().size();
        long outOfStockItems = inventoryItemRepository.findOutOfStockItems().size();

        // Calculate total inventory value
        List<InventoryItem> allItems = inventoryItemRepository.findAll();
        BigDecimal totalValue = allItems.stream()
                .map(InventoryItem::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InventoryStats(totalItems, activeItems, lowStockItems, outOfStockItems, totalValue);
    }

    /**
     * Get items by category
     */
    public GenericResponse getItemsByCategory(String category) {
        try {
            InventoryItem.Category.valueOf(category.toUpperCase());
            List<InventoryItem> items = inventoryItemRepository.findByCategory(category);
            List<InventoryItemResponse> responses = items.stream()
                    .map(InventoryItemResponse::fromInventoryItem)
                    .collect(Collectors.toList());
            return new GenericResponse("Items retrieved successfully", true, responses);
        } catch (IllegalArgumentException e) {
            return new GenericResponse("Invalid category: " + category, false);
        }
    }

    /**
     * Get items by status
     */
    public GenericResponse getItemsByStatus(String status) {
        try {
            InventoryItem.Status itemStatus = InventoryItem.Status.valueOf(status.toUpperCase());
            List<InventoryItem> items = inventoryItemRepository.findByStatus(itemStatus);
            List<InventoryItemResponse> responses = items.stream()
                    .map(InventoryItemResponse::fromInventoryItem)
                    .collect(Collectors.toList());
            return new GenericResponse("Items retrieved successfully", true, responses);
        } catch (IllegalArgumentException e) {
            return new GenericResponse("Invalid status: " + status, false);
        }
    }

    /**
     * Get transaction history for an item
     */
    public GenericResponse getItemTransactionHistory(String itemId, LocalDateTime startDate, LocalDateTime endDate) {
        List<InventoryTransaction> transactions = transactionRepository.findItemTransactionsByDateRange(itemId,
                startDate, endDate);
        return new GenericResponse("Transaction history retrieved successfully", true, transactions);
    }

    // Helper method to create Page from List
    private Page<InventoryItem> createPageFromList(List<InventoryItem> items, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), items.size());

        if (start > items.size()) {
            return Page.empty(pageable);
        }

        return new org.springframework.data.domain.PageImpl<>(
                items.subList(start, end),
                pageable,
                items.size());
    }

    // Statistics class
    public static class InventoryStats {
        private final long totalItems;
        private final long activeItems;
        private final long lowStockItems;
        private final long outOfStockItems;
        private final BigDecimal totalValue;

        public InventoryStats(long totalItems, long activeItems, long lowStockItems, long outOfStockItems,
                BigDecimal totalValue) {
            this.totalItems = totalItems;
            this.activeItems = activeItems;
            this.lowStockItems = lowStockItems;
            this.outOfStockItems = outOfStockItems;
            this.totalValue = totalValue;
        }

        // Getters
        public long getTotalItems() {
            return totalItems;
        }

        public long getActiveItems() {
            return activeItems;
        }

        public long getLowStockItems() {
            return lowStockItems;
        }

        public long getOutOfStockItems() {
            return outOfStockItems;
        }

        public BigDecimal getTotalValue() {
            return totalValue;
        }
    }
}