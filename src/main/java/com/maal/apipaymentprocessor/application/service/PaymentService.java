package com.maal.apipaymentprocessor.application.service;

import com.maal.apipaymentprocessor.domain.model.Payment;
import com.maal.apipaymentprocessor.domain.model.PaymentProcessorType;
import com.maal.apipaymentprocessor.domain.model.PaymentStatus;
import com.maal.apipaymentprocessor.domain.port.in.ProcessPaymentUseCase;
import com.maal.apipaymentprocessor.domain.port.out.PaymentQueuePublisher;
import com.maal.apipaymentprocessor.domain.port.out.PaymentRepository;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PaymentService implements ProcessPaymentUseCase {
    
    private final PaymentQueuePublisher paymentQueuePublisher;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            PaymentQueuePublisher paymentQueuePublisher,
            PaymentRepository paymentRepository
    ) {
        this.paymentQueuePublisher = paymentQueuePublisher;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void receivePayment(PaymentRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        Instant timestamp = Instant.now();
        Payment payment = new Payment(
                request.getCorrelationId(),
                request.getAmount(),
                timestamp,
                PaymentProcessorType.DEFAULT,
                PaymentStatus.PENDING
        );
        
        paymentRepository.save(payment);
        paymentQueuePublisher.publish(payment);
    }

    @Override
    public void purgeAllPayments() {
        paymentRepository.deleteAll();
    }
}


