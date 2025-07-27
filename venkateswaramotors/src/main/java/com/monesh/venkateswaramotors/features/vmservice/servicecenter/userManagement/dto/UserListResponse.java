package com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserListResponse {
    
    private List<UserResponse> users;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    
    public UserListResponse(List<UserResponse> users, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.users = users;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
} 