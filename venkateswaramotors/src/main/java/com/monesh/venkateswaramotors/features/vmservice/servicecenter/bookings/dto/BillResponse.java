package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {

    private boolean success;
    private String message;
    private Bill bill;
    private String billId;
    private String billNumber;
    
    public static BillResponse success(Bill bill, String message) {
        return BillResponse.builder()
                .success(true)
                .message(message)
                .bill(bill)
                .billId(bill.getId())
                .billNumber(bill.getBillNumber())
                .build();
    }
    
    public static BillResponse failure(String message) {
        return BillResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}