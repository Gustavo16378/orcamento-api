package com.orcamento.api.service;

import com.orcamento.api.dto.NotificationEventDTO;
import com.orcamento.api.dto.QuoteRequestDTO;
import com.orcamento.api.entity.BudgetType;
import com.orcamento.api.entity.QuoteRequest;
import com.orcamento.api.entity.enums.QuoteStatus;
import com.orcamento.api.repository.BudgetTypeRepository;
import com.orcamento.api.repository.QuoteRequestRepository;
import com.orcamento.api.util.TemplateUtils;
import com.orcamento.api.messaging.NotificationProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuoteRequestService {

    @Autowired
    private QuoteRequestRepository quoteRequestRepository;

    @Autowired
    private BudgetTypeRepository budgetTypeRepository;

    @Autowired
    private NotificationProducerService notificationProducerService; // <--- AQUI!

    // Conversão Entity -> DTO
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
            dto.setBillingMethodUsed(entity.getBillingMethodUsed());
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

    // Conversão DTO -> Entity (criação)
    private void mapDtoToEntity(QuoteRequestDTO dto, QuoteRequest entity, BudgetType budgetType) {
        entity.setBudgetType(budgetType);
        entity.setRequesterName(dto.getRequesterName());
        entity.setRequesterEmail(dto.getRequesterEmail());
        entity.setDocumentOriginalName(dto.getDocumentOriginalName());
        entity.setDocumentStorageKey(dto.getDocumentStorageKey());
        entity.setDocumentMimeType(dto.getDocumentMimeType());
        entity.setDocumentSizeBytes(dto.getDocumentSizeBytes());
        if (dto.getBillingMethodUsed() != null) {
            // Se precisar parse customizado, adapte aqui!
            entity.setBillingMethodUsed(dto.getBillingMethodUsed());
        }
        entity.setFeeUsed(dto.getFeeUsed());
        entity.setCountedUnits(dto.getCountedUnits());
        entity.setEstimatedTotal(dto.getEstimatedTotal());
        entity.setStatus(dto.getStatus());

    }

    /*** Listar todos (não deletados) ***/
    public List<QuoteRequestDTO> getAll() {
        return quoteRequestRepository.findAllByDeletedAtIsNull()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /*** Listar deletados ***/
    public List<QuoteRequestDTO> getAllDeleted() {
        return quoteRequestRepository.findAllByDeletedAtIsNotNull()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /*** Buscar por ID ***/
    public QuoteRequestDTO getById(UUID id) {
        return quoteRequestRepository.findById(id)
                .filter(qr -> qr.getDeletedAt() == null)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada ou foi deletada."));
    }

    /**
     * Lista todas as quotes não deletadas COM PAGINAÇÃO
     */
    public Page<QuoteRequestDTO> getAllPaginated(Pageable pageable) {
        Page<QuoteRequest> page = quoteRequestRepository.findAllByDeletedAtIsNull(pageable);
        return page.map(this::toDTO);
    }

    /**
     * Lista todas as quotes deletadas COM PAGINAÇÃO
     */
    public Page<QuoteRequestDTO> getAllDeletedPaginated(Pageable pageable) {
        Page<QuoteRequest> page = quoteRequestRepository.findAllByDeletedAtIsNotNull(pageable);
        return page.map(this::toDTO);
    }

    /*** Criar nova solicitação ***/
    public QuoteRequestDTO create(QuoteRequestDTO dto) {
        BudgetType budgetType = budgetTypeRepository.findById(dto.getBudgetTypeId())
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de orçamento com ID " + dto.getBudgetTypeId() + " não encontrado."));
        QuoteRequest entity = new QuoteRequest();
        mapDtoToEntity(dto, entity, budgetType);
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        entity.setDeletedAt(null);

        QuoteRequest saved = quoteRequestRepository.save(entity);
        QuoteRequestDTO savedDTO = toDTO(saved);

        // Monta o BODY HTML via template externo DO CLASSPATH!
        String templateHtml;
        String bodyHtml;
        try {
            templateHtml = TemplateUtils.loadTemplateFromClasspath("templates/orcamento_criado.html");
            bodyHtml = TemplateUtils.processTemplate(
                    templateHtml,
                    savedDTO.getRequesterName(),
                    budgetType.getBudgetTypeName(),
                    savedDTO.getId().toString());
        } catch (IOException e) {
            // Fallback em caso de erro – recomenda-se logar o erro!
            bodyHtml = "<h1>Olá, " + savedDTO.getRequesterName() + "!</h1><p>Seu orçamento foi criado com sucesso!</p>";
        }

        NotificationEventDTO event = new NotificationEventDTO(
                savedDTO.getId(),
                savedDTO.getRequesterEmail(),
                savedDTO.getRequesterName(),
                "Seu orçamento foi criado!",
                bodyHtml);
        notificationProducerService.sendNotification(event);

        return savedDTO;
    }

    /*** Atualizar solicitação ***/
    public QuoteRequestDTO update(UUID id, QuoteRequestDTO dto) {
        QuoteRequest entity = quoteRequestRepository.findById(id)
                .filter(qr -> qr.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada ou foi deletada."));
        BudgetType budgetType = budgetTypeRepository.findById(dto.getBudgetTypeId())
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de orçamento com ID " + dto.getBudgetTypeId() + " não encontrado."));
        mapDtoToEntity(dto, entity, budgetType);
        entity.setUpdatedAt(OffsetDateTime.now());
        QuoteRequest updated = quoteRequestRepository.save(entity);
        return toDTO(updated);
    }

    /*** Soft Delete ***/
    public void softDelete(UUID id) {
        QuoteRequest entity = quoteRequestRepository.findById(id)
                .filter(qr -> qr.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada ou já deletada."));
        entity.setDeletedAt(OffsetDateTime.now());
        quoteRequestRepository.save(entity);
    }
}