package com.maal.apipaymentprocessor.domain.port.in;

import com.maal.apipaymentprocessor.domain.model.Payment;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentRequest;

/**
 * Port (interface) para mapeamento de requests de pagamento
 * Define o contrato que deve ser implementado pelos adaptadores de entrada
 */
public interface PaymentRequestMapper {
    
    /**
     * Converte um PaymentRequest em um objeto Payment do domínio
     * @param request o request de pagamento
     * @return o objeto Payment do domínio
     */
    Payment toDomain(PaymentRequest request);
} 