-- ========================================
-- Insere um tipo de orçamento padrão para ambiente de desenvolvimento
-- ========================================
INSERT INTO budget_types (budget_type_name, billing_method, fee, target_email)
VALUES ('Por palavra', 'WORD', 0.10, 'empresa@teste.com')
ON CONFLICT DO NOTHING;