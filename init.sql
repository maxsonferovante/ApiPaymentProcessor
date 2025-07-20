-- Schema otimizado para máxima performance (reintroduzindo otimizações)
CREATE UNLOGGED TABLE payments (
    id SERIAL PRIMARY KEY,
    correlation_id UUID NOT NULL UNIQUE,
    amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    requested_at TIMESTAMP WITH TIME ZONE NOT NULL,
    payment_processor_type VARCHAR(8) NOT NULL CHECK (payment_processor_type IN ('DEFAULT', 'FALLBACK'))
);

-- Índices otimizados para queries específicas

-- 1. Índice principal para consultas de resumo (query mais frequente)
-- Covering index para evitar lookups na tabela
CREATE INDEX payments_summary_optimized 
ON payments (payment_processor_type, requested_at) 
INCLUDE (amount);

-- 2. Índice para consultas por range de tempo (cobertura total)
CREATE INDEX payments_time_range_covering 
ON payments (requested_at) 
INCLUDE (payment_processor_type, amount);

-- 3. Índice básico adicional para fallback
CREATE INDEX idx_payments_type_date ON payments (payment_processor_type, requested_at);

-- Estatísticas para otimizador (ajuda no plano de query)
ANALYZE payments;
