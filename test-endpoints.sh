#!/bin/bash

# Script para testar todos os endpoints da API Payment Processor
# Certifique-se de que a aplicação está rodando em http://localhost:8089

BASE_URL="http://localhost:8089"

echo "🚀 Testando API Payment Processor..."
echo "====================================="

# Função para exibir resposta
show_response() {
    echo "Status: $1"
    echo "Response: $2"
    echo "-------------------------------------"
}

# 1. Testar criação de pagamentos
echo "📝 1. Testando criação de pagamentos..."

echo "→ Criando pagamento 1..."
response1=$(curl -s -w "HTTPSTATUS:%{http_code}" -X POST $BASE_URL/payments \
  -H "Content-Type: application/json" \
  -d '{
    "correlationId": "550e8400-e29b-41d4-a716-446655440001",
    "amount": 100.50
  }')
http_code1=$(echo $response1 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body1=$(echo $response1 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code1 "$body1"

echo "→ Criando pagamento 2..."
response2=$(curl -s -w "HTTPSTATUS:%{http_code}" -X POST $BASE_URL/payments \
  -H "Content-Type: application/json" \
  -d '{
    "correlationId": "550e8400-e29b-41d4-a716-446655440002",
    "amount": 250.75
  }')
http_code2=$(echo $response2 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body2=$(echo $response2 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code2 "$body2"

echo "→ Criando pagamento 3..."
response3=$(curl -s -w "HTTPSTATUS:%{http_code}" -X POST $BASE_URL/payments \
  -H "Content-Type: application/json" \
  -d '{
    "correlationId": "550e8400-e29b-41d4-a716-446655440003",
    "amount": 15.99
  }')
http_code3=$(echo $response3 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body3=$(echo $response3 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code3 "$body3"

# 2. Testar obtenção de resumo
echo "📊 2. Testando obtenção de resumo..."

echo "→ Obtendo resumo geral..."
response4=$(curl -s -w "HTTPSTATUS:%{http_code}" -X GET $BASE_URL/payments-summary \
  -H "Accept: application/json")
http_code4=$(echo $response4 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body4=$(echo $response4 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code4 "$body4"

# 3. Testar com dados inválidos
echo "❌ 3. Testando com dados inválidos..."

echo "→ Testando com correlationId inválido..."
response5=$(curl -s -w "HTTPSTATUS:%{http_code}" -X POST $BASE_URL/payments \
  -H "Content-Type: application/json" \
  -d '{
    "correlationId": "invalid-uuid",
    "amount": 100.50
  }')
http_code5=$(echo $response5 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body5=$(echo $response5 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code5 "$body5"

echo "→ Testando com amount negativo..."
response6=$(curl -s -w "HTTPSTATUS:%{http_code}" -X POST $BASE_URL/payments \
  -H "Content-Type: application/json" \
  -d '{
    "correlationId": "550e8400-e29b-41d4-a716-446655440004",
    "amount": -100.50
  }')
http_code6=$(echo $response6 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body6=$(echo $response6 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code6 "$body6"

# 4. Testar limpeza de pagamentos
echo "🧹 4. Testando limpeza de pagamentos..."
echo "→ Limpando todos os pagamentos..."
response7=$(curl -s -w "HTTPSTATUS:%{http_code}" -X POST $BASE_URL/purge-payments \
  -H "Content-Type: application/json")
http_code7=$(echo $response7 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body7=$(echo $response7 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code7 "$body7"

echo "→ Verificando se a limpeza funcionou (resumo deve estar zerado)..."
response8=$(curl -s -w "HTTPSTATUS:%{http_code}" -X GET $BASE_URL/payments-summary \
  -H "Accept: application/json")
http_code8=$(echo $response8 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body8=$(echo $response8 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code8 "$body8"

echo "✅ Testes concluídos!"
echo "====================="

# 5. Testes específicos do endpoint payments-summary
echo "📊 5. Testando endpoint payments-summary com parâmetros..."

echo "→ Testando summary com parâmetros from e to válidos..."
response9=$(curl -s -w "HTTPSTATUS:%{http_code}" -X GET "$BASE_URL/payments-summary?from=2020-07-10T12:34:56.000Z&to=2025-07-10T12:35:56.000Z" \
  -H "Accept: application/json")
http_code9=$(echo $response9 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body9=$(echo $response9 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code9 "$body9"

echo "→ Testando summary com timestamp inválido (deve retornar 400)..."
response10=$(curl -s -w "HTTPSTATUS:%{http_code}" -X GET "$BASE_URL/payments-summary?from=invalid-timestamp" \
  -H "Accept: application/json")
http_code10=$(echo $response10 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body10=$(echo $response10 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code10 "$body10"

echo "→ Testando summary com apenas parâmetro 'to'..."
response11=$(curl -s -w "HTTPSTATUS:%{http_code}" -X GET "$BASE_URL/payments-summary?to=2025-07-10T12:35:56.000Z" \
  -H "Accept: application/json")
http_code11=$(echo $response11 | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body11=$(echo $response11 | sed -e 's/HTTPSTATUS\:.*//g')
show_response $http_code11 "$body11"

echo "✅ Testes do endpoint payments-summary concluídos!"
echo "====================="
echo "💡 Respostas esperadas:"
echo "  - Pagamentos válidos: HTTP 200"
echo "  - Dados inválidos: HTTP 400"
echo "  - Resumo: HTTP 200 + JSON com campos 'default' e 'fallback'"
echo "  - Timestamp inválido: HTTP 400 + mensagem de erro"
echo "  - Limpeza: HTTP 200"
echo "  - Resumo após limpeza: totais zerados" 