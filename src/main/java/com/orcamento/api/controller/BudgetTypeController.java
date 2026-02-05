package com.orcamento.api.controller;

import com.orcamento.api.dto.BudgetTypeDTO;
import com.orcamento.api.service.BudgetTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/budget-types")
@Tag(name = "Tipos de Orçamento", description = "Endpoints para gerenciar os tipos de orçamento disponíveis.")
public class BudgetTypeController {

    @Autowired
    private BudgetTypeService service;

    @GetMapping
    @Operation(summary = "Lista todos os tipos de orçamento")
    public ResponseEntity<List<BudgetTypeDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um tipo de orçamento pelo ID")
    public ResponseEntity<BudgetTypeDTO> getById(@PathVariable UUID id) {
        BudgetTypeDTO dto = service.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/deleted")
    @Operation(summary = "Lista todos os tipos de orçamento deletados (soft delete)")
    public ResponseEntity<List<BudgetTypeDTO>> getDeleted() {
        return ResponseEntity.ok(service.getAllDeleted());
    }

    @PostMapping
    @Operation(summary = "Cria um novo tipo de orçamento")
    public ResponseEntity<BudgetTypeDTO> create(@RequestBody @Valid BudgetTypeDTO dto) {
        BudgetTypeDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um tipo de orçamento existente")
    public ResponseEntity<BudgetTypeDTO> update(@PathVariable UUID id, @RequestBody @Valid BudgetTypeDTO dto) {
        BudgetTypeDTO updated = service.update(id, dto);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Realiza a exclusão lógica de um tipo de orçamento")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}