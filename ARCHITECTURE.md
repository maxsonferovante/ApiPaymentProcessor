# Arquitetura Limpa - API Payment Processor

Este projeto implementa os princípios da **Arquitetura Limpa (Clean Architecture)** utilizando interfaces de contrato (ports) para desacoplar as camadas.

## 📁 Estrutura de Diretórios

```
src/main/java/com/maal/apipaymentprocessor/
├── domain/                     # Camada de Domínio
│   ├── model/                  # Entidades de domínio
│   ├── exception/              # Exceções de domínio
│   └── port/                   # Interfaces de contrato (Ports)
│       ├── in/                 # Ports de entrada (Use Cases)
│       └── out/                # Ports de saída (Gateways)
├── application/                # Camada de Aplicação
│   └── service/                # Implementação dos Use Cases
├── adapter/                    # Camada de Adaptadores
│   ├── in/                     # Adaptadores de entrada
│   └── out/                    # Adaptadores de saída
│       ├── database/           # Persistência
│       └── redis/              # Mensageria
└── entrypoint/                 # Camada de Interface
    └── web/                    # Controllers REST
```

## 🔌 Interfaces de Contrato (Ports)

### 📥 Ports de Entrada (Use Cases)

#### `ProcessPaymentUseCase`
Interface que define os casos de uso para processamento de pagamentos.
```java
public interface ProcessPaymentUseCase {
    void receivePayment(PaymentRequest request);
    void purgeAllPayments();
}
```
**Implementado por:** `PaymentService`

#### `PaymentSummaryUseCase`
Interface que define os casos de uso para consulta de resumos.
```java
public interface PaymentSummaryUseCase {
    PaymentSummaryGetResponse getPaymentSummary(String from, String to);
}
```
**Implementado por:** `PaymentSummaryService`

#### `PaymentRequestMapper`
Interface para mapeamento de DTOs para entidades de domínio.
```java
public interface PaymentRequestMapper {
    Payment toDomain(PaymentRequest request);
}
```
**Implementado por:** `PaymentRequestMapperImpl`

### 📤 Ports de Saída (Gateways)

#### `PaymentRepository`
Interface para persistência de pagamentos.
```java
public interface PaymentRepository {
    void save(Payment payment);
    void deleteAll();
}
```
**Implementado por:** `PaymentRepositoryJdbc`

#### `PaymentSummaryRepository`
Interface para consultas de resumo de pagamentos.
```java
public interface PaymentSummaryRepository {
    PaymentSummaryGetResponse getSummaryByTimeRange(Instant from, Instant to);
    PaymentSummaryGetResponse getTotalSummary();
}
```
**Implementado por:** `PaymentSummaryRepositoryJdbc`

#### `PaymentQueuePublisher`
Interface para publicação em filas de mensageria.
```java
public interface PaymentQueuePublisher {
    void publish(Payment payment);
}
```
**Implementado por:** `RedisPaymentQueuePublisher`

## 🏗️ Princípios da Arquitetura Limpa Aplicados

### 1. **Inversão de Dependência**
- As camadas internas (domínio/aplicação) não dependem das externas
- Dependências apontam "para dentro" através de interfaces

### 2. **Separation of Concerns**
- Cada camada tem uma responsabilidade específica
- Domain: regras de negócio
- Application: casos de uso
- Adapters: conversões e integrações
- Entrypoints: interface com o mundo externo

### 3. **Testabilidade**
- Interfaces permitem mock fácil para testes unitários
- Camadas podem ser testadas independentemente

### 4. **Flexibilidade**
- Adaptadores podem ser substituídos sem impactar o core
- Ex: trocar PostgreSQL por MongoDB, Redis por RabbitMQ

## 🔄 Fluxo de Execução

```
[HTTP Request] 
    ↓
[PaymentController] 
    ↓
[ProcessPaymentUseCase] (interface)
    ↓
[PaymentService] (implementação)
    ↓
[PaymentRepository] (interface) → [PaymentRepositoryJdbc] (implementação)
    ↓
[PaymentQueuePublisher] (interface) → [RedisPaymentQueuePublisher] (implementação)
```

## 🧪 Vantagens da Implementação

1. **Desacoplamento**: Camadas comunicam-se apenas através de interfaces
2. **Testabilidade**: Fácil criação de mocks e testes unitários
3. **Manutenibilidade**: Mudanças em implementações não afetam outras camadas
4. **Extensibilidade**: Novos adaptadores podem ser adicionados facilmente
5. **Conformidade**: Segue padrões da Clean Architecture

## 📋 Exemplos de Uso

### Substituir Redis por RabbitMQ
1. Criar `RabbitMQPaymentQueuePublisher implements PaymentQueuePublisher`
2. Configurar no Spring para injetar a nova implementação
3. Nenhuma mudança necessária nas outras camadas

### Adicionar Cache
1. Criar `CachedPaymentSummaryRepository implements PaymentSummaryRepository`
2. Usar padrão Decorator para envolver o repositório existente
3. Configurar injeção de dependência

## 🎯 Compliance com Clean Architecture
- ✅ Entities (domain/model)
- ✅ Use Cases (domain/port/in + application/service)
- ✅ Interface Adapters (adapter/*)
- ✅ Frameworks & Drivers (entrypoint/*, infrastructure/*)
- ✅ Dependency Inversion (todas as dependências apontam para dentro) 