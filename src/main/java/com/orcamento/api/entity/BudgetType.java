package com.orcamento.api.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "budget_types")
public class BudgetType {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "budget_type_name", nullable = false, length = 100)
    private String budgetTypeName;

    @Column(name = "billing_method", nullable = false, length = 10)
    private String billingMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal fee;

    @Column
    private String description;

    @Column(name = "target_email", nullable = false, length = 254)
    private String targetEmail;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public BudgetType() {
    }

    public BudgetType(UUID id, String budgetTypeName, String billingMethod, BigDecimal fee,
            String description, String targetEmail, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.budgetTypeName = budgetTypeName;
        this.billingMethod = billingMethod;
        this.fee = fee;
        this.description = description;
        this.targetEmail = targetEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getBillingMethod() {
        return billingMethod;
    }

    public void setBillingMethod(String billingMethod) {
        this.billingMethod = billingMethod;
    }

    public String getBudgetTypeName() {
        return budgetTypeName;
    }

    public void setBudgetTypeName(String budgetTypeName) {
        this.budgetTypeName = budgetTypeName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "BudgetType{" +
                "id=" + id +
                ", budgetTypeName='" + budgetTypeName + '\'' +
                ", billingMethod=" + billingMethod +
                ", fee=" + fee +
                ", targetEmail='" + targetEmail + '\'' +
                '}';
    }
}
