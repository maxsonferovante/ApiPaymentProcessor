package com.maal.apipaymentprocessor.domain.port.out;

import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;

import java.time.Instant;

/**
 * Port (interface) para consultas de resumo de pagamentos
 * Define o contrato que deve ser implementado pelos adaptadores de saída
 */
public interface PaymentSummaryRepository {
    
    /**
     * Obtém um resumo de pagamentos por intervalo de tempo
     * @param from data de início (pode ser null)
     * @param to data de fim (pode ser null)
     * @return resumo com totais por tipo de processador
     */
    PaymentSummaryGetResponse getSummaryByTimeRange(Instant from, Instant to);
    
    /**
     * Obtém um resumo completo de todos os pagamentos
     * @return resumo com todos os totais
     */
    PaymentSummaryGetResponse getTotalSummary();
} 