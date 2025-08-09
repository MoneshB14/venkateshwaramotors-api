package com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListResponse {
    private List<CustomerListItem> customers;
    private long total;
    private int page;
    private int size;
}


