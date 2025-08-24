package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.EmailWithPdfRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Booking;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository.BookingRepository;
import com.monesh.venkateswaramotors.global.dto.EmailResponse;
import com.monesh.venkateswaramotors.global.dto.EmailFileUploadResponse;
import com.monesh.venkateswaramotors.global.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailPdfService {

    private final PdfService pdfService;
    private final EmailService emailService;
    private final BillService billService;
    private final BillHtmlGenerator billHtmlGenerator;
    private final BookingRepository bookingRepository;

    /**
     * Process email with PDF request - generates PDF from HTML and sends email with attachment
     * 
     * @param request EmailWithPdfRequest containing email details and HTML content
     * @return Mono<EmailResponse> with success status
     */
    public Mono<EmailResponse> sendEmailWithPdf(EmailWithPdfRequest request) {
        try {
            log.info("Processing email with PDF request for customer: {} to email: {}", 
                    request.getCustomerName(), request.getEmailId());

            // Get HTML content - either provided or generated from bill data
            String htmlContent = getOrGenerateHtmlContent(request);
            
            if (htmlContent == null || htmlContent.trim().isEmpty()) {
                log.warn("No HTML content available for bill ID: {}", request.getBillId());
                return Mono.just(EmailResponse.builder()
                        .success(false)
                        .message("Unable to generate bill content")
                        .build());
            }

            // Validate HTML content
            if (!pdfService.isValidHtml(htmlContent)) {
                log.warn("Invalid HTML content for bill ID: {}", request.getBillId());
                return Mono.just(EmailResponse.builder()
                        .success(false)
                        .message("Invalid HTML content")
                        .build());
            }

            log.debug("HTML content validation passed for bill ID: {}", request.getBillId());

            // Generate PDF from HTML
            byte[] pdfBytes = generatePdfFromHtml(htmlContent, request);
            log.info("PDF generated successfully for bill ID: {} with size: {} bytes", 
                    request.getBillId(), pdfBytes.length);

            // Prepare email content and subject
            String subject = prepareEmailSubject(request);
            String emailHtmlContent = prepareEmailContent(request);
            
            // Prepare filename
            String filename = prepareFilename(request);
            
            log.debug("Sending email request to: {} with file attachment: {}", 
                    request.getEmailId(), filename);

            // Send email with file upload (new API)
            return emailService.sendEmailWithFileUpload(
                    request.getEmailId(),
                    subject,
                    emailHtmlContent,
                    filename,
                    pdfBytes,
                    "application/pdf"
            ).map(this::convertToEmailResponse)
             .doOnSuccess(response -> {
                 if (response.isSuccess()) {
                     log.info("Email with PDF sent successfully to: {} for bill ID: {}", 
                             request.getEmailId(), request.getBillId());
                 } else {
                     log.warn("Email service returned unsuccessful response: {} for bill ID: {}", 
                             response.getMessage(), request.getBillId());
                 }
             })
             .doOnError(error -> log.error("Failed to send email with PDF for bill ID: {}, Error: {}", 
                     request.getBillId(), error.getMessage()))
             .onErrorReturn(EmailResponse.builder()
                     .success(false)
                     .message("Email service error: Failed to send email")
                     .build());

        } catch (Exception e) {
            log.error("Error processing email with PDF for bill ID: {} - Error: {}", 
                    request.getBillId(), e.getMessage(), e);
            return Mono.just(EmailResponse.builder()
                    .success(false)
                    .message("Failed to process email with PDF: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Get HTML content - either from request or generate from bill data
     */
    private String getOrGenerateHtmlContent(EmailWithPdfRequest request) {
        try {
            // If HTML content is provided, use it
            if (request.getHtmlContent() != null && !request.getHtmlContent().trim().isEmpty()) {
                log.debug("Using provided HTML content for bill ID: {}", request.getBillId());
                return request.getHtmlContent();
            }

            // Otherwise, generate HTML from bill data
            log.info("Generating HTML content from bill data for bill ID: {}", request.getBillId());
            return generateHtmlFromBillData(request.getBillId());

        } catch (Exception e) {
            log.error("Error getting HTML content for bill ID: {}", request.getBillId(), e);
            return null;
        }
    }

    /**
     * Generate HTML content from bill data using bill ID
     */
    private String generateHtmlFromBillData(String billId) throws Exception {
        log.debug("Fetching bill data for bill ID: {}", billId);

        // Get bill data from service
        var billResponse = billService.getBillById(billId);
        
        if (!billResponse.isSuccess() || billResponse.getBill() == null) {
            throw new Exception("Bill not found with ID: " + billId);
        }

        Bill bill = billResponse.getBill();
        log.debug("Retrieved bill: {} for booking: {}", bill.getBillNumber(), bill.getBookingId());

        // Get booking data for customer details
        var bookingOptional = bookingRepository.findByBookingId(bill.getBookingId());
        
        if (bookingOptional.isEmpty()) {
            throw new Exception("Booking not found with ID: " + bill.getBookingId());
        }

        Booking booking = bookingOptional.get();
        log.debug("Retrieved booking for customer: {}", booking.getCustomerName());

        // Generate HTML using the template generator
        String html = billHtmlGenerator.generateBillHtml(bill, booking);
        log.debug("Generated HTML content with length: {} characters", html.length());

        return html;
    }

    /**
     * Generate PDF from HTML content with optional custom CSS
     */
    private byte[] generatePdfFromHtml(String htmlContent, EmailWithPdfRequest request) throws Exception {
        log.debug("Generating PDF for bill ID: {}", request.getBillId());
        
        try {
            // Validate and clean HTML content first
            String cleanedHtml = validateAndCleanHtmlContent(htmlContent);
            
            byte[] pdfBytes;
            if (request.getCustomCss() != null && !request.getCustomCss().trim().isEmpty()) {
                pdfBytes = pdfService.convertHtmlToPdfWithStyling(cleanedHtml, request.getCustomCss());
            } else {
                pdfBytes = pdfService.convertHtmlToPdf(cleanedHtml);
            }
            
            // Validate the generated PDF
            if (pdfBytes == null || pdfBytes.length == 0) {
                throw new Exception("Generated PDF is empty or null");
            }
            
            // Basic PDF header validation
            if (!isValidPdfBytes(pdfBytes)) {
                throw new Exception("Generated content is not a valid PDF document");
            }
            
            log.debug("Successfully generated PDF for bill ID: {} with size: {} bytes", 
                    request.getBillId(), pdfBytes.length);
            
            return pdfBytes;
            
        } catch (Exception e) {
            log.error("Failed to generate PDF for bill ID: {} - Error: {}", request.getBillId(), e.getMessage(), e);
            throw new Exception("PDF generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Validate and clean HTML content for PDF generation
     */
    private String validateAndCleanHtmlContent(String htmlContent) throws Exception {
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            throw new Exception("HTML content is null or empty");
        }
        
        log.debug("Original HTML content length: {} characters", htmlContent.length());
        log.debug("Original HTML preview: {}", htmlContent.length() > 200 ? 
                htmlContent.substring(0, 200) + "..." : htmlContent);
        
        // Clean the HTML content using PdfService
        String cleanedHtml = pdfService.cleanHtmlForPdf(htmlContent);
        
        // Ensure it has proper HTML structure
        if (!cleanedHtml.contains("<html") && !cleanedHtml.contains("<body")) {
            log.debug("Adding HTML structure wrapper to content");
            cleanedHtml = "<html><head><title>Bill</title></head><body>" + cleanedHtml + "</body></html>";
        }
        
        // Additional validation - check for problematic content
        if (cleanedHtml.contains("<!DOCTYPE")) {
            log.debug("HTML contains DOCTYPE declaration");
        }
        
        if (cleanedHtml.contains("javascript:") || cleanedHtml.contains("<script")) {
            log.warn("HTML contains JavaScript content which may cause PDF generation issues");
        }
        
        // Log the final cleaned HTML for debugging
        log.debug("Cleaned HTML content length: {} characters", cleanedHtml.length());
        log.debug("Cleaned HTML preview: {}", cleanedHtml.length() > 200 ? 
                cleanedHtml.substring(0, 200) + "..." : cleanedHtml);
        
        return cleanedHtml;
    }

    /**
     * Validate that the byte array represents a valid PDF
     */
    private boolean isValidPdfBytes(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length < 4) {
            return false;
        }
        
        // Check for PDF signature (%PDF)
        return pdfBytes[0] == 0x25 && pdfBytes[1] == 0x50 && pdfBytes[2] == 0x44 && pdfBytes[3] == 0x46;
    }

    /**
     * Prepare filename for PDF attachment
     */
    private String prepareFilename(EmailWithPdfRequest request) {
        String filename = request.getFilename() != null && !request.getFilename().trim().isEmpty() 
                ? request.getFilename() 
                : "bill_" + request.getBillId() + ".pdf";

        // Ensure .pdf extension
        if (!filename.toLowerCase().endsWith(".pdf")) {
            filename += ".pdf";
        }

        log.debug("Using filename for PDF attachment: {}", filename);
        return filename;
    }

    /**
     * Convert EmailFileUploadResponse to EmailResponse for compatibility
     */
    private EmailResponse convertToEmailResponse(EmailFileUploadResponse fileUploadResponse) {
        return EmailResponse.builder()
                .success(fileUploadResponse.isSuccess())
                .message(fileUploadResponse.getMessage())
                .messageId(fileUploadResponse.getMessageId())
                .build();
    }

    /**
     * Prepare email subject with fallback to default
     */
    private String prepareEmailSubject(EmailWithPdfRequest request) {
        if (request.getSubject() != null && !request.getSubject().trim().isEmpty()) {
            return request.getSubject();
        }
        return "Bill for " + request.getCustomerName() + " - " + request.getBillId();
    }

    /**
     * Prepare professional email content template
     */
    private String prepareEmailContent(EmailWithPdfRequest request) {
        return "<h2>Dear " + request.getCustomerName() + ",</h2>" +
               "<p>Please find your bill attached as a PDF document.</p>" +
               "<p>Bill ID: " + request.getBillId() + "</p>" +
               "<p>Thank you for choosing Venkateswara Motors!</p>" +
               "<br/><p>Best regards,<br/>Venkateswara Motors Team</p>";
    }

    /**
     * Validate email request before processing
     */
    public boolean isValidEmailRequest(EmailWithPdfRequest request) {
        if (request == null) {
            log.warn("Email request is null");
            return false;
        }

        if (request.getEmailId() == null || request.getEmailId().trim().isEmpty()) {
            log.warn("Email ID is missing");
            return false;
        }

        if (request.getBillId() == null || request.getBillId().trim().isEmpty()) {
            log.warn("Bill ID is missing");
            return false;
        }

        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            log.warn("Customer name is missing");
            return false;
        }

        // HTML content is now optional - will be generated from bill data if not provided
        // So we don't need to validate it here

        return true;
    }

    /**
     * Test PDF generation with simple HTML content for debugging
     */
    public byte[] testPdfGeneration() throws Exception {
        String testHtml = """
                <html>
                <head>
                    <title>Test Bill</title>
                </head>
                <body>
                    <h1>Test Bill</h1>
                    <p>This is a test bill document.</p>
                    <table>
                        <tr>
                            <th>Item</th>
                            <th>Quantity</th>
                            <th>Price</th>
                        </tr>
                        <tr>
                            <td>Service</td>
                            <td>1</td>
                            <td>₹100</td>
                        </tr>
                    </table>
                    <p><strong>Total: ₹100</strong></p>
                </body>
                </html>
                """;
        
        log.info("Testing PDF generation with simple HTML");
        
        try {
            byte[] pdfBytes = pdfService.convertHtmlToPdf(testHtml);
            
            if (!isValidPdfBytes(pdfBytes)) {
                throw new Exception("Generated test PDF is not valid");
            }
            
            log.info("Test PDF generated successfully with size: {} bytes", pdfBytes.length);
            return pdfBytes;
            
        } catch (Exception e) {
            log.error("Test PDF generation failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
