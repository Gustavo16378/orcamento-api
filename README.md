# 📄 Orçamento API – Sistema de Orçamentos para Tradução

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

API REST desenvolvida com **Spring Boot/Java 21** e **PostgreSQL**, para gerenciar orçamentos de tradução. Permite criar tipos de orçamento (formas de cobrança) e orçamentos vinculados, com **paginação**, soft delete e documentação automática via Swagger/OpenAPI.

---

## 🎯 **Recursos e Funcionalidades**

### ✨ **Tipos de Orçamento** (`/budget-types`)
Gerencie diferentes formas de cobrança: **Palavra**, **Página**, **Parágrafo**, **Caractere**.  
- Cada tipo tem tarifação específica (`fee`)
- Forma de faturamento (`billingMethod`)
- E-mail de destino para notificações

### 📋 **Solicitações de Orçamento** (`/quote-requests`)
- Cliente envia solicitação com seus dados
- Escolhe o tipo de orçamento
- Anexa documento (futuro: upload automático)
- **API calcula automaticamente** o valor estimado

### 🗑️ **Soft Delete**
Exclusão lógica preserva o registro para:
- Histórico completo
- Auditoria e rastreio
- Recuperação de dados

### 📄 **Paginação** ⭐ **NOVO!**
Todos os endpoints de listagem suportam paginação:
```http
GET /quote-requests?page=0&size=10&sortBy=createdAt&direction=desc
GET /quote-requests/deleted?page=0&size=10
```

**Parâmetros:**
- `page` - Número da página (começa em 0)
- `size` - Quantidade de itens por página
- `sortBy` - Campo para ordenação (ex: `createdAt`, `requesterName`)
- `direction` - Direção (`asc` ou `desc`)

### 📚 **Documentação Automática**
- Swagger UI disponível em `/swagger-ui.html`
- OpenAPI 3.0 com exemplos e descrições detalhadas

### ✅ **Validações Avançadas**
- Bean Validation (`@NotBlank`, `@Email`, `@DecimalMin`, etc)
- Mensagens customizadas para cada campo
- Tratamento global de erros

---

## 🏗️ **Arquitetura do Projeto**

```
orcamento-api/
├── src/main/java/com/orcamento/api/
│   ├── controller/           # 🎮 Endpoints REST
│   │   ├── BudgetTypeController.java
│   │   └── QuoteRequestController.java
│   ├── service/              # 💼 Lógica de negócio
│   │   ├── BudgetTypeService.java
│   │   └── QuoteRequestService.java
│   ├── repository/           # 🗄️ Acesso ao banco (JPA)
│   │   ├── BudgetTypeRepository.java
│   │   └── QuoteRequestRepository.java
│   ├── entity/               # 📦 Entidades JPA
│   │   ├── BudgetType.java
│   │   ├── QuoteRequest.java
│   │   └── enums/
│   │       └── BillingMethod.java
│   ├── dto/                  # 📝 Data Transfer Objects
│   │   ├── BudgetTypeDTO.java
│   │   └── QuoteRequestDTO.java
│   └── exception/            # ⚠️ Tratamento de erros
├── src/main/resources/
│   ├── db/migration/         # 🗃️ Scripts Flyway (SQL)
│   │   ├── V1__create_schema.sql
│   │   ├── V2__triggers.sql
│   │   └── V3__seed_dev_data.sql
│   └── application.properties
├── docker-compose.yml
├── Dockerfile
└── README.md
```

---

## 🚀 **Como Rodar o Projeto**

### **Opção 1: Com Docker (Recomendado)** 🐳

```bash
# Clone o repositório
git clone https://github.com/Gustavo16378/orcamento-api.git
cd orcamento-api

# Suba os containers (API + PostgreSQL)
docker-compose up --build

# API estará rodando em http://localhost:8080
# Swagger disponível em http://localhost:8080/swagger-ui.html
```

### **Opção 2: Rodando Localmente** 💻

**Pré-requisitos:**
- Java 21+
- Maven
- PostgreSQL rodando localmente

**Configuração:**

1. Configure o banco no `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/orcamento_api
spring.datasource.username=postgres
spring.datasource.password=sua_senha
```

2. Execute a aplicação:
```bash
./mvnw spring-boot:run
```

---

## 📚 **Endpoints da API**

### **Budget Types** (Tipos de Orçamento)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/budget-types` | Lista todos os tipos ativos |
| `GET` | `/budget-types/{id}` | Busca tipo por ID |
| `GET` | `/budget-types/deleted` | Lista tipos deletados (soft delete) |
| `POST` | `/budget-types` | Cria novo tipo |
| `PUT` | `/budget-types/{id}` | Atualiza tipo existente |
| `DELETE` | `/budget-types/{id}` | Soft delete de tipo |

