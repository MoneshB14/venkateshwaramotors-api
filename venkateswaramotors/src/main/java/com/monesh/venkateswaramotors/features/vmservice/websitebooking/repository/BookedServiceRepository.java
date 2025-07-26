package com.monesh.venkateswaramotors.features.vmservice.websitebooking.repository;

import com.monesh.venkateswaramotors.features.vmservice.websitebooking.entity.BookedService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookedServiceRepository extends MongoRepository<BookedService, String> {

    Optional<BookedService> findByBookingId(String bookingId);

    List<BookedService> findByCustomerNameContainingIgnoreCase(String customerName);

    List<BookedService> findByVehicleRegistration(String vehicleRegistration);

    List<BookedService> findByServiceType(String serviceType);

    List<BookedService> findByBookingStatus(String bookingStatus);

    List<BookedService> findByEmailSent(boolean emailSent);

    List<BookedService> findByPreferredDate(String preferredDate);
}