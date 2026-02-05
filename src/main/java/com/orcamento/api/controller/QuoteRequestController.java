package com.orcamento.api.controller;

import com.orcamento.api.dto.QuoteRequestDTO;
import com.orcamento.api.service.QuoteRequestService;
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
@RequestMapping("/quote-requests")
@Tag(name = "Solicitações de Orçamento", description = "Endpoints para gerenciar as solicitações de orçamento.")
public class QuoteRequestController {

    @Autowired
    private QuoteRequestService service;

    @GetMapping
    @Operation(summary = "Lista todas as solicitações de orçamento")
    public ResponseEntity<List<QuoteRequestDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma solicitação pelo ID")
    public ResponseEntity<QuoteRequestDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Lista todas as solicitações de orçamento deletadas (soft delete)")
    public ResponseEntity<List<QuoteRequestDTO>> getDeleted() {
        return ResponseEntity.ok(service.getAllDeleted());
    }

    @PostMapping
    @Operation(summary = "Cria uma nova solicitação de orçamento")
    public ResponseEntity<QuoteRequestDTO> create(@RequestBody @Valid QuoteRequestDTO dto) {
        QuoteRequestDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma solicitação de orçamento")
    public ResponseEntity<QuoteRequestDTO> update(@PathVariable UUID id, @RequestBody @Valid QuoteRequestDTO dto) {
        QuoteRequestDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Faz a exclusão lógica (soft delete) da solicitação de orçamento")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}