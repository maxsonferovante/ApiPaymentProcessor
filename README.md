# API Payment Processor

Este projeto é o serviço de API principal para a [Rinha de Backend 2025](https://github.com/zanfranceschi/rinha-de-backend-2025).

## Funcionalidades

* Recebe requisições de pagamento (`POST /payments`) e as envia para uma fila Redis.
* Fornece um resumo dos pagamentos processados (`GET /payments-summary`), consultando dados locais do Redis (salvos pelo async-worker).

## Tecnologias Principais

* Java 24 com GraalVM Native Image
* Spring Boot 3.5 com Virtual Threads
* Redis (para filas de mensagens e cache de metadados)
* Clean Architecture (Ports & Adapters)
* Nginx (como Load Balancer - configuração na raiz do projeto)

## Como Rodar

Para executar este serviço e seus componentes (Redis, Nginx, etc.), utilize o Docker Compose:

```bash
docker-compose up --build
