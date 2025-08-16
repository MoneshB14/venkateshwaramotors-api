package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class PdfService {

    /**
     * Convert HTML content to PDF
     */
    public byte[] convertHtmlToPdf(String htmlContent) throws Exception {
        try {
            log.info("Starting HTML to PDF conversion");

            // Clean and parse HTML
            String cleanedHtml = cleanHtmlForPdf(htmlContent);
            Document document = Jsoup.parse(cleanedHtml);

            // Add default PDF styles
            String defaultCss = getDefaultPdfStyles();
            document.head().appendElement("style").text(defaultCss);

            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            // Convert to W3C Document
            W3CDom w3cDom = new W3CDom();
            org.w3c.dom.Document w3cDocument = w3cDom.fromJsoup(document);

            // Create PDF with proper configuration
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withW3cDocument(w3cDocument, null);
                builder.toStream(outputStream);

                // Configure builder to handle font issues
                builder.useFastMode();
                builder.useDefaultPageSize(210, 297, PdfRendererBuilder.PageSizeUnits.MM);

                builder.run();

                byte[] pdfBytes = outputStream.toByteArray();
                log.info("PDF conversion completed successfully. Size: {} bytes", pdfBytes.length);
                return pdfBytes;
            }

        } catch (Exception e) {
            log.error("Error converting HTML to PDF", e);
            throw new Exception("Failed to convert HTML to PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Convert HTML content to PDF with custom styling
     */
    public byte[] convertHtmlToPdfWithStyling(String htmlContent, String customCss) throws Exception {
        try {
            log.info("Starting HTML to PDF conversion with custom styling");

            // Clean HTML first
            String cleanedHtml = cleanHtmlForPdf(htmlContent);
            Document document = Jsoup.parse(cleanedHtml);

            // Add default PDF-friendly styles first
            String defaultCss = getDefaultPdfStyles();
            document.head().appendElement("style").text(defaultCss);

            // Add custom CSS if provided
            if (customCss != null && !customCss.trim().isEmpty()) {
                String cleanedCss = cleanCssForPdf(customCss);
                document.head().appendElement("style").text(cleanedCss);
            }

            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            // Convert to W3C Document
            W3CDom w3cDom = new W3CDom();
            org.w3c.dom.Document w3cDocument = w3cDom.fromJsoup(document);

            // Create PDF with proper configuration
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withW3cDocument(w3cDocument, null);
                builder.toStream(outputStream);

                // Configure builder to handle font issues
                builder.useFastMode();
                builder.useDefaultPageSize(210, 297, PdfRendererBuilder.PageSizeUnits.MM);

                builder.run();

                byte[] pdfBytes = outputStream.toByteArray();
                log.info("PDF conversion with styling completed successfully. Size: {} bytes", pdfBytes.length);
                return pdfBytes;
            }

        } catch (Exception e) {
            log.error("Error converting HTML to PDF with styling", e);
            throw new Exception("Failed to convert HTML to PDF with styling: " + e.getMessage(), e);
        }
    }

    /**
     * Get default PDF-friendly CSS styles
     */
    private String getDefaultPdfStyles() {
        return """
                @page {
                    size: A4;
                    margin: 1in;
                }

                body {
                    font-family: Arial, sans-serif;
                    font-size: 12px;
                    line-height: 1.4;
                    color: #333;
                    margin: 0;
                    padding: 0;
                }

                h1, h2, h3, h4, h5, h6 {
                    color: #2c3e50;
                    margin: 0.5em 0;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin: 1em 0;
                }

                th, td {
                    padding: 8px;
                    text-align: left;
                    border: 1px solid #ddd;
                }

                th {
                    background-color: #f8f9fa;
                    font-weight: bold;
                }

                .bill-header {
                    text-align: center;
                    margin-bottom: 2em;
                }

                .bill-info {
                    margin: 1em 0;
                }

                .bill-total {
                    font-weight: bold;
                    font-size: 14px;
                }

                .text-right {
                    text-align: right;
                }

                .text-center {
                    text-align: center;
                }

                .mt-2 {
                    margin-top: 1em;
                }

                .mb-2 {
                    margin-bottom: 1em;
                }
                """;
    }

    /**
     * Validate HTML content before conversion
     */
    public boolean isValidHtml(String htmlContent) {
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            log.warn("HTML content is null or empty");
            return false;
        }

        try {
            Jsoup.parse(htmlContent);
            return true;
        } catch (Exception e) {
            log.error("Invalid HTML content", e);
            return false;
        }
    }

    /**
     * Clean HTML content for PDF conversion
     */
    public String cleanHtmlForPdf(String htmlContent) {
        try {
            Document document = Jsoup.parse(htmlContent);

            // Remove script tags
            document.select("script").remove();

            // Remove form elements that cause issues
            document.select("form").remove();
            document.select("input").remove();
            document.select("textarea").remove();
            document.select("button").remove();
            document.select("select").remove();

            // Remove problematic CSS properties
            document.select("[style*=display:grid]").removeAttr("style");
            document.select("[style*=grid-]").removeAttr("style");
            document.select("[style*=flex]").removeAttr("style");
            document.select("[style*=position:fixed]").removeAttr("style");
            document.select("[style*=position:absolute]").removeAttr("style");
            document.select("[style*=position:sticky]").removeAttr("style");

            // Remove CSS classes that might contain unsupported styles
            document.select(".grid").removeClass("grid");
            document.select(".flex").removeClass("flex");
            document.select(".fixed").removeClass("fixed");
            document.select(".absolute").removeClass("absolute");
            document.select(".sticky").removeClass("sticky");

            // Ensure proper structure
            if (document.body() == null) {
                document.appendElement("body");
            }

            return document.html();
        } catch (Exception e) {
            log.error("Error cleaning HTML content", e);
            return htmlContent; // Return original if cleaning fails
        }
    }

    /**
     * Clean CSS content for PDF conversion
     */
    public String cleanCssForPdf(String cssContent) {
        try {
            // Remove unsupported CSS properties
            String cleanedCss = cssContent
                    .replaceAll("(?i)display\\s*:\\s*grid[^;]*;", "")
                    .replaceAll("(?i)display\\s*:\\s*flex[^;]*;", "")
                    .replaceAll("(?i)grid-[^:]*:[^;]*;", "")
                    .replaceAll("(?i)flex-[^:]*:[^;]*;", "")
                    .replaceAll("(?i)align-items[^:]*:[^;]*;", "")
                    .replaceAll("(?i)justify-content[^:]*:[^;]*;", "")
                    .replaceAll("(?i)gap[^:]*:[^;]*;", "")
                    .replaceAll("(?i)position\\s*:\\s*(fixed|absolute|sticky)[^;]*;", "")
                    .replaceAll("(?i)transform[^:]*:[^;]*;", "")
                    .replaceAll("(?i)box-shadow[^:]*:[^;]*;", "")
                    .replaceAll("(?i)[0-9.]+fr\\b", "100%"); // Replace fr units with percentages

            return cleanedCss;
        } catch (Exception e) {
            log.error("Error cleaning CSS content", e);
            return cssContent; // Return original if cleaning fails
        }
    }
}
