-- ========================================
-- Função para atualizar automaticamente o campo updated_at em todo UPDATE
-- ========================================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ========================================
-- Trigger para atualizar o campo updated_at na tabela budget_types
-- ========================================
DROP TRIGGER IF EXISTS trg_budget_types_updated_at ON budget_types;
CREATE TRIGGER trg_budget_types_updated_at
BEFORE UPDATE ON budget_types
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- ========================================
-- Trigger para atualizar o campo updated_at na tabela quote_requests
-- ========================================
DROP TRIGGER IF EXISTS trg_quote_requests_updated_at ON quote_requests;
CREATE TRIGGER trg_quote_requests_updated_at
BEFORE UPDATE ON quote_requests
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();