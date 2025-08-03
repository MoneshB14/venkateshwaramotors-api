package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {

    Optional<Bill> findByBillNumber(String billNumber);

    List<Bill> findByBookingId(String bookingId);

    List<Bill> findByPaymentStatus(String paymentStatus);

    List<Bill> findByCreatedBy(String createdBy);

    boolean existsByBillNumber(String billNumber);
}