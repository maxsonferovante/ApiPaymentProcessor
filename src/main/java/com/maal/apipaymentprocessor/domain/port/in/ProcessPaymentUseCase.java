package com.maal.apipaymentprocessor.domain.port.in;

import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentRequest;

/**
 * Port (interface) para caso de uso de processamento de pagamentos
 * Define o contrato que deve ser implementado pela camada de aplicação
 */
public interface ProcessPaymentUseCase {
    
    /**
     * Processa um novo pagamento
     * @param request o request de pagamento
     */
    void receivePayment(PaymentRequest request);

} 