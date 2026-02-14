package com.orcamento.api.service;

import com.orcamento.api.dto.QuoteRequestDTO;
import com.orcamento.api.entity.BudgetType;
import com.orcamento.api.entity.QuoteRequest;
import com.orcamento.api.repository.BudgetTypeRepository;
import com.orcamento.api.repository.QuoteRequestRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.orcamento.api.extension.MemoryMonitorExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MemoryMonitorExtension.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do QuoteRequestService")
class QuoteRequestServiceTest {

    @Mock
    private QuoteRequestRepository quoteRequestRepository;

    @Mock
    private BudgetTypeRepository budgetTypeRepository;

    @InjectMocks
    private QuoteRequestService service;

    private UUID budgetTypeId;
    private UUID quoteRequestId;
    private BudgetType budgetType;
    private QuoteRequest quoteRequest;
    private QuoteRequestDTO quoteRequestDTO;

    @BeforeEach
    void setUp() {
        budgetTypeId = UUID.randomUUID();
        quoteRequestId = UUID.randomUUID();

        // Cria BudgetType mock
        budgetType = new BudgetType();
        budgetType.setId(budgetTypeId);
        budgetType.setBudgetTypeName("Tradução por Palavra");
        budgetType.setBillingMethod("WORD");
        budgetType.setFee(BigDecimal.valueOf(0.25));
        budgetType.setDescription("Teste");
        budgetType.setTargetEmail("teste@email.com");
        budgetType.setCreatedAt(OffsetDateTime.now());
        budgetType.setUpdatedAt(OffsetDateTime.now());

        // Cria QuoteRequest mock
        quoteRequest = new QuoteRequest();
        quoteRequest.setId(quoteRequestId);
        quoteRequest.setBudgetType(budgetType);
        quoteRequest.setRequesterName("João Silva");
        quoteRequest.setRequesterEmail("joao@email.com");
        quoteRequest.setDocumentOriginalName("contrato.pdf");
        quoteRequest.setDocumentStorageKey("storage/key/123");
        quoteRequest.setDocumentMimeType("application/pdf");
        quoteRequest.setDocumentSizeBytes(1024L);
        quoteRequest.setBillingMethodUsed("WORD");
        quoteRequest.setFeeUsed(BigDecimal.valueOf(0.25));
        quoteRequest.setCountedUnits(1000);
        quoteRequest.setEstimatedTotal(BigDecimal.valueOf(250.0));
        quoteRequest.setStatus("PENDING");
        quoteRequest.setCreatedAt(OffsetDateTime.now());
        quoteRequest.setUpdatedAt(OffsetDateTime.now());

        // Cria DTO
        quoteRequestDTO = new QuoteRequestDTO();
        quoteRequestDTO.setBudgetTypeId(budgetTypeId);
        quoteRequestDTO.setRequesterName("João Silva");
        quoteRequestDTO.setRequesterEmail("joao@email.com");
        quoteRequestDTO.setDocumentOriginalName("contrato.pdf");
        quoteRequestDTO.setDocumentStorageKey("storage/key/123");
        quoteRequestDTO.setDocumentMimeType("application/pdf");
        quoteRequestDTO.setDocumentSizeBytes(1024L);
        quoteRequestDTO.setBillingMethodUsed("WORD");
        quoteRequestDTO.setFeeUsed(BigDecimal.valueOf(0.25));
        quoteRequestDTO.setCountedUnits(1000);
        quoteRequestDTO.setEstimatedTotal(BigDecimal.valueOf(250.0));
        quoteRequestDTO.setStatus("PENDING");
    }

    @Test
    @DisplayName("Deve criar QuoteRequest com sucesso")
    void deveCriarQuoteRequestComSucesso() {
        // Given
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.of(budgetType));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(quoteRequest);

