package com.maal.apipaymentprocessor.application.service;

import com.maal.apipaymentprocessor.adapter.out.redis.RedisPaymentSummaryRepository;
import com.maal.apipaymentprocessor.domain.port.in.PaymentSummaryUseCase;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;
import com.maal.apipaymentprocessor.entrypoint.web.dto.SummaryDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * Serviço de consulta de resumos de pagamentos
 * Consulta dados locais do Redis (salvos pelo async-worker)
 * Muito mais rápido que chamadas HTTP aos Payment Processors
 */
@Service
public class PaymentSummaryService implements PaymentSummaryUseCase {
    
    private final RedisPaymentSummaryRepository redisPaymentSummaryRepository;

    public PaymentSummaryService(RedisPaymentSummaryRepository redisPaymentSummaryRepository) {
        this.redisPaymentSummaryRepository = redisPaymentSummaryRepository;
    }

  /**
     * Remove todos os pagamentos do sistema
     */
    @Override
     public void purgeAllPayments() {
        redisPaymentSummaryRepository.deleteAll();
     }


    @Override
    public PaymentSummaryGetResponse getPaymentSummary(String from, String to) {
        // Parsing dos timestamps de entrada
        Instant fromInstant = parseTimestamp(from, "from");
        Instant toInstant = parseTimestamp(to, "to");
        
        // Consultar Redis (dados salvos pelo async-worker)
        SummaryDetails defaultSummary = redisPaymentSummaryRepository
            .getProcessorSummary("default", fromInstant, toInstant);
            
        SummaryDetails fallbackSummary = redisPaymentSummaryRepository
            .getProcessorSummary("fallback", fromInstant, toInstant);
        
        return new PaymentSummaryGetResponse(defaultSummary, fallbackSummary);
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
