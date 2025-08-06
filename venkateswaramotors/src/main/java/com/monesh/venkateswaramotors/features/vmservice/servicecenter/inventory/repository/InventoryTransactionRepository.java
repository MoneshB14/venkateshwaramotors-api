package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.repository;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends MongoRepository<InventoryTransaction, String> {
    
    List<InventoryTransaction> findByItemId(String itemId);
    
    List<InventoryTransaction> findByItemCode(String itemCode);
    
    List<InventoryTransaction> findByTransactionType(InventoryTransaction.TransactionType transactionType);
    
    List<InventoryTransaction> findByStatus(InventoryTransaction.TransactionStatus status);
    
    List<InventoryTransaction> findByPerformedBy(String performedBy);
    
    List<InventoryTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<InventoryTransaction> findByReferenceNumber(String referenceNumber);
    
    List<InventoryTransaction> findByReferenceType(String referenceType);
    
    List<InventoryTransaction> findBySupplierName(String supplierName);
    
    @Query("{'transactionDate': {$gte: ?0, $lte: ?1}}")
    Page<InventoryTransaction> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    @Query("{'$and': [{'itemId': ?0}, {'transactionDate': {$gte: ?1, $lte: ?2}}]}")
    List<InventoryTransaction> findItemTransactionsByDateRange(String itemId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'$and': [{'transactionType': ?0}, {'transactionDate': {$gte: ?1, $lte: ?2}}]}")
    List<InventoryTransaction> findTransactionsByTypeAndDateRange(InventoryTransaction.TransactionType type, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'$or': [{'itemName': {$regex: ?0, $options: 'i'}}, {'referenceNumber': {$regex: ?0, $options: 'i'}}, {'supplierName': {$regex: ?0, $options: 'i'}}]}")
    Page<InventoryTransaction> searchTransactions(String searchTerm, Pageable pageable);
    
    long countByTransactionType(InventoryTransaction.TransactionType transactionType);
    
    long countByStatus(InventoryTransaction.TransactionStatus status);
    
    long countByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
} 