        // When
        QuoteRequestDTO result = service.create(quoteRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRequesterName()).isEqualTo("João Silva");
        assertThat(result.getRequesterEmail()).isEqualTo("joao@email.com");
        
        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(quoteRequestRepository, times(1)).save(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar QuoteRequest com BudgetType inexistente")
    void deveLancarExcecaoQuandoBudgetTypeNaoExiste() {
        // Given
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.create(quoteRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tipo de orçamento");

        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(quoteRequestRepository, never()).save(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("Deve buscar QuoteRequest por ID com sucesso")
    void deveBuscarQuoteRequestPorId() {
        // Given
        when(quoteRequestRepository.findById(quoteRequestId)).thenReturn(Optional.of(quoteRequest));

        // When
        QuoteRequestDTO result = service.getById(quoteRequestId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRequesterName()).isEqualTo("João Silva");
        
        verify(quoteRequestRepository, times(1)).findById(quoteRequestId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar QuoteRequest inexistente")
    void deveLancarExcecaoQuandoQuoteRequestNaoExiste() {
        // Given
        when(quoteRequestRepository.findById(quoteRequestId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.getById(quoteRequestId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Solicitação não encontrada");

        verify(quoteRequestRepository, times(1)).findById(quoteRequestId);
    }

    @Test
    @DisplayName("Deve retornar página de QuoteRequests não deletadas")
    void deveRetornarPaginaDeQuoteRequests() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<QuoteRequest> page = new PageImpl<>(List.of(quoteRequest), pageable, 1);
        
        when(quoteRequestRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(page);

        // When
        Page<QuoteRequestDTO> result = service.getAllPaginated(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRequesterName()).isEqualTo("João Silva");
        
        verify(quoteRequestRepository, times(1)).findAllByDeletedAtIsNull(pageable);
    }

    @Test
    @DisplayName("Deve atualizar QuoteRequest com sucesso")
    void deveAtualizarQuoteRequestComSucesso() {
        // Given
        QuoteRequestDTO updateDTO = new QuoteRequestDTO();
        updateDTO.setBudgetTypeId(budgetTypeId);
        updateDTO.setRequesterName("João Silva Atualizado");
        updateDTO.setRequesterEmail("joao.novo@email.com");
        updateDTO.setDocumentOriginalName("contrato.pdf");
        updateDTO.setDocumentStorageKey("storage/key/123");
        updateDTO.setDocumentMimeType("application/pdf");
        updateDTO.setDocumentSizeBytes(1024L);
        updateDTO.setBillingMethodUsed("WORD");
        updateDTO.setFeeUsed(BigDecimal.valueOf(0.25));
        updateDTO.setCountedUnits(1000);
        updateDTO.setEstimatedTotal(BigDecimal.valueOf(250.0));
        updateDTO.setStatus("APPROVED");

        when(quoteRequestRepository.findById(quoteRequestId)).thenReturn(Optional.of(quoteRequest));
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.of(budgetType));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(quoteRequest);

        // When
        QuoteRequestDTO result = service.update(quoteRequestId, updateDTO);

        // Then
        assertThat(result).isNotNull();
        
        verify(quoteRequestRepository, times(1)).findById(quoteRequestId);
        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(quoteRequestRepository, times(1)).save(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar QuoteRequest inexistente")
    void deveLancarExcecaoAoAtualizarQuoteRequestInexistente() {
        // Given
        when(quoteRequestRepository.findById(quoteRequestId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.update(quoteRequestId, quoteRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Solicitação não encontrada");

        verify(quoteRequestRepository, times(1)).findById(quoteRequestId);
        verify(quoteRequestRepository, never()).save(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("Deve fazer soft delete de QuoteRequest")
    void deveFazerSoftDelete() {
        // Given
        when(quoteRequestRepository.findById(quoteRequestId)).thenReturn(Optional.of(quoteRequest));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(quoteRequest);

        // When
        service.softDelete(quoteRequestId);

        // Then
        verify(quoteRequestRepository, times(1)).findById(quoteRequestId);
        verify(quoteRequestRepository, times(1)).save(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar QuoteRequest inexistente")
    void deveLancarExcecaoAoDeletarQuoteRequestInexistente() {
        // Given
        when(quoteRequestRepository.findById(quoteRequestId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.softDelete(quoteRequestId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Solicitação não encontrada");

        verify(quoteRequestRepository, times(1)).findById(quoteRequestId);
        verify(quoteRequestRepository, never()).save(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("Deve retornar página de QuoteRequests deletadas")
    void deveRetornarPaginaDeQuoteRequestsDeletadas() {
        // Given
        quoteRequest.setDeletedAt(OffsetDateTime.now());
        Pageable pageable = PageRequest.of(0, 10);
        Page<QuoteRequest> page = new PageImpl<>(List.of(quoteRequest), pageable, 1);
        
        when(quoteRequestRepository.findAllByDeletedAtIsNotNull(pageable)).thenReturn(page);

        // When
        Page<QuoteRequestDTO> result = service.getAllDeletedPaginated(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        
        verify(quoteRequestRepository, times(1)).findAllByDeletedAtIsNotNull(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver QuoteRequests")
    void deveRetornarPaginaVaziaQuandoNaoHouverQuotes() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<QuoteRequest> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(quoteRequestRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(emptyPage);

        // When
        Page<QuoteRequestDTO> result = service.getAllPaginated(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
        
        verify(quoteRequestRepository, times(1)).findAllByDeletedAtIsNull(pageable);
    }
}