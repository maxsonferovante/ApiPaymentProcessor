# API Payment Processor

Este projeto é o serviço de API principal para a [Rinha de Backend 2025](https://github.com/zanfranceschi/rinha-de-backend-2025).

## Funcionalidades

* Recebe requisições de pagamento (`POST /payments`) e as envia para uma fila Redis.
* Fornece um resumo dos pagamentos processados (`GET /payments-summary`), consultando os serviços externos diretamente.

## Tecnologias Principais

* Java 24
* Spring Boot
* Redis (para enfileiramento)
* RestTemplate (para chamadas HTTP externas)
* Nginx (como Load Balancer - configuração na raiz do projeto)

## Como Rodar

Para executar este serviço e seus componentes (Redis, Nginx, etc.), utilize o Docker Compose:

```bash
docker-compose up --build
