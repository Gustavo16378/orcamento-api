package com.orcamento.api.dto;

import java.math.BigDecimal;

import com.orcamento.api.entity.BudgetType;

public class QuoteRequestDTO {
    // todos os outros campos idênticos ao QuoteRequest
    private String billingMethodUsed;

    // + getters e setters
    // pode copiar os campos por IDE mesmo!
    private BudgetType budgetType;
    private String requesterName;
    private String requesterEmail;
    private String documentOriginalName;
    private String documentStorageKey;
    private String documentMimeType;
    private Long documentSizeBytes;
    private BigDecimal feeUsed;
    private Integer countedUnits;
    private BigDecimal estimatedTotal;
    private String status;

    public String getBillingMethodUsed() {
        return billingMethodUsed;
    }

    public void setBillingMethodUsed(String billingMethodUsed) {
        this.billingMethodUsed = billingMethodUsed;
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getDocumentOriginalName() {
        return documentOriginalName;
    }

    public void setDocumentOriginalName(String documentOriginalName) {
        this.documentOriginalName = documentOriginalName;
    }

    public String getDocumentStorageKey() {
        return documentStorageKey;
    }

    public void setDocumentStorageKey(String documentStorageKey) {
        this.documentStorageKey = documentStorageKey;
    }

    public String getDocumentMimeType() {
        return documentMimeType;
    }

    public void setDocumentMimeType(String documentMimeType) {
        this.documentMimeType = documentMimeType;
    }

    public Long getDocumentSizeBytes() {
        return documentSizeBytes;
    }

    public void setDocumentSizeBytes(Long documentSizeBytes) {
        this.documentSizeBytes = documentSizeBytes;
    }

    public BigDecimal getFeeUsed() {
        return feeUsed;
    }

    public void setFeeUsed(BigDecimal feeUsed) {
        this.feeUsed = feeUsed;
    }

    public Integer getCountedUnits() {
        return countedUnits;
    }

    public void setCountedUnits(Integer countedUnits) {
        this.countedUnits = countedUnits;
    }

    public BigDecimal getEstimatedTotal() {
        return estimatedTotal;
    }

    public void setEstimatedTotal(BigDecimal estimatedTotal) {
        this.estimatedTotal = estimatedTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    

}
