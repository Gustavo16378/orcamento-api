package com.orcamento.api.repository;

import com.orcamento.api.entity.BudgetType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.UUID;

public interface BudgetTypeRepository extends JpaRepository<BudgetType, UUID> {
    List<BudgetType> findAllByDeletedAtIsNotNull();

    List<BudgetType> findAllByDeletedAtIsNull();

}
