package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bills")
public class Bill {

    @Id
    private String id;

    @Field("bill_number")
    private String billNumber;

    @Field("booking_id")
    private String bookingId;

    @Field("bill_date")
    private Instant billDate;

    @Field("service_charges")
    private Double serviceCharges;

    @Field("labor_charges")
    private Double laborCharges;

    @Field("parts_total")
    private Double partsTotal;

    @Field("water_wash")
    private Boolean waterWash;

    @Field("water_wash_charges")
    private Double waterWashCharges;

    @Field("water_wash_total")
    private Double waterWashTotal;

    @Field("additional_charges")
    private Double additionalCharges;

    @Field("subtotal")
    private Double subtotal;

    @Field("discount")
    private Double discount;

    @Field("discount_amount")
    private Double discountAmount;

    @Field("after_discount")
    private Double afterDiscount;

    @Field("tax_rate")
    private Double taxRate;

    @Field("tax_amount")
    private Double taxAmount;

    @Field("total")
    private Double total;

    @Field("payment_status")
    private String paymentStatus;

    @Field("work_description")
    private String workDescription;

    @Field("notes")
    private String notes;

    @Field("parts")
    private List<BillPart> parts;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    @Field("created_by")
    private String createdBy;

    @Field("updated_by")
    private String updatedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPart {
        
        @Field("id")
        private Long id;
        
        @Field("name")
        private String name;
        
        @Field("quantity")
        private Integer quantity;
        
        @Field("unit_price")
        private Double unitPrice;
        
        @Field("total")
        private Double total;
    }
}