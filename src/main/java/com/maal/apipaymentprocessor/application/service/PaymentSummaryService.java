package com.maal.apipaymentprocessor.application.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.maal.apipaymentprocessor.domain.model.Payment;
import com.maal.apipaymentprocessor.domain.port.in.PaymentSummaryUseCase;
import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;
import com.maal.apipaymentprocessor.entrypoint.web.dto.SummaryDetails;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import java.util.Objects;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serviço de consulta de resumos de pagamentos
 * Consulta dados locais do Redis (salvos pelo async-worker)
 * Muito mais rápido que chamadas HTTP aos Payment Processors
 */
@Service
public class PaymentSummaryService implements PaymentSummaryUseCase {
    private static final Logger logger = LoggerFactory.getLogger(PaymentSummaryService.class);

    // Chaves para as listas de pagamentos no Redis
    private static final String DEFAULT_PAYMENTS_LIST_KEY = "payments:history:default";
    private static final String FALLBACK_PAYMENTS_LIST_KEY = "payments:history:fallback";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public PaymentSummaryService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

      /**
     * Remove todos os pagamentos do sistema
     */
    @Override
     public void purgeAllPayments() {
        try {
            redisTemplate.delete(List.of(DEFAULT_PAYMENTS_LIST_KEY, FALLBACK_PAYMENTS_LIST_KEY));
            logger.info("Listas de histórico de pagamento foram limpas do Redis.");
        } catch (Exception e) {
            logger.error("Erro ao limpar histórico de pagamentos: {}", e.getMessage(), e);
        }
     }


    @Override
    public PaymentSummaryGetResponse getPaymentSummary(String from, String to) {
        // Parsing dos timestamps de entrada
        Instant fromDate = (from == null) ? Instant.MIN : Instant.parse(from);
        Instant toDate = (to == null) ? Instant.now() : Instant.parse(to);

        SummaryDetails defaultSummary = calculateSummaryForProcessor(DEFAULT_PAYMENTS_LIST_KEY, fromDate, toDate);
        SummaryDetails fallbackSummary = calculateSummaryForProcessor(FALLBACK_PAYMENTS_LIST_KEY, fromDate, toDate);
        return new PaymentSummaryGetResponse(defaultSummary, fallbackSummary);
    }

    private SummaryDetails calculateSummaryForProcessor(String key, Instant from, Instant to) {
        try {
            // O(N) onde N é o número de pagamentos, pode ser lento.
            // Para a Rinha, com volume de dados limitado, é aceitável.
            List<String> paymentJsonList = redisTemplate.opsForList().range(key, 0, -1);
            if (paymentJsonList == null || paymentJsonList.isEmpty()) {
                return new SummaryDetails();
            }

            List<Payment> payments = paymentJsonList.stream()
                    .map(this::deserializePayment)
                    .filter(Objects::nonNull)
                    .filter(p -> !p.getRequestedAt().isBefore(from) && p.getRequestedAt().isBefore(to))
                    .toList();

            long totalRequests = payments.size();
            BigDecimal totalAmount = payments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new SummaryDetails(Math.toIntExact(totalRequests), totalAmount);

        } catch (Exception e) {
            logger.error("Erro ao calcular resumo para a chave {}: {}", key, e.getMessage(), e);
            return new SummaryDetails(); // Retorna resumo vazio em caso de erro
        }
    }

    private Payment deserializePayment(String json) {
        try {
            return objectMapper.readValue(json, Payment.class);
        } catch (IOException e) {
            logger.error("Falha ao deserializar pagamento do JSON: {}", json, e);
            return null;
        }
    }

}
