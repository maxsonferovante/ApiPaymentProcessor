package com.maal.apipaymentprocessor.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.Objects;

@Setter
@Getter
@Builder
public class Payment {
    private final UUID correlationId;
    private final BigDecimal amount;
    private final Instant requestedAt;
    private PaymentProcessorType paymentProcessorType; // Pode ser null no início, setado após processamento
    private PaymentStatus status; // Status do processamento (e.g., PENDING, SUCCESS, FAILED, RETRY)

    public Payment(UUID correlationId, BigDecimal amount, Instant requestedAt) {
        this.correlationId = Objects.requireNonNull(correlationId, "correlationId cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt cannot be null");
        this.status = PaymentStatus.PENDING; // Estado inicial
    }

    // Construtor para reconstruir do banco de dados, incluindo o tipo de processador e status
    public Payment(UUID correlationId, BigDecimal amount, Instant requestedAt,
                   PaymentProcessorType paymentProcessorType, PaymentStatus status) {
        this.correlationId = Objects.requireNonNull(correlationId, "correlationId cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt cannot be null");
        this.paymentProcessorType = paymentProcessorType;
        this.status = Objects.requireNonNull(status, "status cannot be null");
    }
}