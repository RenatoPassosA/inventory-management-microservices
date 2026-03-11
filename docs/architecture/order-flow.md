# Order Creation Flow

Este documento descreve o fluxo principal de negócio do sistema.

O fluxo representa a **criação de um pedido**.

---

# Step 1 — Request

O cliente envia uma requisição para criar um pedido:

POST /orders

Contendo:

- produtos
- quantidades

Exemplo:

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

---

# Step 2 — Gateway

A requisição chega ao **API Gateway**.

O gateway:

- valida autenticação
- encaminha para o Order Service

---

# Step 3 — Product Validation

Order Service valida se os produtos existem.

Chamada:

GET /products/{id}

Se algum produto não existir:

Pedido falha.

---

# Step 4 — Price Retrieval

Order Service consulta o preço atual do produto.

Chamada:

GET /prices/products/{productId}/current

Se algum preço não estiver disponível:

- pedido falha
- status final: FAILED

---

# Step 5 — Stock Reservation

Order Service solicita reserva de estoque.

Chamada:

POST /inventory/reservations

Exemplo:
```
{
  "orderId": "generated-order-id",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

Se não houver estoque suficiente:

- pedido falha
- status final: FAILED

---

# Step 6 — Order Persistence

Se todas as validações forem bem sucedidas:
- o pedido é salvo no banco de dados do Order Service
- o status inicial relevante será RESERVED

O pedido armazenará:

- itens
- quantidades
- preços unitários
- total
- status

---

# Step 7 — Response

O Order Service retorna a resposta ao cliente via Gateway.

Contendo:

- id do pedido
- status
- itens
- total

exemplo:
```
{
  "orderId": "ORD-12345",
  "status": "RESERVED",
  "totalAmount": 299.90,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 99.95,
      "totalPrice": 199.90
    },
    {
      "productId": 2,
      "quantity": 1,
      "unitPrice": 100.00,
      "totalPrice": 100.00
    }
  ]
}
```

# Failure Scenarios

Principais cenários de falha:

- produto inexistente
- produto inativo
- preço não encontrado
- estoque insuficiente
- falha de comunicação entre serviços

Nestes casos, o Order Service deve retornar erro consistente e registrar o pedido como FAILED quando aplicável.