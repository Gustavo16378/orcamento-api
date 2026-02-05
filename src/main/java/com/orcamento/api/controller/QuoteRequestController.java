package com.orcamento.api.controller;

import com.orcamento.api.dto.QuoteRequestDTO;
import com.orcamento.api.service.QuoteRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    
    @GetMapping("/all")
    @Operation(summary = "Lista TODAS as solicitações de orçamento (sem paginação) - DEPRECATED")
    @Deprecated
    public ResponseEntity<List<QuoteRequestDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    
    @GetMapping
    @Operation(summary = "Lista solicitações de orçamento com paginação")
    public ResponseEntity<Page<QuoteRequestDTO>> getAllPaginated(
            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenação (ex: createdAt, requesterName)")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Direção da ordenação (asc ou desc)")
            @RequestParam(defaultValue = "desc") String direction
    ) {
        System.out.println("getAllPaginated called with page=" + page + ", size=" + size + ", sortBy=" + sortBy + ", direction=" + direction);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<QuoteRequestDTO> result = service.getAllPaginated(pageable);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/deleted")
    @Operation(summary = "Lista solicitações deletadas com paginação")
    public ResponseEntity<Page<QuoteRequestDTO>> getDeletedPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "deletedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<QuoteRequestDTO> result = service.getAllDeletedPaginated(pageable);
        
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Busca uma solicitação pelo ID")
    public ResponseEntity<QuoteRequestDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
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