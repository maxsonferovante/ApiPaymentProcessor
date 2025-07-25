package com.maal.apipaymentprocessor.adapter.out.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maal.apipaymentprocessor.domain.exception.PaymentProcessingException;
import com.maal.apipaymentprocessor.domain.model.Payment;
import com.maal.apipaymentprocessor.domain.port.out.PaymentQueuePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RedisPaymentQueuePublisher implements PaymentQueuePublisher {
    

    private final Logger logger = LoggerFactory.getLogger(RedisPaymentQueuePublisher.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rinha.queue.payments-main}")
    private String paymentsMainQueueName;

    public RedisPaymentQueuePublisher(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Payment payment) {
        try {
            String paymentJson = objectMapper.writeValueAsString(payment);
            redisTemplate.opsForList().rightPush(paymentsMainQueueName, paymentJson);
        } catch (Exception e) {
            logger.warn("Payment publishing failed", e);
            throw new PaymentProcessingException("Failed to publish payment to Redis queue: " + e.getMessage(), e.getCause());
        }
    }
}
