package com.orcamento.api.controller;

import com.orcamento.api.entity.BudgetType;
import com.orcamento.api.entity.enums.BillingMethod;
import com.orcamento.api.repository.BudgetTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/budget-types")
public class BudgetTypeController {

    @Autowired
    private BudgetTypeRepository repository;

    @GetMapping
    public List<BudgetType> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<BudgetType> getById(@PathVariable UUID id) {
        return repository.findById(id);
    }

    @PostMapping
    public BudgetType create(@RequestBody BudgetType body) {
        // Converte sigla para palavra inteira
        body.setBillingMethod(BillingMethod.siglaToBillingMethod(body.getBillingMethod()));
        return repository.save(body);
    }

    @PutMapping("/{id}")
    public BudgetType update(@PathVariable UUID id, @RequestBody BudgetType body) {
        body.setId(id);
        body.setBillingMethod(BillingMethod.siglaToBillingMethod(body.getBillingMethod()));
        return repository.save(body);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        repository.deleteById(id);
    }
}