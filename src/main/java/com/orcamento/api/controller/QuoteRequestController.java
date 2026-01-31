package com.orcamento.api.controller;

import com.orcamento.api.dto.QuoteRequestDTO;
import com.orcamento.api.entity.QuoteRequest;
import com.orcamento.api.entity.enums.BillingMethod;
import com.orcamento.api.repository.QuoteRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/quote-requests")
public class QuoteRequestController {

    @Autowired
    private QuoteRequestRepository repository;

    // GET /quote-requests -- lista todos os registros
    @GetMapping
    public List<QuoteRequest> getAll() {
        return repository.findAll();
    }

    // GET /quote-requests/{id} -- busca um pelo id
    @GetMapping("/{id}")
    public Optional<QuoteRequest> getById(@PathVariable UUID id) {
        return repository.findById(id);
    }

    // POST /quote-requests -- criar novo registro
    @PostMapping
    public QuoteRequest create(@RequestBody QuoteRequestDTO dto) {
        QuoteRequest body = new QuoteRequest();
        // copia os campos normais (pode usar ModelMapper ou manualmente)
        body.setBudgetType(dto.getBudgetType());
        body.setRequesterName(dto.getRequesterName());
        body.setRequesterEmail(dto.getRequesterEmail());
        body.setDocumentOriginalName(dto.getDocumentOriginalName());
        body.setDocumentStorageKey(dto.getDocumentStorageKey());
        body.setDocumentMimeType(dto.getDocumentMimeType());
        body.setDocumentSizeBytes(dto.getDocumentSizeBytes());
        body.setFeeUsed(dto.getFeeUsed());
        body.setCountedUnits(dto.getCountedUnits());
        body.setEstimatedTotal(dto.getEstimatedTotal());
        body.setStatus(dto.getStatus());
        // Trata billing methods
        String billingStr = BillingMethod.siglaToBillingMethod(dto.getBillingMethodUsed());
        body.setBillingMethod(billingStr);
        body.setBillingMethodUsed(BillingMethod.valueOf(billingStr));
        return repository.save(body);
    }

    @PutMapping("/{id}")
    public QuoteRequest update(@PathVariable UUID id, @RequestBody QuoteRequestDTO dto) {
        QuoteRequest body = new QuoteRequest();
        body.setId(id);
        body.setBudgetType(dto.getBudgetType());
        body.setRequesterName(dto.getRequesterName());
        body.setRequesterEmail(dto.getRequesterEmail());
        body.setDocumentOriginalName(dto.getDocumentOriginalName());
        body.setDocumentStorageKey(dto.getDocumentStorageKey());
        body.setDocumentMimeType(dto.getDocumentMimeType());
        body.setDocumentSizeBytes(dto.getDocumentSizeBytes());
        body.setFeeUsed(dto.getFeeUsed());
        body.setCountedUnits(dto.getCountedUnits());
        body.setEstimatedTotal(dto.getEstimatedTotal());
        body.setStatus(dto.getStatus());
        // Trata billing methods igual ao POST
        String billingStr = BillingMethod.siglaToBillingMethod(dto.getBillingMethodUsed());
        body.setBillingMethod(billingStr);
        body.setBillingMethodUsed(BillingMethod.valueOf(billingStr));
        return repository.save(body);
    }

    // DELETE /quote-requests/{id} -- delete pelo id
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        repository.deleteById(id);
    }
}