package com.orcamento.api.controller;

import com.orcamento.api.dto.BudgetTypeDTO;
import com.orcamento.api.entity.BudgetType;
import com.orcamento.api.repository.BudgetTypeRepository;
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
@RequestMapping("/budget-types")
@Tag(name = "Tipos de Orçamento", description = "Endpoints para gerenciar os tipos de orçamento disponíveis.")
public class BudgetTypeController {

    @Autowired
    private BudgetTypeRepository repository;

    @GetMapping
    @Operation(summary = "Lista todos os tipos de orçamento")
    public ResponseEntity<List<BudgetTypeDTO>> getAll() {
        List<BudgetTypeDTO> dtoList = repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um tipo de orçamento pelo ID")
    public ResponseEntity<BudgetTypeDTO> getById(@PathVariable UUID id) {
        return repository.findById(id)
                .map(entity -> ResponseEntity.ok(toDTO(entity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/deleted")
    @Operation(summary = "Lista todos os tipos de orçamento deletados (soft delete)")
    public ResponseEntity<List<BudgetTypeDTO>> getDeleted() {
        List<BudgetTypeDTO> dtoList = repository.findAllByDeletedAtIsNotNull()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping
    @Operation(summary = "Cria um novo tipo de orçamento")

    public ResponseEntity<BudgetTypeDTO> create(@RequestBody @Valid BudgetTypeDTO dto) {
        BudgetType entity = new BudgetType();
        // Mapeia DTO para Entidade, respeitando os tipos
        mapDtoToEntity(dto, entity);

        BudgetType savedEntity = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedEntity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um tipo de orçamento existente")
    public ResponseEntity<BudgetTypeDTO> update(@PathVariable UUID id, @RequestBody @Valid BudgetTypeDTO dto) {
        BudgetType entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Tipo de orçamento não encontrado com o ID: " + id));

        // Mapeia DTO para Entidade, respeitando os tipos
        mapDtoToEntity(dto, entity);
        entity.setId(id); // Garante que o ID não seja alterado

        BudgetType updatedEntity = repository.save(entity);
        return ResponseEntity.ok(toDTO(updatedEntity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Realiza a exclusão lógica de um tipo de orçamento")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        BudgetType entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Tipo de orçamento não encontrado com o ID: " + id));

        entity.setDeletedAt(OffsetDateTime.now());
        repository.save(entity);

        return ResponseEntity.noContent().build();
    }

    // --- Métodos Auxiliares ---
    private BudgetTypeDTO toDTO(BudgetType entity) {
        BudgetTypeDTO dto = new BudgetTypeDTO();
        dto.setId(entity.getId());
        dto.setBudgetTypeName(entity.getBudgetTypeName());
        // Aqui, a conversão é do Enum da entidade para a String do DTO
        if (entity.getBillingMethod() != null) {
            dto.setBillingMethod(entity.getBillingMethod());
        }
        dto.setFee(entity.getFee());
        dto.setDescription(entity.getDescription());
        dto.setTargetEmail(entity.getTargetEmail());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDeletedAt(entity.getDeletedAt());
        return dto;
    }

    private void mapDtoToEntity(BudgetTypeDTO dto, BudgetType entity) {
        entity.setBudgetTypeName(dto.getBudgetTypeName());
        // **CORREÇÃO APLICADA AQUI**
        // Passa a String do DTO diretamente, pois a entidade faz a conversão
        entity.setBillingMethod(dto.getBillingMethod());
        entity.setFee(dto.getFee());
        entity.setDescription(dto.getDescription());
        entity.setTargetEmail(dto.getTargetEmail());
    }
}
