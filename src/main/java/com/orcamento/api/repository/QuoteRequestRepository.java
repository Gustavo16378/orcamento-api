package com.orcamento.api.repository;

import com.orcamento.api.entity.QuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, UUID> {
    List<QuoteRequest> findAllByDeletedAtIsNotNull();
    List<QuoteRequest> findAllByDeletedAtIsNull();
}
