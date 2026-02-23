package com.orcamento.api.service;

import com.orcamento.api.dto.BudgetTypeDTO;
import com.orcamento.api.entity.BudgetType;
import com.orcamento.api.repository.BudgetTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.orcamento.api.entity.enums.BillingMethod;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do BudgetTypeService")
class BudgetTypeServiceTest {

    @Mock
    private BudgetTypeRepository budgetTypeRepository;

    @InjectMocks
    private BudgetTypeService service;

    private UUID budgetTypeId;
    private BudgetType budgetType;
    private BudgetTypeDTO budgetTypeDTO;

    @BeforeEach
    void setUp() {
        budgetTypeId = UUID.randomUUID();

        // Cria BudgetType mock
        budgetType = new BudgetType();
        budgetType.setId(budgetTypeId);
        budgetType.setBudgetTypeName("Tradução Juramentada");
        budgetType.setBillingMethod(BillingMethod.WORD);
        budgetType.setFee(BigDecimal.valueOf(0.30));
        budgetType.setDescription("Tradução oficial");
        budgetType.setTargetEmail("contato@empresa.com");
        budgetType.setCreatedAt(OffsetDateTime.now());
        budgetType.setUpdatedAt(OffsetDateTime.now());
        budgetType.setDeletedAt(null);

        // Cria DTO
        budgetTypeDTO = new BudgetTypeDTO();
        budgetTypeDTO.setBudgetTypeName("Tradução Juramentada");
        budgetTypeDTO.setBillingMethod(BillingMethod.WORD);
        budgetTypeDTO.setFee(BigDecimal.valueOf(0.30));
        budgetTypeDTO.setDescription("Tradução oficial");
        budgetTypeDTO.setTargetEmail("contato@empresa.com");
    }

    @Test
    @DisplayName("Deve criar BudgetType com sucesso")
    void deveCriarBudgetTypeComSucesso() {
        // Given
        when(budgetTypeRepository.save(any(BudgetType.class))).thenReturn(budgetType);

        // When
        BudgetTypeDTO result = service.create(budgetTypeDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBudgetTypeName()).isEqualTo("Tradução Juramentada");
        assertThat(result.getBillingMethod()).isEqualTo(BillingMethod.WORD);
        assertThat(result.getFee()).isEqualByComparingTo(BigDecimal.valueOf(0.30));
        
        verify(budgetTypeRepository, times(1)).save(any(BudgetType.class));
    }

    @Test
    @DisplayName("Deve buscar BudgetType por ID com sucesso")
    void deveBuscarBudgetTypePorId() {
        // Given
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.of(budgetType));

