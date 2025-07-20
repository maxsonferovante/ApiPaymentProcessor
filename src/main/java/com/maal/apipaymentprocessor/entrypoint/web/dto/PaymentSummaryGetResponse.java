package com.maal.apipaymentprocessor.entrypoint.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PaymentSummaryGetResponse {
    // Mapeamento correto para o JSON conforme especificação: campo "default"
    @JsonProperty("default")
    private SummaryDetails defaultApi;

    // Mapeamento correto para o JSON conforme especificação: campo "fallback"
    @JsonProperty("fallback") 
    private SummaryDetails fallbackApi;

    public PaymentSummaryGetResponse() {
    }

    public PaymentSummaryGetResponse(SummaryDetails defaultApi, SummaryDetails fallbackApi) {
        this.defaultApi = defaultApi;
        this.fallbackApi = fallbackApi;
    }

    @Override
    public String toString() {
        return "PaymentSummaryGetResponse{" +
                "defaultApi=" + defaultApi +
                ", fallbackApi=" + fallbackApi +
                '}';
    }
}
