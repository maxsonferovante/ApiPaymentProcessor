# 🚀 Guia de Testes de Performance - ApiPaymentProcessor

## 📊 **Validação das Otimizações Aplicadas**

### **🔧 Comandos de Teste Rápido:**

#### **1. Inicialização da Aplicação:**
```bash
# Testar startup time
time docker-compose up --build

# Expectativa: PostgreSQL + Apps inicializando em < 2 minutos
# GraalVM Native: ~80ms vs JVM tradicional ~3s
```

#### **2. Teste de Conectividade:**
```bash
# Health check das aplicações
curl http://localhost:8089/health

# Teste de load balancer
for i in {1..10}; do curl -s http://localhost:8089/health; done
```

#### **3. Teste de Performance dos Endpoints:**
```bash
# POST - Criar pagamento
curl -X POST http://localhost:8089/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "paymentProcessorType": "DEFAULT"
  }'

# GET - Resumo total
time curl http://localhost:8089/payments/summary

# GET - Resumo por período
time curl "http://localhost:8089/payments/summary?from=2024-01-01T00:00:00Z&to=2024-12-31T23:59:59Z"
```

### **📈 Benchmarks com ApacheBench:**

#### **Teste de Carga - INSERTs:**
```bash
# 1000 requests, 50 concurrent
ab -n 1000 -c 50 -p payment.json -T application/json \
   http://localhost:8089/payments

# payment.json:
echo '{"amount":100.50,"paymentProcessorType":"DEFAULT"}' > payment.json
```

#### **Teste de Carga - SELECTs:**
```bash
# 1000 requests de consulta resumo
ab -n 1000 -c 20 http://localhost:8089/payments/summary

# Expectativa com otimizações:
# - Requests per second: >2000 RPS
# - Time per request: <50ms (95th percentile)
```

### **🗄️ Validação do PostgreSQL:**

#### **Verificar Configurações Aplicadas:**
```bash
# Conectar ao PostgreSQL
docker exec -it $(docker ps | grep postgres | awk '{print $1}') psql -U user -d rinha

# Verificar configurações de performance
SHOW fsync;                    -- Deve ser 'off'
SHOW synchronous_commit;       -- Deve ser 'off' 
SHOW shared_buffers;           -- Deve ser '32MB'
SHOW work_mem;                 -- Deve ser '2MB'
```

#### **Verificar Schema Otimizado:**
```sql
-- Verificar tabela UNLOGGED
SELECT schemaname, tablename, hasindexes, hasrules, hastriggers 
FROM pg_tables WHERE tablename = 'payments';

-- Verificar índices covering
\d+ payments

-- Deve mostrar índices:
-- - payments_summary_optimized (covering)
-- - payments_time_range_covering (covering)
```

#### **Teste de Performance das Queries:**
```sql
-- Habilitar timing
\timing on

-- Teste INSERT (deve ser ultra-rápido com UNLOGGED)
INSERT INTO payments (correlation_id, amount, requested_at, payment_processor_type) 
VALUES (gen_random_uuid(), 100.50, NOW(), 'DEFAULT');

-- Teste SELECT com índice covering (deve usar covering index)
EXPLAIN (ANALYZE, BUFFERS) 
SELECT payment_processor_type, COUNT(*), SUM(amount) 
FROM payments 
GROUP BY payment_processor_type;
```

### **💾 Monitoramento de Recursos:**

#### **Uso de Memória:**
```bash
# Ver uso de memória dos containers
docker stats --no-stream

# Expectativa:
# - postgres: ~60-80MB (limit: 100MB)
# - app-native-1/2: ~40-60MB cada (limit: 80MB each)
# - redis: ~20-40MB (limit: 60MB)
# - nginx: ~5-10MB (limit: 30MB)
```

#### **Uso de CPU:**
```bash
# Monitoramento contínuo
watch "docker stats --no-stream | head -10"

# Durante carga:
# - postgres: 20-30% CPU (limit: 0.3 CPU)
# - app-native: 40-50% CPU cada (limit: 0.5 CPU each)
```

### **🧪 Testes de Stress:**

#### **Carga Pesada de INSERTs:**
```bash
# Teste extremo - 10000 requests, 100 concurrent
ab -n 10000 -c 100 -p payment.json -T application/json \
   http://localhost:8089/payments

# Expectativa com otimizações:
# - Success rate: >99%
# - Average response time: <100ms
# - No memory leaks
```

#### **Carga de SELECTs Simultâneos:**
```bash
# Múltiplas consultas simultâneas
for i in {1..10}; do
  ab -n 500 -c 25 http://localhost:8089/payments/summary &
done
wait

# Verificar se covering indexes estão sendo usados
```

### **📊 Métricas de Sucesso:**

#### **Performance Targets:**
| Métrica | **Valor Alvo** | **Como Medir** |
|---------|---------------|----------------|
| **Startup Time** | < 90 segundos | `time docker-compose up` |
| **POST /payments** | < 50ms p95 | `ab -n 1000 -c 50` |
| **GET /summary** | < 30ms p95 | `ab -n 1000 -c 20` |
| **Memory Usage** | < 350MB total | `docker stats` |
| **CPU Usage** | < 1.5 CPUs total | `docker stats` |
| **Throughput** | > 2000 RPS | ApacheBench |

#### **Indicadores de Otimização:**
```bash
# ✅ PostgreSQL otimizado
docker exec postgres-container psql -U user -d rinha -c "SHOW fsync;"

# ✅ Covering indexes sendo usados  
docker exec postgres-container psql -U user -d rinha -c "
EXPLAIN (ANALYZE, BUFFERS) 
SELECT payment_processor_type, COUNT(*), SUM(amount) 
FROM payments GROUP BY payment_processor_type;"

# ✅ Aplicação GraalVM nativa
docker exec app-container ps aux | grep java  # Não deve aparecer processo Java
```

### **🚨 Troubleshooting:**

#### **Se Performance Estiver Baixa:**
1. **Verificar configurações PostgreSQL:** `SHOW ALL;`
2. **Analisar planos de query:** `EXPLAIN (ANALYZE, BUFFERS) SELECT...`
3. **Monitorar I/O:** `docker exec postgres iostat -x 1`
4. **Verificar logs:** `docker logs postgres-container`

#### **Se Memória Estiver Alta:**
1. **Verificar vazamentos:** `docker stats --no-stream`
2. **Analisar conexões:** `docker exec postgres psql -c "SELECT count(*) FROM pg_stat_activity;"`
3. **Verificar cache Redis:** `docker exec redis redis-cli info memory`

---

## 🎯 **Resumo:**

**Com todas as otimizações aplicadas, a aplicação deve demonstrar:**
- ⚡ **Ultra-low latency:** <50ms para operações principais
- 🚀 **High throughput:** >2000 RPS em hardware modesto  
- 💾 **Low memory footprint:** <350MB total para toda a stack
- 🔄 **Fast startup:** <90s para todo o ambiente
- 📈 **Excellent scalability:** 2 instâncias com load balancing

**Use este guia para validar que todas as otimizações estão funcionando corretamente! 🚀** 