package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Slf4j
public class BillHtmlGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            .withLocale(Locale.ENGLISH)
            .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a")
            .withLocale(Locale.ENGLISH)
            .withZone(ZoneId.systemDefault());

    /**
     * Generate professional bill HTML from bill and booking data
     */
    public String generateBillHtml(Bill bill, Booking booking) {
        log.debug("Generating HTML for bill: {} and booking: {}", bill.getBillNumber(), booking.getBookingId());

        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Invoice - %s</title>
                    <style>
                        %s
                    </style>
                </head>
                <body>
                """.formatted(bill.getBillNumber(), getBillCss()));

        // Header
        html.append(generateHeader());

        // Bill Information
        html.append(generateBillInfo(bill, booking));

        // Customer Information
        html.append(generateCustomerInfo(booking));

        // Service Details
        html.append(generateServiceDetails(bill));

        // Parts Details (if any)
        if (bill.getParts() != null && !bill.getParts().isEmpty()) {
            html.append(generatePartsTable(bill));
        }

        // Bill Summary
        html.append(generateBillSummary(bill));

        // Footer
        html.append(generateFooter());

        html.append("""
                </body>
                </html>
                """);

        log.debug("Generated HTML bill with length: {} characters", html.length());
        return html.toString();
    }

    /**
     * Generate company header
     */
    private String generateHeader() {
        return """
                <div class="header">
                    <div class="company-info">
                        <h1>VENKATESWARA MOTORS</h1>
                        <p>Professional Vehicle Service Center</p>
                        <p>Contact: +91-XXXXXXXXXX | Email: info@venkateswaramotors.com</p>
                        <p>Address: Your Address Here, City, State - PIN</p>
                    </div>
                </div>
                <hr class="header-divider">
                """;
    }

    /**
     * Generate bill information section
     */
    private String generateBillInfo(Bill bill, Booking booking) {
        String billDate = bill.getBillDate() != null ? DATE_FORMATTER.format(bill.getBillDate()) : "N/A";
        String billTime = bill.getBillDate() != null ? TIME_FORMATTER.format(bill.getBillDate()) : "N/A";

        return """
                <div class="bill-info-section">
                    <div class="bill-header">
                        <h2>INVOICE</h2>
                    </div>
                    <div class="bill-details">
                        <div class="left-details">
                            <p><strong>Bill No:</strong> %s</p>
                            <p><strong>Booking ID:</strong> %s</p>
                            <p><strong>Service Type:</strong> %s</p>
                        </div>
                        <div class="right-details">
                            <p><strong>Date:</strong> %s</p>
                            <p><strong>Time:</strong> %s</p>
                            <p><strong>Payment Status:</strong> %s</p>
                        </div>
                    </div>
                </div>
                """.formatted(
                bill.getBillNumber(),
                booking.getBookingId(),
                booking.getServiceType() != null ? booking.getServiceType() : "General Service",
                billDate,
                billTime,
                bill.getPaymentStatus() != null ? bill.getPaymentStatus() : "Pending"
        );
    }

    /**
     * Generate customer information section
     */
    private String generateCustomerInfo(Booking booking) {
        return """
                <div class="customer-info">
                    <h3>Customer Details</h3>
                    <div class="customer-details">
                        <div class="left-details">
                            <p><strong>Name:</strong> %s</p>
                            <p><strong>Contact:</strong> %s</p>
                        </div>
                        <div class="right-details">
                            <p><strong>Vehicle:</strong> %s</p>
                            <p><strong>Registration:</strong> %s</p>
                        </div>
                    </div>
                </div>
                """.formatted(
                booking.getCustomerName(),
                booking.getContactNumber() != null ? booking.getContactNumber() : "N/A",
                booking.getVehicleModel() != null ? booking.getVehicleModel() : "N/A",
                booking.getVehicleRegistration() != null ? booking.getVehicleRegistration() : "N/A"
        );
    }

    /**
     * Generate service details section
     */
    private String generateServiceDetails(Bill bill) {
        StringBuilder serviceHtml = new StringBuilder();

        serviceHtml.append("""
                <div class="service-details">
                    <h3>Service Details</h3>
                """);

        if (bill.getWorkDescription() != null && !bill.getWorkDescription().trim().isEmpty()) {
            serviceHtml.append("""
                    <div class="work-description">
                        <p><strong>Work Description:</strong></p>
                        <p>%s</p>
                    </div>
                    """.formatted(bill.getWorkDescription()));
        }

        serviceHtml.append("""
                    <table class="service-table">
                        <thead>
                            <tr>
                                <th>Service Type</th>
                                <th>Description</th>
                                <th>Amount (₹)</th>
                            </tr>
                        </thead>
                        <tbody>
                """);

        // Service charges
        if (bill.getServiceCharges() != null && bill.getServiceCharges() > 0) {
            serviceHtml.append("""
                    <tr>
                        <td>Service Charges</td>
                        <td>General service charges</td>
                        <td class="amount">%.2f</td>
                    </tr>
                    """.formatted(bill.getServiceCharges()));
        }

        // Labor charges
        if (bill.getLaborCharges() != null && bill.getLaborCharges() > 0) {
            serviceHtml.append("""
                    <tr>
                        <td>Labor Charges</td>
                        <td>Labor and technician charges</td>
                        <td class="amount">%.2f</td>
                    </tr>
                    """.formatted(bill.getLaborCharges()));
        }

        // Water wash
        if (bill.getWaterWash() != null && bill.getWaterWash() && bill.getWaterWashCharges() != null && bill.getWaterWashCharges() > 0) {
            serviceHtml.append("""
                    <tr>
                        <td>Water Wash</td>
                        <td>Vehicle cleaning service</td>
                        <td class="amount">%.2f</td>
                    </tr>
                    """.formatted(bill.getWaterWashCharges()));
        }

        // Additional charges
        if (bill.getAdditionalCharges() != null && bill.getAdditionalCharges() > 0) {
            serviceHtml.append("""
                    <tr>
                        <td>Additional Charges</td>
                        <td>Other miscellaneous charges</td>
                        <td class="amount">%.2f</td>
                    </tr>
                    """.formatted(bill.getAdditionalCharges()));
        }

        serviceHtml.append("""
                        </tbody>
                    </table>
                </div>
                """);

        return serviceHtml.toString();
    }

    /**
     * Generate parts table if parts exist
     */
    private String generatePartsTable(Bill bill) {
        StringBuilder partsHtml = new StringBuilder();

        partsHtml.append("""
                <div class="parts-details">
                    <h3>Parts Used</h3>
                    <table class="parts-table">
                        <thead>
                            <tr>
                                <th>Part Name</th>
                                <th>Quantity</th>
                                <th>Unit Price (₹)</th>
                                <th>Total (₹)</th>
                            </tr>
                        </thead>
                        <tbody>
                """);

        for (Bill.BillPart part : bill.getParts()) {
            partsHtml.append("""
                    <tr>
                        <td>%s</td>
                        <td>%d</td>
                        <td class="amount">%.2f</td>
                        <td class="amount">%.2f</td>
                    </tr>
                    """.formatted(
                    part.getName() != null ? part.getName() : "N/A",
                    part.getQuantity() != null ? part.getQuantity() : 0,
                    part.getUnitPrice() != null ? part.getUnitPrice() : 0.0,
                    part.getTotal() != null ? part.getTotal() : 0.0
            ));
        }

        partsHtml.append("""
                        </tbody>
                        <tfoot>
                            <tr class="parts-total">
                                <td colspan="3"><strong>Parts Total</strong></td>
                                <td class="amount"><strong>₹ %.2f</strong></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
                """.formatted(bill.getPartsTotal() != null ? bill.getPartsTotal() : 0.0));

        return partsHtml.toString();
    }

    /**
     * Generate bill summary section
     */
    private String generateBillSummary(Bill bill) {
        return """
                <div class="bill-summary">
                    <h3>Bill Summary</h3>
                    <table class="summary-table">
                        <tr>
                            <td>Subtotal</td>
                            <td class="amount">₹ %.2f</td>
                        </tr>
                        %s
                        %s
                        %s
                        <tr class="total-row">
                            <td><strong>TOTAL AMOUNT</strong></td>
                            <td class="amount"><strong>₹ %.2f</strong></td>
                        </tr>
                    </table>
                    %s
                </div>
                """.formatted(
                bill.getSubtotal() != null ? bill.getSubtotal() : 0.0,
                generateDiscountRow(bill),
                generateAfterDiscountRow(bill),
                generateTaxRow(bill),
                bill.getTotal() != null ? bill.getTotal() : 0.0,
                generateNotesSection(bill)
        );
    }

    private String generateDiscountRow(Bill bill) {
        if (bill.getDiscount() != null && bill.getDiscount() > 0) {
            return """
                    <tr>
                        <td>Discount (%.1f%%)</td>
                        <td class="amount">- ₹ %.2f</td>
                    </tr>
                    """.formatted(
                    bill.getDiscount(),
                    bill.getDiscountAmount() != null ? bill.getDiscountAmount() : 0.0
            );
        }
        return "";
    }

    private String generateAfterDiscountRow(Bill bill) {
        if (bill.getDiscount() != null && bill.getDiscount() > 0) {
            return """
                    <tr>
                        <td>After Discount</td>
                        <td class="amount">₹ %.2f</td>
                    </tr>
                    """.formatted(bill.getAfterDiscount() != null ? bill.getAfterDiscount() : 0.0);
        }
        return "";
    }

    private String generateTaxRow(Bill bill) {
        if (bill.getTaxRate() != null && bill.getTaxRate() > 0) {
            return """
                    <tr>
                        <td>Tax (%.1f%%)</td>
                        <td class="amount">₹ %.2f</td>
                    </tr>
                    """.formatted(
                    bill.getTaxRate(),
                    bill.getTaxAmount() != null ? bill.getTaxAmount() : 0.0
            );
        }
        return "";
    }

    private String generateNotesSection(Bill bill) {
        if (bill.getNotes() != null && !bill.getNotes().trim().isEmpty()) {
            return """
                    <div class="notes-section">
                        <p><strong>Notes:</strong></p>
                        <p>%s</p>
                    </div>
                    """.formatted(bill.getNotes());
        }
        return "";
    }

    /**
     * Generate footer
     */
    private String generateFooter() {
        return """
                <div class="footer">
                    <hr>
                    <p class="footer-text">Thank you for choosing Venkateswara Motors!</p>
                    <p class="footer-text">For any queries, please contact us at the above mentioned details.</p>
                    <p class="footer-small">This is a computer generated bill.</p>
                </div>
                """;
    }

    /**
     * Get comprehensive CSS for bill styling
     */
    private String getBillCss() {
        return """
                @page {
                    size: A4;
                    margin: 0.75in;
                }
                
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                
                body {
                    font-family: Arial, sans-serif;
                    font-size: 12px;
                    line-height: 1.4;
                    color: #333;
                }
                
                .header {
                    text-align: center;
                    margin-bottom: 20px;
                }
                
                .company-info h1 {
                    font-size: 24px;
                    font-weight: bold;
                    color: #2c3e50;
                    margin-bottom: 5px;
                }
                
                .company-info p {
                    margin: 2px 0;
                    font-size: 11px;
                }
                
                .header-divider {
                    border: none;
                    border-top: 2px solid #2c3e50;
                    margin: 15px 0;
                }
                
                .bill-info-section {
                    margin-bottom: 20px;
                }
                
                .bill-header h2 {
                    text-align: center;
                    font-size: 18px;
                    color: #2c3e50;
                    margin-bottom: 15px;
                    text-decoration: underline;
                }
                
                .bill-details, .customer-details {
                    display: table;
                    width: 100%;
                }
                
                .left-details, .right-details {
                    display: table-cell;
                    width: 50%;
                    vertical-align: top;
                }
                
                .right-details {
                    text-align: right;
                }
                
                .customer-info, .service-details, .parts-details, .bill-summary {
                    margin: 20px 0;
                }
                
                .customer-info h3, .service-details h3, .parts-details h3, .bill-summary h3 {
                    font-size: 14px;
                    color: #2c3e50;
                    border-bottom: 1px solid #ddd;
                    padding-bottom: 5px;
                    margin-bottom: 10px;
                }
                
                .work-description {
                    background-color: #f8f9fa;
                    padding: 10px;
                    border-radius: 4px;
                    margin-bottom: 15px;
                }
                
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin: 10px 0;
                }
                
                th, td {
                    padding: 8px;
                    text-align: left;
                    border: 1px solid #ddd;
                }
                
                th {
                    background-color: #f8f9fa;
                    font-weight: bold;
                    font-size: 11px;
                }
                
                .amount {
                    text-align: right;
                    font-family: monospace;
                }
                
                .service-table th, .parts-table th {
                    background-color: #e9ecef;
                }
                
                .parts-total td {
                    background-color: #f8f9fa;
                    font-weight: bold;
                }
                
                .summary-table {
                    width: 60%;
                    margin-left: auto;
                    border: 2px solid #2c3e50;
                }
                
                .summary-table td {
                    padding: 10px;
                    font-size: 12px;
                }
                
                .total-row td {
                    background-color: #2c3e50;
                    color: white;
                    font-size: 14px;
                    font-weight: bold;
                }
                
                .notes-section {
                    margin-top: 15px;
                    padding: 10px;
                    background-color: #fff3cd;
                    border-radius: 4px;
                }
                
                .footer {
                    margin-top: 30px;
                    text-align: center;
                }
                
                .footer hr {
                    border: none;
                    border-top: 1px solid #ddd;
                    margin-bottom: 10px;
                }
                
                .footer-text {
                    margin: 5px 0;
                    font-size: 11px;
                }
                
                .footer-small {
                    font-size: 9px;
                    color: #666;
                    margin-top: 10px;
                }
                
                p {
                    margin: 3px 0;
                }
                """;
    }
}
