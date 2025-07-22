package com.maal.apipaymentprocessor.application.service;

import com.maal.apipaymentprocessor.adapter.out.http.PaymentProcessorClient;
import com.maal.apipaymentprocessor.domain.port.in.PaymentSummaryUseCase;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * Serviço que consulta resumos de pagamentos diretamente nos Payment Processors
 * Substituiu a consulta ao banco PostgreSQL por chamadas HTTP aos endpoints administrativos
 */
@Service
public class PaymentSummaryService implements PaymentSummaryUseCase {
    
    private final PaymentProcessorClient paymentProcessorClient;

    public PaymentSummaryService(PaymentProcessorClient paymentProcessorClient) {
        this.paymentProcessorClient = paymentProcessorClient;
    }

    @Override
    public PaymentSummaryGetResponse getPaymentSummary(String from, String to) {
        // Parsing dos timestamps de entrada
        Instant fromInstant = parseTimestamp(from, "from");
        Instant toInstant = parseTimestamp(to, "to");
        
        // Consulta direta aos Payment Processors via HTTP
        return paymentProcessorClient.getPaymentSummary(fromInstant, toInstant);
    }

    private Instant parseTimestamp(String timestamp, String paramName) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Instant.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                String.format("Invalid timestamp format for parameter '%s': '%s'. Expected ISO format (e.g., 2020-07-10T12:34:56.000Z)", 
                paramName, timestamp), e);
        }
    }
}
