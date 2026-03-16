package com.orcamento.api.dto;

import java.util.UUID;

public class NotificationEventDTO {

    private UUID externalReferenceId;
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String bodyHtml;
    private String templateType; // NOVO CAMPO

    public NotificationEventDTO() {
    }

    public NotificationEventDTO(UUID externalReferenceId, String recipientEmail, String recipientName, String subject,
            String bodyHtml, String templateType) {
        this.externalReferenceId = externalReferenceId;
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.subject = subject;
        this.bodyHtml = bodyHtml;
        this.templateType = templateType;
    }

    // Getters e setters abaixo
    public UUID getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(UUID externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
}