### **Quote Requests** (Solicitações) ⭐ **COM PAGINAÇÃO**

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/quote-requests?page=0&size=10` | Lista solicitações com paginação |
| `GET` | `/quote-requests/{id}` | Busca solicitação por ID |
| `GET` | `/quote-requests/deleted?page=0&size=10` | Lista deletadas (paginado) |
| `POST` | `/quote-requests` | Cria nova solicitação |
| `PUT` | `/quote-requests/{id}` | Atualiza solicitação |
| `DELETE` | `/quote-requests/{id}` | Soft delete de solicitação |

---

## 📋 **Exemplos de Requisições**

### **1. Criar Tipo de Orçamento**

```http
POST /budget-types
Content-Type: application/json

{
  "budgetTypeName": "Tradução Juramentada",
  "billingMethod": "WORD",
  "fee": 0.25,
  "description": "Tradução oficial com validade legal",
  "targetEmail": "juridico@empresa.com"
}
```

**Response (201 Created):**
```json
{
  "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "budgetTypeName": "Tradução Juramentada",
  "billingMethod": "WORD",
  "fee": 0.25,
  "description": "Tradução oficial com validade legal",
  "targetEmail": "juridico@empresa.com",
  "createdAt": "2026-02-05T10:30:00Z",
  "updatedAt": "2026-02-05T10:30:00Z",
  "deletedAt": null
}
```

---

### **2. Criar Solicitação de Orçamento**

```http
POST /quote-requests
Content-Type: application/json

{
  "budgetTypeId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "requesterName": "João da Silva",
  "requesterEmail": "joao@email.com",
  "documentOriginalName": "contrato.pdf",
  "documentStorageKey": "uploads/2026/02/uuid-contrato.pdf",
  "documentMimeType": "application/pdf",
  "documentSizeBytes": 1048576,
  "billingMethodUsed": "WORD",
  "feeUsed": 0.25,
  "countedUnits": 1500,
  "estimatedTotal": 375.00,
  "status": "PENDING"
}
```

**Response (201 Created):**
```json
{
  "id": "f9e8d7c6-b5a4-3210-9876-543210fedcba",
  "budgetTypeId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "requesterName": "João da Silva",
  "requesterEmail": "joao@email.com",
  "documentOriginalName": "contrato.pdf",
  "billingMethodUsed": "WORD",
  "feeUsed": 0.25,
  "countedUnits": 1500,
  "estimatedTotal": 375.00,
  "status": "PENDING",
  "createdAt": "2026-02-05T11:00:00Z",
  "updatedAt": "2026-02-05T11:00:00Z"
}
```

---

### **3. Listar Solicitações com Paginação** ⭐

```http
GET /quote-requests?page=0&size=10&sortBy=createdAt&direction=desc
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "...",
      "requesterName": "João da Silva",
      "estimatedTotal": 375.00,
      "status": "PENDING",
      "createdAt": "2026-02-05T11:00:00Z"
    },
    // ... mais 9 itens
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 47,
  "totalPages": 5,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false,
  "numberOfElements": 10,
  "empty": false
}
```

---

## 🛠️ **Tecnologias Utilizadas**

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21 | Linguagem de programação |
| **Spring Boot** | 3.x | Framework para APIs REST |
| **Spring Data JPA** | 3.x | Abstração de persistência |
| **PostgreSQL** | 16 | Banco de dados relacional |
| **Flyway** | Latest | Migrations de banco de dados |
| **Swagger/OpenAPI** | 3.0 | Documentação automática |
| **Docker** | Latest | Containerização |
| **Maven** | 3.9+ | Gerenciamento de dependências |

---

## 🎨 **Princípios Aplicados**

- ✅ **Clean Code** - Código limpo e legível
- ✅ **SOLID** - Single Responsibility, Dependency Inversion
- ✅ **DRY** - Don't Repeat Yourself
- ✅ **RESTful** - Boas práticas de API REST
- ✅ **Separation of Concerns** - Controller → Service → Repository
- ✅ **DTO Pattern** - Separação entre Entity e dados expostos

---

## 📌 **Roadmap / Próximas Features**

- [ ] 📤 Upload automático de documentos (PDF, DOCX)
- [ ] 🧮 Processamento automático de arquivos (contagem de palavras/páginas)
- [ ] 🔐 Autenticação e autorização (JWT)
- [ ] 📧 Envio de e-mails com orçamentos
- [ ] 📊 Dashboard com estatísticas
- [ ] 🧪 Testes automatizados (JUnit + MockMvc)
- [ ] 🌐 Internacionalização (i18n)

---

## 🧪 **Testando a API**

### **Via Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

### **Via Postman/Insomnia:**
Importe a collection ou use os exemplos acima.

### **Via cURL:**
```bash
# Listar tipos de orçamento
curl http://localhost:8080/budget-types

# Listar solicitações com paginação
curl "http://localhost:8080/quote-requests?page=0&size=10"
```

---

## 👨‍💻 **Autor**

Desenvolvido por **[Gustavo](https://github.com/Gustavo16378)** 🚀

---

## 📄 **Licença**

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 🤝 **Contribuindo**

Contribuições são bem-vindas! Sinta-se à vontade para:

1. Fazer um fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abrir um Pull Request

---

## 📞 **Contato**

Dúvidas ou sugestões? Abra uma issue ou entre em contato!

---

**Bons testes e boas traduções! 🚀📄**