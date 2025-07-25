package com.maal.apipaymentprocessor.entrypoint.web;

import com.maal.apipaymentprocessor.domain.exception.PaymentProcessingException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manipulador global de exceções para tratar erros de deserialização JSON
 * e erros de banco de dados sem gerar logs de erro desnecessários.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura erros de deserialização JSON (incluindo UUIDs inválidos)
     * e retorna resposta estruturada SEM logar como erro.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        
        // Detecta erro específico de UUID
        if (message != null && message.contains("UUID")) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "erro", "UUID inválido",
                    "descricao", "correlationId deve ser um UUID válido no formato: 550e8400-e29b-41d4-a716-446655440001",
                    "status", 400
                ));
        }
        
        // Para outros erros de JSON (como BigDecimal inválido)
        if (message != null && message.contains("BigDecimal")) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "erro", "Valor inválido",
                    "descricao", "amount deve ser um número decimal válido",
                    "status", 400
                ));
        }
        
        // Para outros erros gerais de JSON
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "erro", "Formato JSON inválido",
                "descricao", "Verifique a estrutura dos dados enviados",
                "status", 400
            ));
    }
    
    /**
     * Captura erros de chave duplicada no banco de dados (correlationId já existe)
     * e retorna HTTP 409 Conflict com mensagem clara.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKey(DuplicateKeyException ex) {
        String message = ex.getMessage();
        
        // Extrai o correlationId da mensagem de erro se possível
        String correlationId = "desconhecido";
        if (message != null && message.contains("correlation_id")) {
            // Tenta extrair o UUID da mensagem de erro
            int start = message.indexOf("(correlation_id)=(") + 18;
            int end = message.indexOf(")", start);
            if (start > 17 && end > start) {
                correlationId = message.substring(start, end);
            }
        }
        
        return ResponseEntity
            .status(HttpStatus.CONFLICT) // 409 Conflict
            .body(Map.of(
                "erro", "Pagamento já processado",
                "descricao", String.format("O correlationId '%s' já foi utilizado em outro pagamento", correlationId),
                "status", 409,
                "correlationId", correlationId
            ));
    }
    
    /**
     * Captura erros de argumentos inválidos (incluindo timestamps inválidos)
     * e retorna HTTP 400 Bad Request com mensagem descritiva.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "erro", "Parâmetro inválido",
                "descricao", ex.getMessage(),
                "status", 400
            ));
    }
    /**
     * Captura falha no enfilamento de pagamento, lançando uma exceção com HTTP 503
     */
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentProcessingException(PaymentProcessingException ex) {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE) // 503 Service Unavailable
            .body(Map.of(
                "erro", "Falha no processamento de pagamento. Tente novamente mais tarde.",
                "descricao", ex.getMessage(),
                "status", 503
            ));
    }

} 