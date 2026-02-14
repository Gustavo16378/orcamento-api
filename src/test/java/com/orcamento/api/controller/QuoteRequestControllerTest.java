package com.orcamento.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orcamento.api.dto.QuoteRequestDTO;
import com.orcamento.api.service.QuoteRequestService;
import com.orcamento.api.entity.enums.QuoteStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@WebMvcTest(QuoteRequestController.class)
@DisplayName("Testes do QuoteRequestController")
class QuoteRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuoteRequestService service;

    private UUID quoteRequestId;
    private UUID budgetTypeId;
    private QuoteRequestDTO quoteRequestDTO;

    @BeforeEach
    void setUp() {
        quoteRequestId = UUID.randomUUID();
        budgetTypeId = UUID.randomUUID();

        quoteRequestDTO = new QuoteRequestDTO();
        quoteRequestDTO.setId(quoteRequestId);
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
        quoteRequestDTO.setStatus(QuoteStatus.RECEIVED);
        quoteRequestDTO.setCreatedAt(OffsetDateTime.now());
        quoteRequestDTO.setUpdatedAt(OffsetDateTime.now());
    }

    @Test
    @DisplayName("GET /quote-requests - Deve retornar 200 e página de quotes")
    void deveRetornar200QuandoListarComPaginacao() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<QuoteRequestDTO> page = new PageImpl<>(List.of(quoteRequestDTO), pageable, 1);
        
        when(service.getAllPaginated(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/quote-requests")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].requesterName", is("João Silva")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.size", is(10)));

        verify(service, times(1)).getAllPaginated(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /quote-requests - Deve usar valores padrão de paginação")
    void deveUsarValoresPadraoDePaginacao() throws Exception {
        // Given
        Page<QuoteRequestDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(service.getAllPaginated(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/quote-requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(service, times(1)).getAllPaginated(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /quote-requests/deleted - Deve retornar 200 e quotes deletadas")
    void deveRetornar200QuandoListarDeletadas() throws Exception {
        // Given
        quoteRequestDTO.setDeletedAt(OffsetDateTime.now());
        Page<QuoteRequestDTO> page = new PageImpl<>(List.of(quoteRequestDTO), PageRequest.of(0, 10), 1);
        
        when(service.getAllDeletedPaginated(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/quote-requests/deleted")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].deletedAt", notNullValue()));

        verify(service, times(1)).getAllDeletedPaginated(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /quote-requests/{id} - Deve retornar 200 e a quote")
    void deveRetornar200QuandoBuscarPorId() throws Exception {
        // Given
        when(service.getById(quoteRequestId)).thenReturn(quoteRequestDTO);

        // When & Then
        mockMvc.perform(get("/quote-requests/{id}", quoteRequestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(quoteRequestId.toString())))
                .andExpect(jsonPath("$.requesterName", is("João Silva")))
                .andExpect(jsonPath("$.requesterEmail", is("joao@email.com")));

        verify(service, times(1)).getById(quoteRequestId);
    }

    @Test
    @DisplayName("GET /quote-requests/{id} - Deve retornar 500 quando não encontrar")
    void deveRetornar500QuandoNaoEncontrar() throws Exception {
        // Given
        when(service.getById(quoteRequestId))
                .thenThrow(new RuntimeException("Solicitação não encontrada ou foi deletada."));

        // When & Then
        mockMvc.perform(get("/quote-requests/{id}", quoteRequestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).getById(quoteRequestId);
    }

    @Test
    @DisplayName("POST /quote-requests - Deve retornar 201 ao criar")
    void deveRetornar201QuandoCriar() throws Exception {
        // Given
        QuoteRequestDTO inputDTO = new QuoteRequestDTO();
        inputDTO.setBudgetTypeId(budgetTypeId);
        inputDTO.setRequesterName("Maria Santos");
        inputDTO.setRequesterEmail("maria@email.com");
        inputDTO.setDocumentOriginalName("documento.pdf");
        inputDTO.setDocumentStorageKey("storage/key/456");
        inputDTO.setDocumentMimeType("application/pdf");
        inputDTO.setDocumentSizeBytes(2048L);
        inputDTO.setBillingMethodUsed("WORD");
        inputDTO.setFeeUsed(BigDecimal.valueOf(0.25));
        inputDTO.setCountedUnits(500);
        inputDTO.setEstimatedTotal(BigDecimal.valueOf(125.0));
        inputDTO.setStatus(QuoteStatus.RECEIVED);

        QuoteRequestDTO createdDTO = new QuoteRequestDTO();
        createdDTO.setId(UUID.randomUUID());
        createdDTO.setBudgetTypeId(budgetTypeId);
        createdDTO.setRequesterName("Maria Santos");
        createdDTO.setRequesterEmail("maria@email.com");
        createdDTO.setDocumentOriginalName("documento.pdf");
        createdDTO.setDocumentStorageKey("storage/key/456");
        createdDTO.setDocumentMimeType("application/pdf");
        createdDTO.setDocumentSizeBytes(2048L);
        createdDTO.setBillingMethodUsed("WORD");
        createdDTO.setFeeUsed(BigDecimal.valueOf(0.25));
        createdDTO.setCountedUnits(500);
        createdDTO.setEstimatedTotal(BigDecimal.valueOf(125.0));
        createdDTO.setStatus(QuoteStatus.RECEIVED);

        when(service.create(any(QuoteRequestDTO.class))).thenReturn(createdDTO);

        // When & Then
        mockMvc.perform(post("/quote-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.requesterName", is("Maria Santos")))
                .andExpect(jsonPath("$.requesterEmail", is("maria@email.com")));

        verify(service, times(1)).create(any(QuoteRequestDTO.class));
    }

    @Test
    @DisplayName("POST /quote-requests - Deve retornar 400 com dados inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        // Given - DTO sem campos obrigatórios
        QuoteRequestDTO invalidDTO = new QuoteRequestDTO();
        // Não seta nada (todos os campos obrigatórios faltando)

        // When & Then
        mockMvc.perform(post("/quote-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any(QuoteRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /quote-requests/{id} - Deve retornar 200 ao atualizar")
    void deveRetornar200QuandoAtualizar() throws Exception {
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
        updateDTO.setStatus(QuoteStatus.RECEIVED);

        QuoteRequestDTO updatedDTO = new QuoteRequestDTO();
        updatedDTO.setId(quoteRequestId);
        updatedDTO.setBudgetTypeId(budgetTypeId);
        updatedDTO.setRequesterName("João Silva Atualizado");
        updatedDTO.setRequesterEmail("joao.novo@email.com");
        updatedDTO.setDocumentOriginalName("contrato.pdf");
        updatedDTO.setDocumentStorageKey("storage/key/123");
        updatedDTO.setDocumentMimeType("application/pdf");
        updatedDTO.setDocumentSizeBytes(1024L);
        updatedDTO.setBillingMethodUsed("WORD");
        updatedDTO.setFeeUsed(BigDecimal.valueOf(0.25));
        updatedDTO.setCountedUnits(1000);
        updatedDTO.setEstimatedTotal(BigDecimal.valueOf(250.0));
        updatedDTO.setStatus(QuoteStatus.RECEIVED);

        when(service.update(eq(quoteRequestId), any(QuoteRequestDTO.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/quote-requests/{id}", quoteRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requesterName", is("João Silva Atualizado")))
                .andExpect(jsonPath("$.status", is("RECEIVED")));

        verify(service, times(1)).update(eq(quoteRequestId), any(QuoteRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /quote-requests/{id} - Deve retornar 400 com dados inválidos")
    void deveRetornar400QuandoAtualizarComDadosInvalidos() throws Exception {
        // Given - DTO inválido
        QuoteRequestDTO invalidDTO = new QuoteRequestDTO();

        // When & Then
        mockMvc.perform(put("/quote-requests/{id}", quoteRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).update(any(UUID.class), any(QuoteRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /quote-requests/{id} - Deve retornar 204 ao deletar")
    void deveRetornar204QuandoDeletar() throws Exception {
        // Given
        doNothing().when(service).softDelete(quoteRequestId);

        // When & Then
        mockMvc.perform(delete("/quote-requests/{id}", quoteRequestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).softDelete(quoteRequestId);
    }

    @Test
    @DisplayName("DELETE /quote-requests/{id} - Deve retornar 500 quando não encontrar")
    void deveRetornar500QuandoDeletarInexistente() throws Exception {
        // Given
        doThrow(new RuntimeException("Solicitação não encontrada ou já deletada."))
                .when(service).softDelete(quoteRequestId);

        // When & Then
        mockMvc.perform(delete("/quote-requests/{id}", quoteRequestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).softDelete(quoteRequestId);
    }

    @Test
    @DisplayName("GET /quote-requests/all - Deve retornar 200 (DEPRECATED)")
    void deveRetornar200QuandoListarTodos() throws Exception {
        // Given
        when(service.getAll()).thenReturn(List.of(quoteRequestDTO));

        // When & Then
        mockMvc.perform(get("/quote-requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].requesterName", is("João Silva")));

        verify(service, times(1)).getAll();
    }
}