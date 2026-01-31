package com.orcamento.api.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import com.orcamento.api.entity.enums.BillingMethod;

@Entity
@Table(name = "quote_requests")
public class QuoteRequest {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "budget_type_id", nullable = false)
    private BudgetType budgetType;

    @Column(name = "requester_name", nullable = false, length = 150)
    private String requesterName;

    @Column(name = "requester_email", length = 254)
    private String requesterEmail;

    @Column(name = "document_original_name", nullable = false, length = 255)
    private String documentOriginalName;

    @Column(name = "document_storage_key", nullable = false, length = 500)
    private String documentStorageKey;

    @Column(name = "document_mime_type", length = 100)
    private String documentMimeType;

    @Column(name = "document_size_bytes")
    private Long documentSizeBytes;

    @Column(name = "billing_method", nullable = false)
    private String billingMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_method_used", nullable = false)
    private BillingMethod billingMethodUsed;

    @Column(name = "fee_used", nullable = false, precision = 12, scale = 2)
    private BigDecimal feeUsed;

    @Column(name = "counted_units", nullable = false)
    private Integer countedUnits;

    @Column(name = "estimated_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedTotal;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public QuoteRequest() {

    }

    public QuoteRequest(UUID id, BudgetType budgetType, String requesterName, String requesterEmail,
            String documentOriginalName, String documentStorageKey, String documentMimeType, Long documentSizeBytes,
            BillingMethod billingMethodUsed, BigDecimal feeUsed, Integer countedUnits,
            BigDecimal estimatedTotal, String status, OffsetDateTime createdAt, OffsetDateTime updatedAt,
            OffsetDateTime deletedAt) {
        this.id = id;
        this.budgetType = budgetType;
        this.requesterName = requesterName;
        this.requesterEmail = requesterEmail;
        this.documentOriginalName = documentOriginalName;
        this.documentStorageKey = documentStorageKey;
        this.documentMimeType = documentMimeType;
        this.documentSizeBytes = documentSizeBytes;
        this.billingMethodUsed = billingMethodUsed;
        this.feeUsed = feeUsed;
        this.countedUnits = countedUnits;
        this.estimatedTotal = estimatedTotal;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public String getBillingMethod() {
        return billingMethod;
    }

    public void setBillingMethod(String billingMethod) {
        this.billingMethod = billingMethod;
    }

    public BillingMethod getBillingMethodUsed() {
        return billingMethodUsed;
    }

    public void setBillingMethodUsed(BillingMethod billingMethodUsed) {
        this.billingMethodUsed = billingMethodUsed;
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public Integer getCountedUnits() {
        return countedUnits;
    }

    public void setCountedUnits(Integer countedUnits) {
        this.countedUnits = countedUnits;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getEstimatedTotal() {
        return estimatedTotal;
    }

    public void setEstimatedTotal(BigDecimal estimatedTotal) {
        this.estimatedTotal = estimatedTotal;
    }

    public BigDecimal getFeeUsed() {
        return feeUsed;
    }

    public void setFeeUsed(BigDecimal feeUsed) {
        this.feeUsed = feeUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

}