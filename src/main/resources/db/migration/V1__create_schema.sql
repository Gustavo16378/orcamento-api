-- Cria extensão para geração de UUIDs aleatórios
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Cria o tipo ENUM para métodos de cobrança, se ainda não existir
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'billing_method') THEN
    CREATE TYPE billing_method AS ENUM ('WORD', 'PARAGRAPH', 'CHARACTER', 'PAGE');
  END IF;
END $$;

-- ========================================
-- Tabela de tipos de orçamento (tabela de configuração dos serviços)
-- ========================================
CREATE TABLE IF NOT EXISTS budget_types (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),          -- Identificador universal único
  budget_type_name varchar(100) NOT NULL,                 -- Nome do tipo de orçamento
  billing_method billing_method NOT NULL,                  -- Método de cobrança (enum)
  fee numeric(12,2) NOT NULL DEFAULT 0.00,                -- Valor padrão da taxa para esse tipo
  description text NULL,                                  -- Descrição opcional do tipo
  target_email varchar(254) NOT NULL,                     -- Email para receber o pedido
  created_at timestamptz NOT NULL DEFAULT now(),          -- Data/hora de criação
  updated_at timestamptz NOT NULL DEFAULT now(),          -- Data/hora da última atualização
  deleted_at timestamptz NULL                             -- Data/hora da exclusão (soft delete)
);

-- Índices para melhorar buscas por campos usados em filtros
CREATE INDEX IF NOT EXISTS idx_budget_types_deleted_at ON budget_types (deleted_at);
CREATE INDEX IF NOT EXISTS idx_budget_types_method ON budget_types (billing_method);

-- ========================================
-- Tabela de pedidos de orçamento (transações)
-- ========================================
CREATE TABLE IF NOT EXISTS quote_requests (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),              -- Identificador universal único
  budget_type_id uuid NOT NULL REFERENCES budget_types(id),   -- FK para tipo de orçamento

  requester_name varchar(150) NOT NULL,                       -- Nome de quem pediu
  requester_email varchar(254) NULL,                          -- E-mail do solicitante

  document_original_name varchar(255) NOT NULL,               -- Nome original do arquivo
  document_storage_key varchar(500) NOT NULL,                 -- Chave/ID do arquivo no storage
  document_mime_type varchar(100) NULL,                       -- MIME type do arquivo (ex: application/pdf)
  document_size_bytes bigint NULL,                            -- Tamanho do arquivo

  billing_method_used billing_method NOT NULL,                -- Método de cobrança usado na solicitação
  fee_used numeric(12,2) NOT NULL DEFAULT 0.00,               -- Valor da taxa congelado no momento do pedido

  counted_units integer NOT NULL DEFAULT 0,                   -- Total de unidades contadas (palavras, páginas...)
  estimated_total numeric(12,2) NOT NULL DEFAULT 0.00,        -- Valor total estimado para esse pedido

  status varchar(30) NOT NULL DEFAULT 'RECEIVED',             -- Status do pedido

  created_at timestamptz NOT NULL DEFAULT now(),              -- Data/hora de criação
  updated_at timestamptz NOT NULL DEFAULT now(),              -- Data/hora da última atualização
  deleted_at timestamptz NULL                                 -- Data/hora da exclusão (soft delete)
);

-- Índices para filtros comuns
CREATE INDEX IF NOT EXISTS idx_quote_requests_budget_type_id ON quote_requests (budget_type_id);
CREATE INDEX IF NOT EXISTS idx_quote_requests_status ON quote_requests (status);
CREATE INDEX IF NOT EXISTS idx_quote_requests_deleted_at ON quote_requests (deleted_at);
