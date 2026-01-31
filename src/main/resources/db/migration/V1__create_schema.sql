-- Cria extensão para geração de UUIDs aleatórios
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Cria o tipo ENUM para métodos de cobrança, se ainda não existir
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'billing_method') THEN
    CREATE TYPE billing_method AS ENUM ('WORD', 'PARAGRAPH', 'CHARACTER', 'PAGE');
  END IF;
END $$;

-- Tabela de tipos de orçamento (tabela de configuração dos serviços)
CREATE TABLE IF NOT EXISTS budget_types (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  budget_type_name varchar(100) NOT NULL,
  billing_method varchar(10) NOT NULL,      -- <- AJUSTE AQUI
  fee numeric(12,2) NOT NULL DEFAULT 0.00,
  description text NULL,
  target_email varchar(254) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz NULL
);

CREATE INDEX IF NOT EXISTS idx_budget_types_deleted_at ON budget_types (deleted_at);
CREATE INDEX IF NOT EXISTS idx_budget_types_method ON budget_types (billing_method);

-- >>>>>>>>>>>>  
-- ALTERAÇÃO PRINCIPAL AQUI ↓↓↓↓
-- Tabela de pedidos de orçamento (transações)
CREATE TABLE IF NOT EXISTS quote_requests (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  budget_type_id uuid NOT NULL REFERENCES budget_types(id),

  requester_name varchar(150) NOT NULL,
  requester_email varchar(254) NULL,

  document_original_name varchar(255) NOT NULL,
  document_storage_key varchar(500) NOT NULL,
  document_mime_type varchar(100) NULL,
  document_size_bytes bigint NULL,

  billing_method varchar(10) NOT NULL,           -- Este campo é um, pode usar para API/etc

  -- ADICIONE ESTA LINHA:
  billing_method_used varchar(10) NOT NULL DEFAULT 'WORD', -- <<<<<<<<<<<<<< NOVO CAMPO PARA BATER COM SUA ENTIDADE JAVA

  fee_used numeric(12,2) NOT NULL DEFAULT 0.00,

  counted_units integer NOT NULL DEFAULT 0,
  estimated_total numeric(12,2) NOT NULL DEFAULT 0.00,

  status varchar(30) NOT NULL DEFAULT 'RECEIVED',

  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz NULL
);
-- <<<<<<<<<<<<<< ACIMA está a alteração essencial

CREATE INDEX IF NOT EXISTS idx_quote_requests_budget_type_id ON quote_requests (budget_type_id);
CREATE INDEX IF NOT EXISTS idx_quote_requests_status ON quote_requests (status);
CREATE INDEX IF NOT EXISTS idx_quote_requests_deleted_at ON quote_requests (deleted_at);