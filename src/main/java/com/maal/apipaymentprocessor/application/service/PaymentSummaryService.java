package com.maal.apipaymentprocessor.application.service;

import com.maal.apipaymentprocessor.domain.port.in.PaymentSummaryUseCase;
import com.maal.apipaymentprocessor.domain.port.out.PaymentSummaryRepository;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
public class PaymentSummaryService implements PaymentSummaryUseCase {
    
    private final PaymentSummaryRepository paymentSummaryRepository;

    public PaymentSummaryService(PaymentSummaryRepository paymentSummaryRepository) {
        this.paymentSummaryRepository = paymentSummaryRepository;
    }

    @Override
    public PaymentSummaryGetResponse getPaymentSummary(String from, String to) {
        Instant fromInstant = parseTimestamp(from, "from");
        Instant toInstant = parseTimestamp(to, "to");
        return paymentSummaryRepository.getSummaryByTimeRange(fromInstant, toInstant);
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
