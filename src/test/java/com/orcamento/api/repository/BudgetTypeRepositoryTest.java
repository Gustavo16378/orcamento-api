package com.orcamento.api.repository;

import com.orcamento.api.entity.BudgetType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import com.orcamento.api.entity.enums.BillingMethod;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do BudgetTypeRepository")
class BudgetTypeRepositoryTest {

    @Autowired
    private BudgetTypeRepository budgetTypeRepository;

    @BeforeEach
    void setUp() {
        budgetTypeRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar um BudgetType no banco")
    void deveSalvarBudgetType() {
        // Given
        BudgetType budgetType = criarBudgetType("Tradução Juramentada", BillingMethod.WORD, 0.30);

        // When
        BudgetType saved = budgetTypeRepository.save(budgetType);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBudgetTypeName()).isEqualTo("Tradução Juramentada");
        assertThat(saved.getBillingMethod()).isEqualTo(BillingMethod.WORD);
        assertThat(saved.getFee()).isEqualByComparingTo(BigDecimal.valueOf(0.30));
    }

    @Test
    @DisplayName("Deve buscar apenas BudgetTypes não deletados")
    void deveBuscarApenasNaoDeletados() {
        // Given
        BudgetType type1 = criarBudgetType("Tipo 1", BillingMethod.WORD, 0.25);
        BudgetType type2 = criarBudgetType("Tipo 2", BillingMethod.PAGE, 10.0);
        BudgetType type3 = criarBudgetType("Tipo 3", BillingMethod.PARAGRAPH, 5.0);
        
        budgetTypeRepository.save(type1);
        budgetTypeRepository.save(type2);
        type3.setDeletedAt(OffsetDateTime.now());
        budgetTypeRepository.save(type3);

        // When
        List<BudgetType> result = budgetTypeRepository.findAllByDeletedAtIsNull();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(BudgetType::getBudgetTypeName)
                .containsExactlyInAnyOrder("Tipo 1", "Tipo 2");
    }

    @Test
    @DisplayName("Deve buscar apenas BudgetTypes deletados")
    void deveBuscarApenasDeletados() {
        // Given
        BudgetType type1 = criarBudgetType("Ativo", BillingMethod.WORD, 0.25);
        BudgetType type2 = criarBudgetType("Deletado 1", BillingMethod.PAGE, 10.0);
        BudgetType type3 = criarBudgetType("Deletado 2", BillingMethod.CHARACTER, 0.05);
        
        budgetTypeRepository.save(type1);
        type2.setDeletedAt(OffsetDateTime.now());
        budgetTypeRepository.save(type2);
        type3.setDeletedAt(OffsetDateTime.now());
        budgetTypeRepository.save(type3);

        // When
        List<BudgetType> result = budgetTypeRepository.findAllByDeletedAtIsNotNull();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(BudgetType::getBudgetTypeName)
                .containsExactlyInAnyOrder("Deletado 1", "Deletado 2");
    }

    @Test
    @DisplayName("Deve buscar BudgetType por ID")
    void deveBuscarPorId() {
        // Given
        BudgetType budgetType = criarBudgetType("Tradução Técnica", BillingMethod.PAGE, 15.0);
        BudgetType saved = budgetTypeRepository.save(budgetType);

        // When
        BudgetType found = budgetTypeRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getBudgetTypeName()).isEqualTo("Tradução Técnica");
        assertThat(found.getFee()).isEqualByComparingTo(BigDecimal.valueOf(15.0));
    }

    @Test
    @DisplayName("Deve retornar vazio quando buscar ID inexistente")
    void deveRetornarVazioQuandoIdNaoExiste() {
        // Given
        UUID idInexistente = UUID.randomUUID();

        // When
        var result = budgetTypeRepository.findById(idInexistente);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver BudgetTypes")
    void deveRetornarListaVaziaQuandoNaoHouverDados() {
        // When
        List<BudgetType> result = budgetTypeRepository.findAllByDeletedAtIsNull();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar BudgetType existente")
    void deveAtualizarBudgetType() {
        // Given
        BudgetType budgetType = criarBudgetType("Nome Antigo", BillingMethod.WORD, 0.20);
        BudgetType saved = budgetTypeRepository.save(budgetType);

        // When
        saved.setBudgetTypeName("Nome Novo");
        saved.setFee(BigDecimal.valueOf(0.35));
        saved.setUpdatedAt(OffsetDateTime.now());
        BudgetType updated = budgetTypeRepository.save(saved);

        // Then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getBudgetTypeName()).isEqualTo("Nome Novo");
        assertThat(updated.getFee()).isEqualByComparingTo(BigDecimal.valueOf(0.35));
    }


    private BudgetType criarBudgetType(String nome, BillingMethod method, double fee) {
        BudgetType budgetType = new BudgetType();
        budgetType.setId(UUID.randomUUID());
        budgetType.setBudgetTypeName(nome);
        budgetType.setBillingMethod(method);
        budgetType.setFee(BigDecimal.valueOf(fee));
        budgetType.setDescription("Descrição de teste");
        budgetType.setTargetEmail("teste@email.com");
        budgetType.setCreatedAt(OffsetDateTime.now());
        budgetType.setUpdatedAt(OffsetDateTime.now());
        return budgetType;
    }
}