        // When
        BudgetTypeDTO result = service.getById(budgetTypeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBudgetTypeName()).isEqualTo("Tradução Juramentada");
        assertThat(result.getId()).isEqualTo(budgetTypeId);
        
        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar BudgetType inexistente")
    void deveLancarExcecaoQuandoBudgetTypeNaoExiste() {
        // Given
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.getById(budgetTypeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("BudgetType não encontrado");

        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar BudgetType deletado")
    void deveLancarExcecaoQuandoBudgetTypeDeletado() {
        // Given
        budgetType.setDeletedAt(OffsetDateTime.now());
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.of(budgetType));

        // When & Then
        assertThatThrownBy(() -> service.getById(budgetTypeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("BudgetType não encontrado");

        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
    }

    @Test
    @DisplayName("Deve listar todos os BudgetTypes não deletados")
    void deveListarTodosBudgetTypes() {
        // Given
        BudgetType budgetType2 = new BudgetType();
        budgetType2.setId(UUID.randomUUID());
        budgetType2.setBudgetTypeName("Tradução Técnica");
        budgetType2.setBillingMethod(BillingMethod.PAGE);
        budgetType2.setFee(BigDecimal.valueOf(15.0));
        budgetType2.setDescription("Tradução técnica");
        budgetType2.setTargetEmail("tecnica@empresa.com");
        budgetType2.setCreatedAt(OffsetDateTime.now());
        budgetType2.setUpdatedAt(OffsetDateTime.now());

        when(budgetTypeRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(budgetType, budgetType2));

        // When
        List<BudgetTypeDTO> result = service.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(BudgetTypeDTO::getBudgetTypeName)
                .containsExactlyInAnyOrder("Tradução Juramentada", "Tradução Técnica");
        
        verify(budgetTypeRepository, times(1)).findAllByDeletedAtIsNull();
    }

    @Test
    @DisplayName("Deve listar BudgetTypes deletados")
    void deveListarBudgetTypesDeletados() {
        // Given
        budgetType.setDeletedAt(OffsetDateTime.now());
        when(budgetTypeRepository.findAllByDeletedAtIsNotNull()).thenReturn(List.of(budgetType));

        // When
        List<BudgetTypeDTO> result = service.getAllDeleted();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBudgetTypeName()).isEqualTo("Tradução Juramentada");
        assertThat(result.get(0).getDeletedAt()).isNotNull();
        
        verify(budgetTypeRepository, times(1)).findAllByDeletedAtIsNotNull();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver BudgetTypes")
    void deveRetornarListaVaziaQuandoNaoHouverDados() {
        // Given
        when(budgetTypeRepository.findAllByDeletedAtIsNull()).thenReturn(List.of());

        // When
        List<BudgetTypeDTO> result = service.getAll();

        // Then
        assertThat(result).isEmpty();
        
        verify(budgetTypeRepository, times(1)).findAllByDeletedAtIsNull();
    }

    @Test
    @DisplayName("Deve atualizar BudgetType com sucesso")
    void deveAtualizarBudgetTypeComSucesso() {
        // Given
        BudgetTypeDTO updateDTO = new BudgetTypeDTO();
        updateDTO.setBudgetTypeName("Tradução Juramentada Atualizada");
        updateDTO.setBillingMethod(BillingMethod.PAGE);
        updateDTO.setFee(BigDecimal.valueOf(20.0));
        updateDTO.setDescription("Nova descrição");
        updateDTO.setTargetEmail("novo@empresa.com");

        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.of(budgetType));
        when(budgetTypeRepository.save(any(BudgetType.class))).thenReturn(budgetType);

        // When
        BudgetTypeDTO result = service.update(budgetTypeId, updateDTO);

        // Then
        assertThat(result).isNotNull();
        
        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(budgetTypeRepository, times(1)).save(any(BudgetType.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar BudgetType inexistente")
    void deveLancarExcecaoAoAtualizarBudgetTypeInexistente() {
        // Given
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.update(budgetTypeId, budgetTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("BudgetType não encontrado");

        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(budgetTypeRepository, never()).save(any(BudgetType.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar BudgetType deletado")
    void deveLancarExcecaoAoAtualizarBudgetTypeDeletado() {
        // Given
        budgetType.setDeletedAt(OffsetDateTime.now());
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.of(budgetType));

        // When & Then
        assertThatThrownBy(() -> service.update(budgetTypeId, budgetTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("BudgetType não encontrado");

        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(budgetTypeRepository, never()).save(any(BudgetType.class));
    }

    @Test
    @DisplayName("Deve fazer soft delete de BudgetType")
    void deveFazerSoftDelete() {
        // Given
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.of(budgetType));
        when(budgetTypeRepository.save(any(BudgetType.class))).thenReturn(budgetType);

        // When
        service.softDelete(budgetTypeId);

        // Then
        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(budgetTypeRepository, times(1)).save(any(BudgetType.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar BudgetType inexistente")
    void deveLancarExcecaoAoDeletarBudgetTypeInexistente() {
        // Given
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.softDelete(budgetTypeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("BudgetType não encontrado");

        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(budgetTypeRepository, never()).save(any(BudgetType.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar BudgetType já deletado")
    void deveLancarExcecaoAoDeletarBudgetTypeJaDeletado() {
        // Given
        budgetType.setDeletedAt(OffsetDateTime.now());
        when(budgetTypeRepository.findById(budgetTypeId)).thenReturn(Optional.of(budgetType));

        // When & Then
        assertThatThrownBy(() -> service.softDelete(budgetTypeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("BudgetType não encontrado");

        verify(budgetTypeRepository, times(1)).findById(budgetTypeId);
        verify(budgetTypeRepository, never()).save(any(BudgetType.class));
    }
}