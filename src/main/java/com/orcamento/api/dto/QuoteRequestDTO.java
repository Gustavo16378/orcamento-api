package com.orcamento.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class QuoteRequestDTO {

    @Schema(description = "ID único da solicitação de orçamento", example = "f1e2d3c4-b5a6-7890-1234-567890abcdef")
    private UUID id;

    @NotNull(message = "O ID do tipo de orçamento é obrigatório")
    @Schema(description = "ID do tipo de orçamento relacionado", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID budgetTypeId;

    @NotBlank(message = "O nome do solicitante é obrigatório")
    @Size(max = 150, message = "O nome pode ter até 150 caracteres")
    @Schema(example = "João da Silva")
    private String requesterName;

    @Email(message = "Formato de e-mail do solicitante inválido")
    @Size(max = 254, message = "O e-mail pode ter até 254 caracteres")
    @Schema(example = "joao.silva@email.com")
    private String requesterEmail;

    @NotBlank(message = "O nome do documento é obrigatório")
    @Size(max = 255, message = "O nome do documento pode ter até 255 caracteres")
    @Schema(example = "contrato_proposta.pdf")
    private String documentOriginalName;

    @NotBlank(message = "A chave de armazenamento do documento é obrigatória")
    @Size(max = 500, message = "A chave pode ter até 500 caracteres")
    @Schema(example = "docs/2026/02/uuid-aleatorio.pdf")
    private String documentStorageKey;

    @Size(max = 100, message = "O MIME type pode ter até 100 caracteres")
    @Schema(example = "application/pdf")
    private String documentMimeType;

    @NotNull(message = "O tamanho do documento é obrigatório")
    @Min(value = 1, message = "O tamanho do documento deve ser positivo")
    @Schema(example = "1024768")
    private Long documentSizeBytes;

    @NotBlank(message = "O método de faturamento utilizado é obrigatório")
    @Schema(example = "WORD")
    private String billingMethodUsed;

    @NotNull(message = "A taxa utilizada é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "A taxa deve ser positiva")
    @Schema(example = "0.25")
    private BigDecimal feeUsed;

    @NotNull(message = "A quantidade de unidades é obrigatória")
    @Min(value = 1, message = "Deve ser no mínimo 1 unidade")
    @Schema(example = "1500")
    private Integer countedUnits;

    @NotNull(message = "O valor estimado total é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O total deve ser positivo")
    @Schema(example = "375.00")
    private BigDecimal estimatedTotal;

    @NotBlank(message = "O status é obrigatório")
    @Size(max = 30, message = "O status pode ter até 30 caracteres")
    @Schema(example = "PENDING")
    private String status;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private OffsetDateTime deletedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBudgetTypeId() {
        return budgetTypeId;
    }

    public void setBudgetTypeId(UUID budgetTypeId) {
        this.budgetTypeId = budgetTypeId;
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

    public String getBillingMethodUsed() {
        return billingMethodUsed;
    }

    public void setBillingMethodUsed(String billingMethodUsed) {
        this.billingMethodUsed = billingMethodUsed;
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
