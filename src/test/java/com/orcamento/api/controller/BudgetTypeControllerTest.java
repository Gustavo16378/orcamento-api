package com.orcamento.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orcamento.api.dto.BudgetTypeDTO;
import com.orcamento.api.service.BudgetTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetTypeController.class)
@DisplayName("Testes do BudgetTypeController")
class BudgetTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BudgetTypeService service;

    private UUID budgetTypeId;
    private BudgetTypeDTO budgetTypeDTO;

    @BeforeEach
    void setUp() {
        budgetTypeId = UUID.randomUUID();

        budgetTypeDTO = new BudgetTypeDTO();
        budgetTypeDTO.setId(budgetTypeId);
        budgetTypeDTO.setBudgetTypeName("Tradução Juramentada");
        budgetTypeDTO.setBillingMethod("WORD");
        budgetTypeDTO.setFee(BigDecimal.valueOf(0.30));
        budgetTypeDTO.setDescription("Tradução oficial com validade legal");
        budgetTypeDTO.setTargetEmail("contato@empresa.com");
        budgetTypeDTO.setCreatedAt(OffsetDateTime.now());
        budgetTypeDTO.setUpdatedAt(OffsetDateTime.now());
    }

    @Test
    @DisplayName("GET /budget-types - Deve retornar 200 e lista de budget types")
    void deveRetornar200QuandoListar() throws Exception {
        // Given
        BudgetTypeDTO budgetType2 = new BudgetTypeDTO();
        budgetType2.setId(UUID.randomUUID());
        budgetType2.setBudgetTypeName("Tradução Técnica");
        budgetType2.setBillingMethod("PAGE");
        budgetType2.setFee(BigDecimal.valueOf(15.0));
        budgetType2.setDescription("Tradução de documentos técnicos");
        budgetType2.setTargetEmail("tecnica@empresa.com");

        when(service.getAll()).thenReturn(List.of(budgetTypeDTO, budgetType2));

        // When & Then
        mockMvc.perform(get("/budget-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].budgetTypeName", is("Tradução Juramentada")))
                .andExpect(jsonPath("$[1].budgetTypeName", is("Tradução Técnica")));

        verify(service, times(1)).getAll();
    }

    @Test
    @DisplayName("GET /budget-types - Deve retornar 200 e lista vazia")
    void deveRetornar200ComListaVazia() throws Exception {
        // Given
        when(service.getAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/budget-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(service, times(1)).getAll();
    }

    @Test
    @DisplayName("GET /budget-types/{id} - Deve retornar 200 e o budget type")
    void deveRetornar200QuandoBuscarPorId() throws Exception {
        // Given
        when(service.getById(budgetTypeId)).thenReturn(budgetTypeDTO);

        // When & Then
        mockMvc.perform(get("/budget-types/{id}", budgetTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(budgetTypeId.toString())))
                .andExpect(jsonPath("$.budgetTypeName", is("Tradução Juramentada")))
                .andExpect(jsonPath("$.billingMethod", is("WORD")))
                .andExpect(jsonPath("$.fee", is(0.30)));

        verify(service, times(1)).getById(budgetTypeId);
    }

    @Test
    @DisplayName("GET /budget-types/{id} - Deve retornar 404 quando não encontrar")
    void deveRetornar404QuandoNaoEncontrar() throws Exception {
        // Given
        when(service.getById(budgetTypeId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/budget-types/{id}", budgetTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getById(budgetTypeId);
    }

    @Test
    @DisplayName("GET /budget-types/deleted - Deve retornar 200 e budget types deletados")
    void deveRetornar200QuandoListarDeletados() throws Exception {
        // Given
        budgetTypeDTO.setDeletedAt(OffsetDateTime.now());
        when(service.getAllDeleted()).thenReturn(List.of(budgetTypeDTO));

        // When & Then
        mockMvc.perform(get("/budget-types/deleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].deletedAt", notNullValue()));

        verify(service, times(1)).getAllDeleted();
    }

    @Test
    @DisplayName("POST /budget-types - Deve retornar 201 ao criar")
    void deveRetornar201QuandoCriar() throws Exception {
        // Given
        BudgetTypeDTO inputDTO = new BudgetTypeDTO();
        inputDTO.setBudgetTypeName("Nova Tradução");
        inputDTO.setBillingMethod("PARAGRAPH");
        inputDTO.setFee(BigDecimal.valueOf(5.0));
        inputDTO.setDescription("Descrição teste");
        inputDTO.setTargetEmail("novo@empresa.com");

        BudgetTypeDTO createdDTO = new BudgetTypeDTO();
        createdDTO.setId(UUID.randomUUID());
        createdDTO.setBudgetTypeName("Nova Tradução");
        createdDTO.setBillingMethod("PARAGRAPH");
        createdDTO.setFee(BigDecimal.valueOf(5.0));
        createdDTO.setDescription("Descrição teste");
        createdDTO.setTargetEmail("novo@empresa.com");
        createdDTO.setCreatedAt(OffsetDateTime.now());
        createdDTO.setUpdatedAt(OffsetDateTime.now());

        when(service.create(any(BudgetTypeDTO.class))).thenReturn(createdDTO);

        // When & Then
        mockMvc.perform(post("/budget-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.budgetTypeName", is("Nova Tradução")))
                .andExpect(jsonPath("$.billingMethod", is("PARAGRAPH")))
                .andExpect(jsonPath("$.fee", is(5.0)));

        verify(service, times(1)).create(any(BudgetTypeDTO.class));
    }

    @Test
    @DisplayName("POST /budget-types - Deve retornar 400 com dados inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        // Given - DTO sem campos obrigatórios
        BudgetTypeDTO invalidDTO = new BudgetTypeDTO();
        // Não seta nada

        // When & Then
        mockMvc.perform(post("/budget-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any(BudgetTypeDTO.class));
    }

    @Test
    @DisplayName("POST /budget-types - Deve retornar 400 com email inválido")
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        // Given
        BudgetTypeDTO invalidDTO = new BudgetTypeDTO();
        invalidDTO.setBudgetTypeName("Teste");
        invalidDTO.setBillingMethod("WORD");
        invalidDTO.setFee(BigDecimal.valueOf(0.25));
        invalidDTO.setDescription("Descrição");
        invalidDTO.setTargetEmail("email-invalido"); // ← Email inválido

        // When & Then
        mockMvc.perform(post("/budget-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.targetEmail", containsString("e-mail")));

        verify(service, never()).create(any(BudgetTypeDTO.class));
    }

    @Test
    @DisplayName("PUT /budget-types/{id} - Deve retornar 200 ao atualizar")
    void deveRetornar200QuandoAtualizar() throws Exception {
        // Given
        BudgetTypeDTO updateDTO = new BudgetTypeDTO();
        updateDTO.setBudgetTypeName("Tradução Juramentada Atualizada");
        updateDTO.setBillingMethod("PAGE");
        updateDTO.setFee(BigDecimal.valueOf(20.0));
        updateDTO.setDescription("Nova descrição");
        updateDTO.setTargetEmail("atualizado@empresa.com");

        BudgetTypeDTO updatedDTO = new BudgetTypeDTO();
        updatedDTO.setId(budgetTypeId);
        updatedDTO.setBudgetTypeName("Tradução Juramentada Atualizada");
        updatedDTO.setBillingMethod("PAGE");
        updatedDTO.setFee(BigDecimal.valueOf(20.0));
        updatedDTO.setDescription("Nova descrição");
        updatedDTO.setTargetEmail("atualizado@empresa.com");
        updatedDTO.setCreatedAt(OffsetDateTime.now());
        updatedDTO.setUpdatedAt(OffsetDateTime.now());

        when(service.update(eq(budgetTypeId), any(BudgetTypeDTO.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/budget-types/{id}", budgetTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budgetTypeName", is("Tradução Juramentada Atualizada")))
                .andExpect(jsonPath("$.billingMethod", is("PAGE")))
                .andExpect(jsonPath("$.fee", is(20.0)));

        verify(service, times(1)).update(eq(budgetTypeId), any(BudgetTypeDTO.class));
    }

    @Test
    @DisplayName("PUT /budget-types/{id} - Deve retornar 404 quando não encontrar")
    void deveRetornar404QuandoAtualizarInexistente() throws Exception {
        // Given
        BudgetTypeDTO updateDTO = new BudgetTypeDTO();
        updateDTO.setBudgetTypeName("Teste");
        updateDTO.setBillingMethod("WORD");
        updateDTO.setFee(BigDecimal.valueOf(0.25));
        updateDTO.setDescription("Descrição");
        updateDTO.setTargetEmail("teste@empresa.com");

        when(service.update(eq(budgetTypeId), any(BudgetTypeDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/budget-types/{id}", budgetTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(service, times(1)).update(eq(budgetTypeId), any(BudgetTypeDTO.class));
    }

    @Test
    @DisplayName("PUT /budget-types/{id} - Deve retornar 400 com dados inválidos")
    void deveRetornar400QuandoAtualizarComDadosInvalidos() throws Exception {
        // Given - DTO inválido
        BudgetTypeDTO invalidDTO = new BudgetTypeDTO();

        // When & Then
        mockMvc.perform(put("/budget-types/{id}", budgetTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).update(any(UUID.class), any(BudgetTypeDTO.class));
    }

    @Test
    @DisplayName("DELETE /budget-types/{id} - Deve retornar 204 ao deletar")
    void deveRetornar204QuandoDeletar() throws Exception {
        // Given
        doNothing().when(service).softDelete(budgetTypeId);

        // When & Then
        mockMvc.perform(delete("/budget-types/{id}", budgetTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).softDelete(budgetTypeId);
    }

    @Test
    @DisplayName("DELETE /budget-types/{id} - Deve retornar 500 quando não encontrar")
    void deveRetornar500QuandoDeletarInexistente() throws Exception {
        // Given
        doThrow(new RuntimeException("BudgetType não encontrado ou já deletado."))
                .when(service).softDelete(budgetTypeId);

        // When & Then
        mockMvc.perform(delete("/budget-types/{id}", budgetTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).softDelete(budgetTypeId);
    }
}