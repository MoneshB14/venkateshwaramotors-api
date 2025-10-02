package com.monesh.venkateswaramotors.features.vmservice.servicecenter.dashboard.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Booking;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository.BillRepository;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository.BookingRepository;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.dashboard.dto.DashboardResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.InventoryItem;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final BillRepository billRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public DashboardResponse getDashboardData() {
        log.info("Fetching dashboard data");

        try {
            DashboardResponse.KPICards kpiCards = getKPICards();
            List<DashboardResponse.DailyIncomeChart> dailyIncomeChart = getDailyIncomeChart();
            List<DashboardResponse.ServiceTypeChart> serviceTypeChart = getServiceTypeChart();
            List<DashboardResponse.RecentActivity> recentActivity = getRecentActivity();
            DashboardResponse.InventoryAnalytics inventoryAnalytics = getInventoryAnalytics();

            return DashboardResponse.builder()
                    .kpiCards(kpiCards)
                    .dailyIncomeChart(dailyIncomeChart)
                    .serviceTypeChart(serviceTypeChart)
                    .recentActivity(recentActivity)
                    .inventoryAnalytics(inventoryAnalytics)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching dashboard data", e);
            throw new RuntimeException("Failed to fetch dashboard data", e);
        }
    }

    private DashboardResponse.KPICards getKPICards() {
        log.info("Calculating KPI cards");

        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Today's completed services
        List<Booking> todaysCompletedBookings = bookingRepository.findByPreferredDateAndBookingStatus(todayStr,
                "COMPLETED");
        Long todaysCompletedServices = (long) todaysCompletedBookings.size();

        // Today's income
        Double todaysIncome = calculateTodaysIncome(todaysCompletedBookings);

        // Total pending bookings
        Long totalPendingBookings = bookingRepository.countByBookingStatus("PENDING");

        // Total completed bookings
        Long totalCompletedBookings = bookingRepository.countByBookingStatus("COMPLETED");

        return DashboardResponse.KPICards.builder()
                .todaysIncome(todaysIncome)
                .todaysCompletedServices(todaysCompletedServices)
                .totalPendingBookings(totalPendingBookings)
                .totalCompletedBookings(totalCompletedBookings)
                .build();
    }

    private Double calculateTodaysIncome(List<Booking> todaysCompletedBookings) {
        if (todaysCompletedBookings.isEmpty()) {
            return 0.0;
        }

        List<String> bookingIds = todaysCompletedBookings.stream()
                .map(Booking::getBookingId)
                .collect(Collectors.toList());

        List<Bill> bills = billRepository.findByBookingIdIn(bookingIds);

        return bills.stream()
                .mapToDouble(bill -> bill.getTotal() != null ? bill.getTotal() : 0.0)
                .sum();
    }

    private List<DashboardResponse.DailyIncomeChart> getDailyIncomeChart() {
        log.info("Calculating daily income chart for past 7 days");

        List<DashboardResponse.DailyIncomeChart> dailyIncomeData = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            List<Booking> completedBookings = bookingRepository.findByPreferredDateAndBookingStatus(dateStr,
                    "COMPLETED");
            Long serviceCount = (long) completedBookings.size();

            Double income = calculateTodaysIncome(completedBookings);

            dailyIncomeData.add(DashboardResponse.DailyIncomeChart.builder()
                    .date(dateStr)
                    .income(income)
                    .serviceCount(serviceCount)
                    .build());
        }

        return dailyIncomeData;
    }

    private List<DashboardResponse.ServiceTypeChart> getServiceTypeChart() {
        log.info("Calculating service type chart");

        List<Booking> completedBookings = bookingRepository.findByBookingStatus("COMPLETED");

        Map<String, Long> serviceTypeCounts = completedBookings.stream()
                .collect(Collectors.groupingBy(
                        Booking::getServiceType,
                        Collectors.counting()));

        long totalCount = completedBookings.size();

        return serviceTypeCounts.entrySet().stream()
                .map(entry -> {
                    String serviceType = entry.getKey();
                    Long count = entry.getValue();
                    Double percentage = totalCount > 0 ? (count.doubleValue() / totalCount) * 100 : 0.0;

                    return DashboardResponse.ServiceTypeChart.builder()
                            .serviceType(serviceType)
                            .count(count)
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }

    private List<DashboardResponse.RecentActivity> getRecentActivity() {
        log.info("Fetching recent activity - last 3 bookings");

        List<Booking> recentBookings = bookingRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();

        return recentBookings.stream()
                .map(booking -> DashboardResponse.RecentActivity.builder()
                        .bookingId(booking.getBookingId())
                        .customerName(booking.getCustomerName())
                        .vehicleRegistration(booking.getVehicleRegistration())
                        .serviceType(booking.getServiceType())
                        .bookingStatus(booking.getBookingStatus())
                        .preferredDate(booking.getPreferredDate())
                        .preferredTime(booking.getPreferredTime())
                        .assignedTechnician(booking.getAssignedTechnician())
                        .estimatedCost(booking.getEstimatedCost())
                        .createdAt(booking.getCreatedAt() != null ? booking.getCreatedAt().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private DashboardResponse.InventoryAnalytics getInventoryAnalytics() {
        log.info("Calculating inventory analytics");

        List<InventoryItem> allItems = inventoryItemRepository.findByIsActive(true);
        Long totalItemsCount = (long) allItems.size();

        // Stock level breakdowns
        List<DashboardResponse.InventoryAnalytics.StockLevelBreakdown> stockBreakdowns = calculateStockLevelBreakdowns(
                allItems);

        // Category distributions
        List<DashboardResponse.InventoryAnalytics.CategoryDistribution> categoryDistributions = calculateCategoryDistributions(
                allItems);

        // Total inventory value
        Double totalInventoryValue = allItems.stream()
                .mapToDouble(item -> item.getTotalValue() != null ? item.getTotalValue().doubleValue() : 0.0)
                .sum();

        return DashboardResponse.InventoryAnalytics.builder()
                .totalItemsCount(totalItemsCount)
                .stockLevelBreakdowns(stockBreakdowns)
                .categoryDistributions(categoryDistributions)
                .totalInventoryValue(totalInventoryValue)
                .build();
    }

    private List<DashboardResponse.InventoryAnalytics.StockLevelBreakdown> calculateStockLevelBreakdowns(
            List<InventoryItem> items) {
        long totalItems = items.size();

        long outOfStock = items.stream().mapToLong(item -> item.isOutOfStock() ? 1 : 0).sum();
        long lowStock = items.stream().mapToLong(item -> item.isLowStock() && !item.isOutOfStock() ? 1 : 0).sum();
        long inStock = totalItems - outOfStock - lowStock;

        List<DashboardResponse.InventoryAnalytics.StockLevelBreakdown> breakdowns = new ArrayList<>();

        if (totalItems > 0) {
            breakdowns.add(DashboardResponse.InventoryAnalytics.StockLevelBreakdown.builder()
                    .level("Out of Stock")
                    .count(outOfStock)
                    .percentage(Math.round((outOfStock * 100.0 / totalItems) * 100.0) / 100.0)
                    .build());

            breakdowns.add(DashboardResponse.InventoryAnalytics.StockLevelBreakdown.builder()
                    .level("Low Stock")
                    .count(lowStock)
                    .percentage(Math.round((lowStock * 100.0 / totalItems) * 100.0) / 100.0)
                    .build());

            breakdowns.add(DashboardResponse.InventoryAnalytics.StockLevelBreakdown.builder()
                    .level("In Stock")
                    .count(inStock)
                    .percentage(Math.round((inStock * 100.0 / totalItems) * 100.0) / 100.0)
                    .build());
        }

        return breakdowns;
    }

    private List<DashboardResponse.InventoryAnalytics.CategoryDistribution> calculateCategoryDistributions(
            List<InventoryItem> items) {
        Map<String, List<InventoryItem>> categoryGroups = items.stream()
                .collect(Collectors.groupingBy(InventoryItem::getCategory));

        long totalItems = items.size();

        return categoryGroups.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    List<InventoryItem> categoryItems = entry.getValue();
                    Long count = (long) categoryItems.size();
                    Double percentage = totalItems > 0 ? (count.doubleValue() / totalItems) * 100 : 0.0;

                    Double value = categoryItems.stream()
                            .mapToDouble(
                                    item -> item.getTotalValue() != null ? item.getTotalValue().doubleValue() : 0.0)
                            .sum();

                    return DashboardResponse.InventoryAnalytics.CategoryDistribution.builder()
                            .category(category)
                            .count(count)
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .value(value)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
}
