package com.orcamento.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.orcamento.api.entity.enums.BillingMethod;

public class BudgetTypeDTO {

    @Schema(description = "ID único do tipo de orçamento", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID id;

    @NotBlank(message = "O nome do tipo de orçamento é obrigatório.")
    @Size(max = 100, message = "O nome pode ter até 100 caracteres.")
    @Schema(description = "Nome descritivo do tipo de orçamento", example = "Tradução Juramentada")
    private String budgetTypeName;

    @NotBlank(message = "O método de faturamento é obrigatório.")
    @Size(max = 10, message = "O método de faturamento pode ter até 10 caracteres.")
    @Schema(description = "Método de faturamento (ex: 'WORD', 'PAGE')", example = "WORD")
    private BillingMethod billingMethod;

    @NotNull(message = "A taxa é obrigatória.")
    @DecimalMin(value = "0.0", inclusive = true, message = "A taxa deve ser positiva.")
    @Schema(description = "Valor da taxa para o cálculo", example = "0.25")
    private BigDecimal fee;

    @Size(max = 500, message = "A descrição pode ter até 500 caracteres.")
    @Schema(description = "Descrição detalhada do tipo de orçamento", example = "Tradução de documentos oficiais com validade legal.")
    private String description;

    @NotBlank(message = "O e-mail de destino é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 254, message = "O e-mail pode ter até 254 caracteres.")
    @Schema(description = "E-mail para onde as solicitações serão enviadas", example = "contato@empresa.com")
    private String targetEmail;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private OffsetDateTime deletedAt;

    public BudgetTypeDTO() {
    }

    public BudgetTypeDTO(UUID id, String budgetTypeName, BillingMethod billingMethod, BigDecimal fee,
            String description, String targetEmail, OffsetDateTime createdAt, OffsetDateTime updatedAt,
            OffsetDateTime deletedAt) {
        this.id = id;
        this.budgetTypeName = budgetTypeName;
        this.billingMethod = billingMethod;
        this.fee = fee;
        this.description = description;
        this.targetEmail = targetEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBudgetTypeName() {
        return budgetTypeName;
    }

    public void setBudgetTypeName(String budgetTypeName) {
        this.budgetTypeName = budgetTypeName;
    }

    public BillingMethod getBillingMethod() {
        return billingMethod;
    }

    public void setBillingMethod(BillingMethod billingMethod) {
        this.billingMethod = billingMethod;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
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
}
