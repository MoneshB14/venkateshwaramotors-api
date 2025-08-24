package com.monesh.venkateswaramotors.global.service;

import com.monesh.venkateswaramotors.global.dto.EmailRequest;
import com.monesh.venkateswaramotors.global.dto.EmailResponse;
import com.monesh.venkateswaramotors.global.dto.EmailWithAttachmentsRequest;
import com.monesh.venkateswaramotors.global.dto.EmailFileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

        private final WebClient webClient;
        private final TemplateService templateService;

        @Value("${email.api.url:http://localhost:3000}")
        private String emailApiUrl;

        @Value("${email.api.endpoint:/send-email}")
        private String emailApiEndpoint;

        @Value("${email.api.attachments.endpoint:/send-email-with-attachments}")
        private String emailWithAttachmentsApiEndpoint;

        @Value("${email.api.files.endpoint:/send-email-with-files}")
        private String emailWithFilesApiEndpoint;

        /**
         * Send email using the Node.js mailer API
         * 
         * @param emailRequest The email request containing to, subject, and htmlContent
         * @return EmailResponse with success status
         */
        public Mono<EmailResponse> sendEmail(EmailRequest emailRequest) {
                log.info("Sending email to: {}", emailRequest.getTo());

                return webClient.post()
                                .uri(emailApiUrl + emailApiEndpoint)
                                .bodyValue(emailRequest)
                                .retrieve()
                                .bodyToMono(EmailResponse.class)
                                .doOnSuccess(response -> log.info("Email sent successfully to: {}",
                                                emailRequest.getTo()))
                                .doOnError(error -> log.error("Failed to send email to: {}, Error: {}",
                                                emailRequest.getTo(),
                                                error.getMessage()));
        }

        /**
         * Send email with template ID (fetches template from database)
         * 
         * @param to           Recipient email
         * @param subject      Email subject
         * @param templateId   Template ID to fetch from database
         * @param templateData Data to replace in template
         * @return EmailResponse with success status
         */
        public Mono<EmailResponse> sendEmailWithTemplate(String to, String subject, String templateId,
                        Map<String, Object> templateData) {
                // Get template from database or use default
                String template = templateService.getTemplateById(templateId);

                // Process template with data
                String htmlContent = templateService.processTemplate(template, templateData);

                EmailRequest emailRequest = EmailRequest.builder()
                                .to(to)
                                .subject(subject)
                                .htmlContent(htmlContent)
                                .build();

                return sendEmail(emailRequest);
        }

        /**
         * Send email with attachments using the Node.js mailer API
         * 
         * @param emailWithAttachmentsRequest The email request containing to, subject,
         *                                    htmlContent and attachments
         * @return EmailResponse with success status
         */
        public Mono<EmailResponse> sendEmailWithAttachments(EmailWithAttachmentsRequest emailWithAttachmentsRequest) {
                log.info("Sending email with attachments to: {}", emailWithAttachmentsRequest.getTo());

                return webClient.post()
                                .uri(emailApiUrl + emailWithAttachmentsApiEndpoint)
                                // .uri("http://localhost:3000/send-email-with-attachments")
                                .bodyValue(emailWithAttachmentsRequest)
                                .retrieve()
                                .bodyToMono(EmailResponse.class)
                                .doOnSuccess(response -> log.info("Email with attachments sent successfully to: {}",
                                                emailWithAttachmentsRequest.getTo()))
                                .doOnError(error -> log.error("Failed to send email with attachments to: {}, Error: {}",
                                                emailWithAttachmentsRequest.getTo(), error.getMessage()));
        }

        /**
         * Send email with file uploads using multipart/form-data
         * 
         * @param to          Recipient email address
         * @param subject     Email subject
         * @param htmlContent Email HTML content
         * @param fileName    Name of the file to attach
         * @param fileContent Byte array content of the file
         * @param contentType MIME type of the file
         * @return EmailFileUploadResponse with success status and file info
         */
        public Mono<EmailFileUploadResponse> sendEmailWithFileUpload(String to, String subject, String htmlContent,
                        String fileName, byte[] fileContent, String contentType) {

                log.info("Sending email with file upload to: {}, file: {}", to, fileName);

                try {
                        // Create multipart form data
                        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                        formData.add("to", to);
                        formData.add("subject", subject);
                        formData.add("htmlContent", htmlContent);

                        // Create file resource
                        ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
                                @Override
                                public String getFilename() {
                                        return fileName;
                                }
                        };

                        formData.add("files", fileResource);

                        return webClient.post()
                                        .uri(emailApiUrl + emailWithFilesApiEndpoint)
                                        // .uri("http://localhost:3000/send-email-with-files")
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .body(BodyInserters.fromMultipartData(formData))
                                        .retrieve()
                                        .bodyToMono(EmailFileUploadResponse.class)
                                        .doOnSuccess(response -> log.info(
                                                        "Email with file upload sent successfully to: {}, files count: {}",
                                                        to, response.getFilesCount()))
                                        .doOnError(error -> log.error(
                                                        "Failed to send email with file upload to: {}, Error: {}",
                                                        to, error.getMessage()));

                } catch (Exception e) {
                        log.error("Error preparing email with file upload to: {}", to, e);
                        return Mono.just(EmailFileUploadResponse.builder()
                                        .success(false)
                                        .message("Failed to prepare email: " + e.getMessage())
                                        .build());
                }
        }

}