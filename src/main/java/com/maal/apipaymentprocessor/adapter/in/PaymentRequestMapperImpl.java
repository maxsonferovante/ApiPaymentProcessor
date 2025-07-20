package com.maal.apipaymentprocessor.adapter.in;

import com.maal.apipaymentprocessor.domain.model.Payment;
import com.maal.apipaymentprocessor.domain.port.in.PaymentRequestMapper;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PaymentRequestMapperImpl implements PaymentRequestMapper {
    
    @Override
    public Payment toDomain(PaymentRequest request) {
        if (request == null) {
            return null;
        }
        
        Instant timestamp = Instant.now();
        
        return new Payment(
                request.getCorrelationId(),
                request.getAmount(),
                timestamp
        );
    }
}
