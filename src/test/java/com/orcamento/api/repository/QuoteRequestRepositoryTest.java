package com.orcamento.api.repository;

import com.orcamento.api.entity.BudgetType;
import com.orcamento.api.entity.QuoteRequest;
import com.orcamento.api.entity.enums.BillingMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import com.orcamento.api.entity.enums.QuoteStatus;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do QuoteRequestRepository")
class QuoteRequestRepositoryTest {

    @Autowired
    private QuoteRequestRepository quoteRequestRepository;

    @Autowired
    private BudgetTypeRepository budgetTypeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private BudgetType budgetType;

    @BeforeEach
    void setUp() {
        quoteRequestRepository.deleteAll();
        budgetTypeRepository.deleteAll();

        // Limpa o banco antes de cada teste
        quoteRequestRepository.deleteAll();
        budgetTypeRepository.deleteAll();

        // Cria um BudgetType para usar nos testes
        budgetType = new BudgetType();
        budgetType.setId(UUID.randomUUID());
        budgetType.setBudgetTypeName("Tradução por Palavra");
        budgetType.setBillingMethod("WORD");
        budgetType.setFee(BigDecimal.valueOf(0.25));
        budgetType.setDescription("Teste");
        budgetType.setTargetEmail("teste@email.com");
        budgetType.setCreatedAt(OffsetDateTime.now());
        budgetType.setUpdatedAt(OffsetDateTime.now());
        budgetType = budgetTypeRepository.save(budgetType);
    }

    @Test
    @DisplayName("Deve salvar uma QuoteRequest no banco")
    void deveSalvarQuoteRequest() {
        // Given
        QuoteRequest quote = criarQuoteRequest("João", "joao@email.com");

        // When
        QuoteRequest savedQuote = quoteRequestRepository.save(quote);

        // Then
        assertThat(savedQuote.getId()).isNotNull();
        assertThat(savedQuote.getRequesterName()).isEqualTo("João");
        assertThat(savedQuote.getRequesterEmail()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("Deve buscar apenas QuoteRequests não deletadas")
    void deveBuscarApenasNaoDeletadas() {
        // Given
        QuoteRequest quote1 = criarQuoteRequest("Maria", "maria@email.com");
        QuoteRequest quote2 = criarQuoteRequest("Pedro", "pedro@email.com");
        QuoteRequest quote3 = criarQuoteRequest("Ana", "ana@email.com");
        
        quoteRequestRepository.save(quote1);
        quoteRequestRepository.save(quote2);
        quote3.setDeletedAt(OffsetDateTime.now()); // Marca como deletado
        quoteRequestRepository.save(quote3);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<QuoteRequest> result = quoteRequestRepository.findAllByDeletedAtIsNull(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .hasSize(2)
                .extracting(QuoteRequest::getRequesterName)
                .containsExactlyInAnyOrder("Maria", "Pedro");
    }

    @Test
    @DisplayName("Deve buscar apenas QuoteRequests deletadas")
    void deveBuscarApenasDeletadas() {
        // Given
        QuoteRequest quote1 = criarQuoteRequest("Carlos", "carlos@email.com");
        QuoteRequest quote2 = criarQuoteRequest("Julia", "julia@email.com");
        
        quoteRequestRepository.save(quote1);
        quote2.setDeletedAt(OffsetDateTime.now());
        quoteRequestRepository.save(quote2);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<QuoteRequest> result = quoteRequestRepository.findAllByDeletedAtIsNotNull(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getRequesterName()).isEqualTo("Julia");
    }

    @Test
    @DisplayName("Deve retornar paginação correta com 15 quotes")
    void deveRetornarPaginacaoCorreta() {
        // Given - Cria 15 quotes
        for (int i = 1; i <= 15; i++) {
            QuoteRequest quote = criarQuoteRequest("Usuário " + i, "user" + i + "@email.com");
            quoteRequestRepository.save(quote);
        }

        // When - Busca página 0 com tamanho 10
        Pageable pageable = PageRequest.of(0, 10);
        Page<QuoteRequest> result = quoteRequestRepository.findAllByDeletedAtIsNull(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(0); // Página atual
        assertThat(result.getNumberOfElements()).isEqualTo(10); // Itens nesta página
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar segunda página com 5 itens restantes")
    void deveRetornarSegundaPaginaCorretamente() {
        // Given - Cria 15 quotes
        for (int i = 1; i <= 15; i++) {
            QuoteRequest quote = criarQuoteRequest("Usuário " + i, "user" + i + "@email.com");
            quoteRequestRepository.save(quote);
        }

        // When - Busca página 1 (segunda página) com tamanho 10
        Pageable pageable = PageRequest.of(1, 10);
        Page<QuoteRequest> result = quoteRequestRepository.findAllByDeletedAtIsNull(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getNumberOfElements()).isEqualTo(5); // Apenas 5 restantes
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();
    }

    @Test
    @DisplayName("Deve buscar QuoteRequest por ID")
    void deveBuscarPorId() {
        // Given
        QuoteRequest quote = criarQuoteRequest("Fernanda", "fernanda@email.com");
        QuoteRequest savedQuote = quoteRequestRepository.save(quote);

        // When
        QuoteRequest found = quoteRequestRepository.findById(savedQuote.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getRequesterName()).isEqualTo("Fernanda");
    }

    @Test
    @DisplayName("Deve retornar vazio quando buscar ID inexistente")
    void deveRetornarVazioQuandoIdNaoExiste() {
        // Given
        UUID idInexistente = UUID.randomUUID();

        // When
        var result = quoteRequestRepository.findById(idInexistente);

        // Then
        assertThat(result).isEmpty();
    }

    // ===== MÉTODO AUXILIAR =====

    private QuoteRequest criarQuoteRequest(String nome, String email) {
        QuoteRequest quote = new QuoteRequest();
        quote.setId(UUID.randomUUID());
        quote.setBudgetType(budgetType);
        quote.setRequesterName(nome);
        quote.setRequesterEmail(email);
        quote.setDocumentOriginalName("documento.pdf");
        quote.setDocumentStorageKey("storage/key/" + UUID.randomUUID());
        quote.setDocumentMimeType("application/pdf");
        quote.setDocumentSizeBytes(1024L);
        quote.setBillingMethodUsed("WORD");
        quote.setFeeUsed(BigDecimal.valueOf(0.25));
        quote.setCountedUnits(1000);
        quote.setEstimatedTotal(BigDecimal.valueOf(250.0));
        quote.setStatus(QuoteStatus.PENDING);
        quote.setCreatedAt(OffsetDateTime.now());
        quote.setUpdatedAt(OffsetDateTime.now());
        return quote;
    }
}
