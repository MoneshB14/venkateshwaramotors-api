package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.repository;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends MongoRepository<InventoryItem, String> {
    
    Optional<InventoryItem> findByItemCode(String itemCode);
    
    boolean existsByItemCode(String itemCode);
    
    List<InventoryItem> findByCategory(String category);
    
    List<InventoryItem> findByStatus(InventoryItem.Status status);
    
    List<InventoryItem> findByIsActive(boolean isActive);
    
    List<InventoryItem> findByCurrentStockLessThanEqual(int stock);
    
    List<InventoryItem> findByCurrentStockBetween(int minStock, int maxStock);
    
    @Query("{'currentStock': {$lte: '$minimumStock'}}")
    List<InventoryItem> findLowStockItems();
    
    @Query("{'currentStock': 0}")
    List<InventoryItem> findOutOfStockItems();
    
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'itemCode': {$regex: ?0, $options: 'i'}}, {'brand': {$regex: ?0, $options: 'i'}}, {'partNumber': {$regex: ?0, $options: 'i'}}]}")
    Page<InventoryItem> searchItems(String searchTerm, Pageable pageable);
    
    @Query("{'$and': [{'category': ?0}, {'$or': [{'name': {$regex: ?1, $options: 'i'}}, {'itemCode': {$regex: ?1, $options: 'i'}}, {'brand': {$regex: ?1, $options: 'i'}}]}]}")
    Page<InventoryItem> findByCategoryAndSearch(String category, String searchTerm, Pageable pageable);
    
    @Query("{'$and': [{'status': ?0}, {'$or': [{'name': {$regex: ?1, $options: 'i'}}, {'itemCode': {$regex: ?1, $options: 'i'}}, {'brand': {$regex: ?1, $options: 'i'}}]}]}")
    Page<InventoryItem> findByStatusAndSearch(InventoryItem.Status status, String searchTerm, Pageable pageable);
    
    List<InventoryItem> findBySupplierName(String supplierName);
    
    List<InventoryItem> findByLocation(String location);
    
    @Query("{'costPrice': {$gte: ?0, $lte: ?1}}")
    List<InventoryItem> findByPriceRange(double minPrice, double maxPrice);
    
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<InventoryItem> findByDateRange(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    
    @Query("{'updatedAt': {$gte: ?0}}")
    List<InventoryItem> findRecentlyUpdated(java.time.LocalDateTime since);
    
    long countByStatus(InventoryItem.Status status);
    
    long countByCategory(String category);
    
    long countByIsActive(boolean isActive);
} 