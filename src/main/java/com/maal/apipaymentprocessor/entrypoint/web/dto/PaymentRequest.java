package com.maal.apipaymentprocessor.entrypoint.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Builder
public class PaymentRequest {
    // correlationId como UUID conforme especificação
    @JsonProperty("correlationId")
    private UUID correlationId;
    
    // amount como BigDecimal conforme especificação  
    @JsonProperty("amount")
    private BigDecimal amount;

    // Construtor padrão necessário para Jackson
    public PaymentRequest() {}

    // Construtor com parâmetros para Jackson
    @JsonCreator
    public PaymentRequest(@JsonProperty("correlationId") UUID correlationId, 
                         @JsonProperty("amount") BigDecimal amount) {
        this.correlationId = correlationId;
        this.amount = amount;
    }
}