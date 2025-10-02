package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    Optional<Booking> findByBookingId(String bookingId);

    List<Booking> findByPreferredDate(String preferredDate);

    List<Booking> findByBookingStatus(String bookingStatus);

    List<Booking> findByAssignedTechnician(String assignedTechnician);

    @Query("{'customer_name': {$regex: ?0, $options: 'i'}}")
    List<Booking> findByCustomerNameContainingIgnoreCase(String customerName);

    @Query("{'vehicle_registration': {$regex: ?0, $options: 'i'}}")
    List<Booking> findByVehicleRegistrationContainingIgnoreCase(String vehicleRegistration);

    List<Booking> findByVehicleRegistration(String vehicleRegistration);

    @Query("{'preferred_date': ?0, 'preferred_time': ?1}")
    List<Booking> findByPreferredDateAndPreferredTime(String preferredDate, String preferredTime);

    Page<Booking> findByBookingStatusOrderByCreatedAtDesc(String bookingStatus, Pageable pageable);

    Page<Booking> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("{'preferred_date': {$gte: ?0}}")
    List<Booking> findUpcomingBookings(String fromDate);

    @Query("{'preferred_date': ?0, 'preferred_time': ?1, 'booking_status': {$ne: 'CANCELLED'}}")
    List<Booking> findActiveBookingsByDateTime(String preferredDate, String preferredTime);

    List<Booking> findByServiceType(String serviceType);

    List<Booking> findByPreferredDateBetween(String fromDate, String toDate);

    @Query("{'preferred_date': ?0, 'booking_status': ?1}")
    List<Booking> findByPreferredDateAndBookingStatus(String preferredDate, String bookingStatus);

    long countByBookingStatus(String bookingStatus);
} 