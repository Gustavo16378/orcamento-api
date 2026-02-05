package com.orcamento.api.service;

import com.orcamento.api.dto.BudgetTypeDTO;
import com.orcamento.api.entity.BudgetType;
import com.orcamento.api.repository.BudgetTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BudgetTypeService {

    @Autowired
    private BudgetTypeRepository budgetTypeRepository;

    /*** Conversão de Entity para DTO ***/
    private BudgetTypeDTO toDTO(BudgetType entity) {
        BudgetTypeDTO dto = new BudgetTypeDTO(entity.getId(), entity.getBudgetTypeName(), entity.getBillingMethod(), entity.getFee(), entity.getDescription(), entity.getTargetEmail(), entity.getCreatedAt(), entity.getUpdatedAt(), entity.getDeletedAt());
        return dto;
    }

    // Conversao de DTO para Entity
    private BudgetType toEntity(BudgetTypeDTO dto) {
        BudgetType entity = new BudgetType(dto.getId(), dto.getBudgetTypeName(), dto.getBillingMethod(), dto.getFee(), dto.getDescription(), dto.getTargetEmail(), dto.getCreatedAt(), dto.getUpdatedAt(), dto.getDeletedAt());
        return entity;
    }

    /*** Listar todos (não deletados) ***/
    public List<BudgetTypeDTO> getAll() {
        return budgetTypeRepository.findAllByDeletedAtIsNull()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /*** Listar deletados ***/
    public List<BudgetTypeDTO> getAllDeleted() {
        return budgetTypeRepository.findAllByDeletedAtIsNotNull()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /*** Buscar por ID ***/
    public BudgetTypeDTO getById(UUID id) {
        return budgetTypeRepository.findById(id)
                .filter(bt -> bt.getDeletedAt() == null)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("BudgetType não encontrado ou foi deletado."));
    }

    /*** Criar novo ***/
    public BudgetTypeDTO create(BudgetTypeDTO dto) {
        BudgetType entity = toEntity(dto);
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        entity.setDeletedAt(null); // Garantido

        BudgetType saved = budgetTypeRepository.save(entity);
        return toDTO(saved);
    }

    /*** Atualizar ***/
    public BudgetTypeDTO update(UUID id, BudgetTypeDTO dto) {
        BudgetType entity = budgetTypeRepository.findById(id)
                .filter(bt -> bt.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("BudgetType não encontrado ou foi deletado."));

        entity.setBudgetTypeName(dto.getBudgetTypeName());
        entity.setBillingMethod(dto.getBillingMethod());
        entity.setFee(dto.getFee());
        entity.setDescription(dto.getDescription());
        entity.setTargetEmail(dto.getTargetEmail());
        entity.setUpdatedAt(OffsetDateTime.now());

        BudgetType saved = budgetTypeRepository.save(entity);
        return toDTO(saved);
    }

    /*** Soft Delete ***/
    public void softDelete(UUID id) {
    BudgetType entity = budgetTypeRepository.findById(id)
            .filter(bt -> bt.getDeletedAt() == null)
            .orElseThrow(() -> new RuntimeException("BudgetType não encontrado ou já deletado."));
    entity.setDeletedAt(OffsetDateTime.now());
    budgetTypeRepository.save(entity);
}
}