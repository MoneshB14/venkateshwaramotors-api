package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "booked_services")
public class Booking {

    @Id
    private String id;

    @Field("booking_id")
    private String bookingId;

    @Field("customer_name")
    private String customerName;

    @Field("contact_number")
    private String contactNumber;

    @Field("vehicle_registration")
    private String vehicleRegistration;

    @Field("vehicle_model")
    private String vehicleModel;

    @Field("service_type")
    private String serviceType;

    @Field("preferred_date")
    private String preferredDate;

    @Field("preferred_time")
    private String preferredTime;

    @Field("booking_status")
    private String bookingStatus;

    @Field("assigned_technician")
    private String assignedTechnician;

    @Field("estimated_cost")
    private String estimatedCost;

    @Field("notes")
    private String notes;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    @Field("email_sent")
    private boolean emailSent;

    @Field("email_sent_at")
    private Instant emailSentAt;

    @Field("created_by")
    private String createdBy;

    @Field("updated_by")
    private String updatedBy;
} 