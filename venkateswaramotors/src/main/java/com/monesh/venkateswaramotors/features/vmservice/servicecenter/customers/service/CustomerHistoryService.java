package com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Booking;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository.BillRepository;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository.BookingRepository;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.dto.CustomerHistoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CustomerHistoryService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BillRepository billRepository;

    public CustomerHistoryResponse getHistoryByVehicleRegistration(String vehicleRegistration) {
        List<Booking> bookings = bookingRepository.findByVehicleRegistration(vehicleRegistration);

        // Gather all bills for these bookings
        List<Bill> bills = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getBookingId() != null) {
                bills.addAll(billRepository.findByBookingId(booking.getBookingId()));
            }
        }

        // Derive summary info
        int totalVisits = bookings.size();
        double totalBilledAmount = bills.stream()
                .filter(b -> b.getTotal() != null)
                .mapToDouble(Bill::getTotal)
                .sum();

        // Identify most recent booking for name/contact
        bookings.sort(Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        String customerName = bookings.isEmpty() ? null : bookings.get(0).getCustomerName();
        String contactNumber = bookings.isEmpty() ? null : bookings.get(0).getContactNumber();

        return CustomerHistoryResponse.builder()
                .vehicleRegistration(vehicleRegistration)
                .customerName(customerName)
                .contactNumber(contactNumber)
                .bookings(bookings)
                .bills(bills)
                .totalVisits(totalVisits)
                .totalBilledAmount(totalBilledAmount)
                .build();
    }
}


