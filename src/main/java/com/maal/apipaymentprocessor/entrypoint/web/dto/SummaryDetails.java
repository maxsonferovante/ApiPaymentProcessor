
package com.maal.apipaymentprocessor.entrypoint.web.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal; // Certifique-se de importar

@Setter
@Getter
@Builder
public class SummaryDetails {
    // Getters e Setters
    private Integer totalRequests;
    private BigDecimal totalAmount;

    // Construtor padrão para serialização JSON
    public SummaryDetails() {
    }

    public SummaryDetails(Integer totalRequests, BigDecimal totalAmount) {
        this.totalRequests = totalRequests;
        this.totalAmount = totalAmount;
    }

}