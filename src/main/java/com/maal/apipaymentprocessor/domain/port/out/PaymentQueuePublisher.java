package com.maal.apipaymentprocessor.domain.port.out;

import com.maal.apipaymentprocessor.domain.model.Payment;

/**
 * Port (interface) para publicação de pagamentos em fila
 * Define o contrato que deve ser implementado pelos adaptadores de saída
 */
public interface PaymentQueuePublisher {
    
    /**
     * Publica um pagamento na fila para processamento assíncrono
     * @param payment o pagamento a ser publicado
     */
    void publish(Payment payment);
} 