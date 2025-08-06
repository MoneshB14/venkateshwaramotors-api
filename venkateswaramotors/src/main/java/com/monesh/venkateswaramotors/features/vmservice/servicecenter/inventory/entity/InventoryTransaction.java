package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "inventory_transactions")
public class InventoryTransaction {
    
    @Id
    private String id;
    
    @Indexed
    private String itemId;
    
    @Indexed
    private String itemCode;
    
    private String itemName;
    
    @Indexed
    private TransactionType transactionType;
    
    private int quantity;
    
    private BigDecimal unitPrice;
    
    private BigDecimal totalAmount;
    
    private String referenceNumber; // PO number, invoice number, etc.
    
    private String referenceType; // PURCHASE_ORDER, SALE, ADJUSTMENT, TRANSFER
    
    private String supplierName;
    
    private String customerName;
    
    private String reason;
    
    private String notes;
    
    private LocalDateTime transactionDate;
    
    private String performedBy;
    
    private String approvedBy;
    
    private TransactionStatus status;
    
    private String location;
    
    private String destinationLocation; // for transfers
    
    private BigDecimal previousStock;
    
    private BigDecimal newStock;
    
    // Transaction types
    public enum TransactionType {
        IN, OUT, ADJUSTMENT, TRANSFER_IN, TRANSFER_OUT, RETURN, DAMAGE
    }
    
    // Transaction status
    public enum TransactionStatus {
        PENDING, APPROVED, REJECTED, COMPLETED, CANCELLED
    }
    
    public void onCreate() {
        this.transactionDate = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }
    
    public void approve() {
        this.status = TransactionStatus.APPROVED;
    }
    
    public void reject() {
        this.status = TransactionStatus.REJECTED;
    }
    
    public void complete() {
        this.status = TransactionStatus.COMPLETED;
    }
    
    public void cancel() {
        this.status = TransactionStatus.CANCELLED;
    }
} 