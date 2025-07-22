package com.maal.apipaymentprocessor.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    // Removido final para compatibilidade com Jackson/GraalVM
    @JsonProperty("correlationId")
    private UUID correlationId;
    
    @JsonProperty("amount") 
    private BigDecimal amount;
    
    @JsonProperty("requestedAt")
    private Instant requestedAt;
    
    @JsonProperty("paymentProcessorType")
    private PaymentProcessorType paymentProcessorType; // Pode ser null no início, setado após processamento
    
    @JsonProperty("status")
    private PaymentStatus status; // Status do processamento (e.g., PENDING, SUCCESS, FAILED, RETRY)

    // Construtor padrão necessário para Jackson
    public Payment() {}

    public Payment(UUID correlationId, BigDecimal amount, Instant requestedAt) {
        this.correlationId = Objects.requireNonNull(correlationId, "correlationId cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt cannot be null");
        this.status = PaymentStatus.PENDING; // Estado inicial
    }

    // Construtor para reconstruir do banco de dados, incluindo o tipo de processador e status
    @JsonCreator
    public Payment(@JsonProperty("correlationId") UUID correlationId, 
                   @JsonProperty("amount") BigDecimal amount, 
                   @JsonProperty("requestedAt") Instant requestedAt,
                   @JsonProperty("paymentProcessorType") PaymentProcessorType paymentProcessorType, 
                   @JsonProperty("status") PaymentStatus status) {
        this.correlationId = Objects.requireNonNull(correlationId, "correlationId cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt cannot be null");
        this.paymentProcessorType = paymentProcessorType;
        this.status = Objects.requireNonNull(status, "status cannot be null");
    }
}