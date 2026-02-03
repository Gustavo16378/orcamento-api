package com.orcamento.api.controller;

import com.orcamento.api.dto.QuoteRequestDTO;
import com.orcamento.api.entity.BudgetType;
import com.orcamento.api.entity.QuoteRequest;
import com.orcamento.api.entity.enums.BillingMethod;
import com.orcamento.api.repository.BudgetTypeRepository;
import com.orcamento.api.repository.QuoteRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/quote-requests")
@Tag(name = "Solicitações de Orçamento", description = "Endpoints para gerenciar as solicitações de orçamento.")
public class QuoteRequestController {

    @Autowired
    private QuoteRequestRepository quoteRequestRepository;

    @Autowired
    private BudgetTypeRepository budgetTypeRepository;

    @GetMapping
    @Operation(summary = "Lista todas as solicitações de orçamento")
    public ResponseEntity<List<QuoteRequestDTO>> getAll() {
        List<QuoteRequestDTO> dtoList = quoteRequestRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma solicitação pelo ID")
    public ResponseEntity<QuoteRequestDTO> getById(@PathVariable UUID id) {
        return quoteRequestRepository.findById(id)
                .map(entity -> ResponseEntity.ok(toDTO(entity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/deleted")
    @Operation(summary = "Lista todas as solicitações de orçamento deletadas (soft delete)")
    public ResponseEntity<List<QuoteRequestDTO>> getDeleted() {
        List<QuoteRequestDTO> dtoList = quoteRequestRepository.findAllByDeletedAtIsNotNull()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping
    @Operation(summary = "Cria uma nova solicitação de orçamento")
    public ResponseEntity<QuoteRequestDTO> create(@RequestBody @Valid QuoteRequestDTO dto) {
        BudgetType budgetType = budgetTypeRepository.findById(dto.getBudgetTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tipo de orçamento com ID " + dto.getBudgetTypeId() + " não encontrado."));

        QuoteRequest entity = new QuoteRequest();
        mapDtoToEntity(dto, entity, budgetType);

        QuoteRequest savedEntity = quoteRequestRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedEntity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma solicitação de orçamento")
    public ResponseEntity<QuoteRequestDTO> update(@PathVariable UUID id, @RequestBody @Valid QuoteRequestDTO dto) {
        QuoteRequest entity = quoteRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada com ID: " + id));

        BudgetType budgetType = budgetTypeRepository.findById(dto.getBudgetTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tipo de orçamento com ID " + dto.getBudgetTypeId() + " não encontrado."));

        mapDtoToEntity(dto, entity, budgetType);
        entity.setId(id);

        QuoteRequest updatedEntity = quoteRequestRepository.save(entity);
        return ResponseEntity.ok(toDTO(updatedEntity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Faz a exclusão lógica (soft delete) da solicitação de orçamento")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        QuoteRequest entity = quoteRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada com ID: " + id));
        entity.setDeletedAt(OffsetDateTime.now());
        quoteRequestRepository.save(entity);
        return ResponseEntity.noContent().build();
    }

    // --- Métodos Auxiliares ---

    private QuoteRequestDTO toDTO(QuoteRequest entity) {
        QuoteRequestDTO dto = new QuoteRequestDTO();
        dto.setId(entity.getId());
        dto.setRequesterName(entity.getRequesterName());
        dto.setRequesterEmail(entity.getRequesterEmail());
        dto.setDocumentOriginalName(entity.getDocumentOriginalName());
        dto.setDocumentStorageKey(entity.getDocumentStorageKey());
        dto.setDocumentMimeType(entity.getDocumentMimeType());
        dto.setDocumentSizeBytes(entity.getDocumentSizeBytes());
        if (entity.getBillingMethodUsed() != null) {
            dto.setBillingMethodUsed(entity.getBillingMethodUsed().name()); // Se for enum, .name(); se for String, use direto
        }
        dto.setFeeUsed(entity.getFeeUsed());
        dto.setCountedUnits(entity.getCountedUnits());
        dto.setEstimatedTotal(entity.getEstimatedTotal());
        dto.setStatus(entity.getStatus());
        if (entity.getBudgetType() != null) {
            dto.setBudgetTypeId(entity.getBudgetType().getId());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDeletedAt(entity.getDeletedAt());
        return dto;
    }

    private void mapDtoToEntity(QuoteRequestDTO dto, QuoteRequest entity, BudgetType budgetType) {
        entity.setBudgetType(budgetType);
        entity.setRequesterName(dto.getRequesterName());
        entity.setRequesterEmail(dto.getRequesterEmail());
        entity.setDocumentOriginalName(dto.getDocumentOriginalName());
        entity.setDocumentStorageKey(dto.getDocumentStorageKey());
        entity.setDocumentMimeType(dto.getDocumentMimeType());
        entity.setDocumentSizeBytes(dto.getDocumentSizeBytes());
        // AQUI, FAZ O FUNIL CERTO (STRING --> ENUM usando seu método)
        entity.setBillingMethod(BillingMethod.siglaToBillingMethod(dto.getBillingMethodUsed())); // Espera String!
        entity.setBillingMethodUsed(BillingMethod.valueOf(BillingMethod.siglaToBillingMethod(dto.getBillingMethodUsed()))); // Espera Enum!
        entity.setFeeUsed(dto.getFeeUsed());
        entity.setCountedUnits(dto.getCountedUnits());
        entity.setEstimatedTotal(dto.getEstimatedTotal());
        entity.setStatus(dto.getStatus());
    }
}