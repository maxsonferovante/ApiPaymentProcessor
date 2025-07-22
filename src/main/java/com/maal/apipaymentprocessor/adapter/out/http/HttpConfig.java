package com.maal.apipaymentprocessor.adapter.out.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do cliente HTTP para chamadas aos Payment Processors
 */
@Configuration
public class HttpConfig {

    /**
     * Configura RestTemplate otimizado para chamadas rápidas
     * @return RestTemplate configurado
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Timeouts otimizados para baixa latência
        factory.setConnectTimeout(2000); // 2 segundos para conectar
        factory.setReadTimeout(5000);    // 5 segundos para ler resposta
        
        return new RestTemplate(factory);
    }
} 