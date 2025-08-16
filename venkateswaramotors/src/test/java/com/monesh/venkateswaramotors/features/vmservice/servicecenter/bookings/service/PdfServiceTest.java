package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfServiceTest {

    @Autowired
    private PdfService pdfService;

    @Test
    void testConvertHtmlToPdf() throws Exception {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Test Bill</title>
            </head>
            <body>
                <h1>Test Bill</h1>
                <p>This is a test bill for PDF generation.</p>
                <table>
                    <tr>
                        <td>Item</td>
                        <td>Price</td>
                    </tr>
                    <tr>
                        <td>Service</td>
                        <td>₹1000.00</td>
                    </tr>
                </table>
            </body>
            </html>
            """;

        byte[] pdfBytes = pdfService.convertHtmlToPdf(htmlContent);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        
        // Check if the generated PDF starts with PDF header
        String pdfHeader = new String(pdfBytes, 0, 4);
        assertEquals("%PDF", pdfHeader);
    }

    @Test
    void testIsValidHtml() {
        assertTrue(pdfService.isValidHtml("<html><body><h1>Valid HTML</h1></body></html>"));
        assertTrue(pdfService.isValidHtml("<div>Simple div</div>"));
        assertFalse(pdfService.isValidHtml(null));
        assertFalse(pdfService.isValidHtml(""));
        assertFalse(pdfService.isValidHtml("   "));
    }

    @Test
    void testCleanHtmlForPdf() {
        String htmlWithScript = "<html><body><script>alert('test');</script><h1>Title</h1></body></html>";
        String cleaned = pdfService.cleanHtmlForPdf(htmlWithScript);
        
        assertFalse(cleaned.contains("<script>"));
        assertTrue(cleaned.contains("<h1>Title</h1>"));
    }
}
