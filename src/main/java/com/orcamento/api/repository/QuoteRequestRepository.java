package com.orcamento.api.repository;

import com.orcamento.api.entity.QuoteRequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, UUID> {
    List<QuoteRequest> findAllByDeletedAtIsNotNull();
    List<QuoteRequest> findAllByDeletedAtIsNull();

    Page<QuoteRequest> findAllByDeletedAtIsNull(Pageable pageable);
    
    // NOVO: Também pode ter paginação para deletados
    Page<QuoteRequest> findAllByDeletedAtIsNotNull(Pageable pageable);

}
