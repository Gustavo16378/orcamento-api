# 📄 Orçamento API – Sistema de Orçamentos para Tradução

API REST desenvolvida com **Spring Boot/Java 21** e **PostgreSQL**, para gerenciar orçamentos de tradução. Permite criar tipos de orçamento (formas de cobrança) e orçamentos vinculados, com métodos profissionais, soft delete e documentação automática via Swagger/OpenAPI.

---

## 🗃️ **Recursos e Funcionalidades**

- **Tipos de Orçamento (`budget-types`)**  
  Gerencie diferentes formas de cobrança: Palavra, Página, Parágrafo, Caractere.  
  Cada tipo tem tarifação específica (`fee`), forma de faturamento (`billingMethod`) e e-mail de destino.

- **Solicitações de Orçamento (`quote-requests`)**  
  Crie cotação, anexe documentos, escolha tipo de orçamento, e a API calcula o valor estimado.

- **Soft Delete**  
  Exclusão lógica preserva o registro para histórico e rastreio.

- **Endpoints segregados (CRUD + GET Only Deleted):**  
  - Listar, buscar por ID, criar, atualizar e "deletar" (soft delete)
  - Consultar itens deletados (`GET /budget-types/deleted`, `GET /quote-requests/deleted`)

- **Swagger UI**  
  Documentação automática em `/swagger-ui.html` ou `/swagger-ui/index.html`

- **Validação avançada**  
  Bean Validation (`@NotBlank`, `@Email`, etc), mensagens customizadas para cada campo.

- **DTOs profissionais**  
  Flexíveis, aceitam siglas/nomes, sempre retornados nas respostas.

- **Exemplo de relacionamento entre tabelas**  
  Solicitações (`QuoteRequest`) referenciam tipos (`BudgetType`) via chave estrangeira.

---

## 🚀 **Como Rodar Localmente**

### **Pré-requisitos**
- Java 21+
- Maven
- Docker e Docker Compose (**recomendado!**)
- PostgreSQL (usado via Docker já configurado)

### **Clone e Suba o Projeto**
```bash
git clone https://github.com/seu-usuario/seu-projeto.git
cd seu-projeto
docker compose up --build -d
```
> O Docker sobe banco e API já integrados – zero configuração!

### **Configuração Manual (sem Docker)**
Edite o arquivo `src/main/resources/application.properties` com sua conexão local do Postgres:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/orcamento_api
spring.datasource.username=postgres
spring.datasource.password=SuaSenha
```
Depois:
```bash
mvn spring-boot:run
```

---

## 🧑‍💻 **Endpoints Mais Usados**

### **Tipos de Orçamento**
- `GET /budget-types` — Lista todos ativos
- `GET /budget-types/deleted` — Lista todos deletados (soft delete)
- `POST /budget-types` — Cria novo tipo
- `PUT /budget-types/{id}` — Atualiza tipo existente
- `DELETE /budget-types/{id}` — Soft delete

### **Solicitações de Orçamento**
- `GET /quote-requests` — Lista todas ativas
- `GET /quote-requests/deleted` — Lista deletadas
- `POST /quote-requests` — Cria nova cotação
- `PUT /quote-requests/{id}` — Atualiza cotação
- `DELETE /quote-requests/{id}` — Soft delete

#### **Exemplo JSON: Criar Tipo de Orçamento**
```json
{
  "budgetTypeName": "Por Palavra",
  "billingMethod": "WORD", // Aceita sigla "W" caso precise
  "fee": 0.30,
  "description": "Cobra por palavra traduzida",
  "targetEmail": "orcamento@empresa.com"
}
```

#### **Exemplo JSON: Criar Orçamento**
```json
{
  "budgetTypeId": "ID_COPIADO_DO_BUDGET",
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

---

## 🛡️ **Boas Práticas Implementadas**
- **DTOs sempre expostos nas respostas**
- **Validações e tratamento global de erro**
- **Exclusão lógica (soft delete) em todos os CRUDs**
- **Documentação automática via Swagger**
- **Bean Validation com mensagens customizadas**
- **Fluxo consistente e rastreável dos orçamentos**

---

## 🧪 **Testando**
Pode testar tudo pelo Swagger (`/swagger-ui.html`), Postman, Insomnia ou qualquer cliente HTTP.

**Dica:**  
Para ver apenas os deletados, use os endpoints `/budget-types/deleted` ou `/quote-requests/deleted`.

---

## 📌 **Backlog & Melhorias Futuras**
- [ ] Paginação nos GETs (`?page=N&size=M`)
- [ ] Teste automático (JUnit/MockMvc)
- [ ] Autenticação/JWT para rotas protegidas
- [ ] Melhorar exemplos no Swagger com @Schema

---

## 💬 **Dúvidas? Sugestões?**
Abra uma issue aqui ou chame no WhatsApp do time!

---

**Bons testes e boas traduções! 🚀**