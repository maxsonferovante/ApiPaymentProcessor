package com.maal.apipaymentprocessor.adapter.out.http;

import com.maal.apipaymentprocessor.entrypoint.web.dto.PaymentSummaryGetResponse;
import com.maal.apipaymentprocessor.entrypoint.web.dto.SummaryDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Cliente HTTP para consultar os Payment Processors administrativos
 * Implementa consultas de resumo diretamente nos processadores
 */
@Component
public class PaymentProcessorClient {

    private final RestTemplate restTemplate;
    
    @Value("${payment-processor.default.url}")
    private String defaultProcessorUrl;
    
    @Value("${payment-processor.fallback.url}")
    private String fallbackProcessorUrl;
    
    @Value("${payment-processor.admin.token}")
    private String adminToken;

    public PaymentProcessorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtém resumo de pagamentos de ambos os processadores
     * @param from data de início (pode ser null)
     * @param to data de fim (pode ser null)
     * @return resumo consolidado dos dois processadores
     */
    public PaymentSummaryGetResponse getPaymentSummary(Instant from, Instant to) {
        try {
            // Buscar dados do processador default
            SummaryDetails defaultSummary = getProcessorSummary(defaultProcessorUrl, from, to);
            
            // Buscar dados do processador fallback
            SummaryDetails fallbackSummary = getProcessorSummary(fallbackProcessorUrl, from, to);
            
            return new PaymentSummaryGetResponse(defaultSummary, fallbackSummary);
            
        } catch (Exception e) {
            // Em caso de erro, retornar dados zerados para manter a aplicação funcionando
            System.err.println("Erro ao consultar Payment Processors: " + e.getMessage());
            return new PaymentSummaryGetResponse(
                new SummaryDetails(0, BigDecimal.ZERO),
                new SummaryDetails(0, BigDecimal.ZERO)
            );
        }
    }

    /**
     * Consulta um processador específico via endpoint administrativo
     * @param baseUrl URL base do processador
     * @param from data de início
     * @param to data de fim
     * @return resumo do processador
     */
    private SummaryDetails getProcessorSummary(String baseUrl, Instant from, Instant to) {
        try {
            // Montar URL com parâmetros de tempo se fornecidos
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + "/admin/payments-summary");
            
            if (from != null) {
                uriBuilder.queryParam("from", DateTimeFormatter.ISO_INSTANT.format(from));
            }
            
            if (to != null) {
                uriBuilder.queryParam("to", DateTimeFormatter.ISO_INSTANT.format(to));
            }
            
            String url = uriBuilder.toUriString();
            
            // Configurar headers com token de autenticação
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Rinha-Token", adminToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            // Fazer a chamada HTTP
            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // Extrair dados da resposta
                int totalRequests = getIntFromMap(responseBody, "totalRequests");
                BigDecimal totalAmount = getBigDecimalFromMap(responseBody, "totalAmount");
                
                return new SummaryDetails(totalRequests, totalAmount);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao consultar processador " + baseUrl + ": " + e.getMessage());
        }
        
        // Retornar valores zerados em caso de erro
        return new SummaryDetails(0, BigDecimal.ZERO);
    }

    /**
     * Extrai valor inteiro do Map de resposta
     */
    private int getIntFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    /**
     * Extrai valor BigDecimal do Map de resposta
     */
    private BigDecimal getBigDecimalFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return BigDecimal.ZERO;
    }
} 