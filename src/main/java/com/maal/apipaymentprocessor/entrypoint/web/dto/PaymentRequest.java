package com.maal.apipaymentprocessor.entrypoint.web.dto;

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
    public UUID correlationId;
    // amount como BigDecimal conforme especificação  
    public BigDecimal amount;
}