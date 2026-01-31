package com.orcamento.api.repository;

import com.orcamento.api.entity.QuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, UUID> {
    
}
