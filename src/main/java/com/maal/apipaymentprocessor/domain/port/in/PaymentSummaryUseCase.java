package com.maal.apipaymentprocessor.domain.port.in;

import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;

/**
 * Port (interface) para caso de uso de resumo de pagamentos
 * Define o contrato que deve ser implementado pela camada de aplicação
 */
public interface PaymentSummaryUseCase {
    
    /**
     * Obtém um resumo de pagamentos por intervalo de tempo
     * @param from data de início (pode ser null)
     * @param to data de fim (pode ser null)
     * @return resumo com totais por tipo de processador
     */
    PaymentSummaryGetResponse getPaymentSummary(String from, String to);
} 