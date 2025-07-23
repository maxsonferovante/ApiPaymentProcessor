package com.maal.apipaymentprocessor.application.service;

import com.maal.apipaymentprocessor.domain.model.Payment;
import com.maal.apipaymentprocessor.domain.port.in.ProcessPaymentUseCase;
import com.maal.apipaymentprocessor.domain.port.out.PaymentQueuePublisher;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Serviço de processamento de pagamentos
 * Removida a dependência do banco PostgreSQL - agora apenas publica na fila Redis
 */
@Service
public class PaymentService implements ProcessPaymentUseCase {
    
    private final PaymentQueuePublisher paymentQueuePublisher;

    public PaymentService(PaymentQueuePublisher paymentQueuePublisher) {
        this.paymentQueuePublisher = paymentQueuePublisher;
    }

    @Override
    public void receivePayment(PaymentRequest request) {
        // Validação do valor do pagamento
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        Instant timestamp = Instant.now();
        
        // CORREÇÃO: Usar construtor de 3 parâmetros para evitar problemas de serialização
        // O PaymentProcessorType será determinado pelo async-worker após processamento
        Payment payment = new Payment(
                request.getCorrelationId(),
                request.getAmount(),
                timestamp
        );
        
        // Apenas publica na fila Redis - não salva mais no banco
        paymentQueuePublisher.publish(payment);
    }  
}


