package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto;

import lombok.Data;

import java.util.List;

@Data
public class InventoryListResponse {
    
    private List<InventoryItemResponse> items;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    
    public InventoryListResponse(List<InventoryItemResponse> items, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.items = items;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
} 