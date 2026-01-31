# Orçamento API - Sistema de Orçamentos para Traduç��o

API REST com Spring Boot/Java e PostgreSQL para gerenciar orçamentos de tradução (quotes) vinculados a tipos de orçamento (budgets), com suporte a diferentes métodos de cobrança (palavra, página, parágrafo, etc).

---

## 🚀 Fluxo do Projeto

1. **Cadastro dos Tipos de Orçamento (`budget-types`)**
   - Exemplo: "Por Palavra", "Por Página", "Por Parágrafo", "Por Caractere".
   - Cada type tem seu preço (`fee`), forma de cobrança (`billingMethod`), e-mail alvo (`targetEmail`) etc.

2. **Cliente faz um Orçamento (`quote-requests`)**
   - Usuário preenche nome, e-mail, anexa documento e escolhe o tipo de orçamento.
   - API recebe, relaciona ao tipo (`budgetType`) e calcula valor estimado automaticamente.

3. **Fluxo Geral**
   - Orçamento fica com status `RECEIVED`, esperando análise.
   - Após análise (manual ou automática), status pode ser alterado para `APPROVED`, `REJECTED`, etc.

---

## 🧑‍💻 Métodos Disponíveis (CRUD)

### Budget Type (`/budget-types`)
- `GET /budget-types` - Lista todos os tipos disponíveis
- `POST /budget-types` - Cria novo tipo
- `PUT /budget-types/{id}` - Atualiza tipo existente
- `DELETE /budget-types/{id}` - Remove tipo
  
### Quote Request (`/quote-requests`)
- `GET /quote-requests` - Lista todas as cotações
- `POST /quote-requests` - Cria nova cotação
- `PUT /quote-requests/{id}` - Atualiza cotação por id
- `DELETE /quote-requests/{id}` - Exclui cotação

*(Todos aceitam/retornam JSON e seguem padrões REST.)*

---

## ⚙️ Abordagens e Boas Práticas

- **DTOs**:  
  - Usados para flexibilizar a entrada/saída (ex: aceitar siglas "W", "PG" além do enum).
  - Possível evoluir para ResponseDTO para controlar melhor o que retorna.
- **Entidades com @CreationTimestamp/@UpdateTimestamp**:  
  - Campos de data (created, updated) automáticos e seguros contra nulos.
- **Relacionamentos claros**:  
  - quote_request sempre referencia um budget_type por id (foreign key).
- **Validação**:  
  - Boa parte vai pelo próprio banco (`nullable = false`), possíveis melhorias com Bean Validation (não obrigatório inicialmente).
- **Paginação**:  
  - Pode ser implementada fácil usando Spring Pageable (`?page=N&size=M`).

---

## 🧪 Como testar na máquina

1. **Pré-requisitos**  
   - Java 17+  
   - Maven  
   - PostgreSQL  
   - [Opcional] Postman ou Insomnia (para requisições)

2. **Clonar e Rodar**
   ```bash
   git clone https://github.com/seu-usuario/seu-projeto.git
   cd seu-projeto
   # Configurar application.properties conforme conexão local do Postgres
   mvn spring-boot:run
   ```

3. **Criar tipos de orçamento**
   - Endpoint: `POST /budget-types`
   - Body exemplo:
   ```json
   {
     "budgetTypeName": "Por Palavra",
     "billingMethod": "WORD",
     "fee": 0.30,
     "description": "Cobra por palavra traduzida",
     "targetEmail": "orcamento@empresa.com"
   }
   ```

4. **Criar orçamento**
   - Endpoint: `POST /quote-requests`
   - Body exemplo:
   ```json
   {
     "budgetType": { "id": "ID_COPIADO_DO_BUDGET" },
     "requesterName": "Fulano",
     "requesterEmail": "fulano@email.com",
     "documentOriginalName": "txt.pdf",
     "documentStorageKey": "arquivo-123",
     "documentMimeType": "application/pdf",
     "documentSizeBytes": 1024,
     "billingMethodUsed": "WORD",
     "feeUsed": 0.30,
     "countedUnits": 1500,
     "estimatedTotal": 450.00,
     "status": "RECEIVED"
   }
   ```

5. **Listar e testar outros endpoints**
   - Basta fazer GET/PUT/DELETE pelos endpoints listados acima.

---

## 💡 Dicas

- **Se aparecer erro de campo nulo (`created_at`)**, confira se anotou as entidades com `@CreationTimestamp`/`@UpdateTimestamp`.
- **Para exibir os dados completos do budgetType nas respostas dos quotes**, use `FetchType.EAGER` no relacionamento em `QuoteRequest`.

---

## 📌 Sprint/Backlog

- [ ] Implementar paginação (GET paginado)
- [ ] Melhorar validação de entrada com Bean Validation
- [ ] Criar testes automáticos (JUnit/MockMvc)
- [ ] Adicionar autenticação caso necessário

---

Qualquer dúvida, sugestão ou bug, só abrir issue no repo!  
Bons testes! 🚀
