package com.monesh.venkateswaramotors.global.service;

import com.monesh.venkateswaramotors.global.entity.EmailTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final MongoTemplate mongoTemplate;

    /**
     * Get email template by ID from database
     * 
     * @param templateId Template ID
     * @return Template content
     */
    public String getTemplateById(String templateId) {
        try {
            Query query = new Query(Criteria.where("templateId").is(templateId));
            EmailTemplate template = mongoTemplate.findOne(query, EmailTemplate.class);

            if (template != null) {
                log.info("Template found for ID: {}", templateId);
                return template.getHtmlContent();
            } else {
                log.warn("Template not found for ID: {}", templateId);
                return getFallbackTemplate(templateId);
            }
        } catch (Exception e) {
            log.error("Error fetching template from database for ID: {}, Error: {}", templateId, e.getMessage());
            return getFallbackTemplate(templateId);
        }
    }

    /**
     * Process template with data
     * 
     * @param template Template content
     * @param data     Template data
     * @return Processed template
     */
    public String processTemplate(String template, Map<String, Object> data) {
        String processedTemplate = template;

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            processedTemplate = processedTemplate.replace(placeholder, value);
        }

        return processedTemplate;
    }

    /**
     * Fallback template when database template is not found
     * 
     * @param templateId Template ID
     * @return Fallback template content
     */
    private String getFallbackTemplate(String templateId) {
        log.warn("Using fallback template for ID: {}", templateId);
        return """
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9;'>
                    <h1 style='color: #333; text-align: center;'>Service Notification</h1>
                    <div style='background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>
                        <p style='color: #666; line-height: 1.6;'>This is a notification from Venkateswara Motors service.</p>
                        <p style='color: #999; font-size: 12px; text-align: center;'>Template ID: %s</p>
                    </div>
                </div>
                """
                .formatted(templateId);
    }
}