package com.maal.apipaymentprocessor.domain.port.out;

import com.maal.apipaymentprocessor.domain.model.Payment;

/**
 * Port (interface) para persistência de pagamentos
 * Define o contrato que deve ser implementado pelos adaptadores de saída
 */
public interface PaymentRepository {
    
    /**
     * Salva um pagamento no repositório
     * @param payment o pagamento a ser salvo
     */
    void save(Payment payment);
    
    /**
     * Remove todos os pagamentos do repositório
     */
    void deleteAll();
} 