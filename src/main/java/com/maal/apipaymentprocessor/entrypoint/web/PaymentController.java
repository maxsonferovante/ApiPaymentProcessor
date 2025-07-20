package com.maal.apipaymentprocessor.entrypoint.web;

import com.maal.apipaymentprocessor.domain.exception.PaymentProcessingException;
import com.maal.apipaymentprocessor.domain.port.in.PaymentSummaryUseCase;
import com.maal.apipaymentprocessor.domain.port.in.ProcessPaymentUseCase;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentRequest;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentSummaryUseCase paymentSummaryUseCase;

    public PaymentController(ProcessPaymentUseCase processPaymentUseCase, PaymentSummaryUseCase paymentSummaryUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.paymentSummaryUseCase = paymentSummaryUseCase;
    }

    @PostMapping(value = "/payments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> receivePayment(@RequestBody PaymentRequest request) {
        try {
            processPaymentUseCase.receivePayment(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (PaymentProcessingException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping(value = "/payments-summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentSummaryGetResponse> getPaymentSummary(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        PaymentSummaryGetResponse summary = paymentSummaryUseCase.getPaymentSummary(from, to);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/purge-payments")
    public ResponseEntity<Void> purgePayments() {
        processPaymentUseCase.purgeAllPayments();
        return ResponseEntity.ok().build();
    }
}
