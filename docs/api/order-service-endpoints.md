# Order Service API

Orquestrador do fluxo de negócio de pedidos.

---

## 1. Criar Pedido

POST /orders

Cria um pedido, consultando preço e reservando estoque.

### Request Body

```json
{
  "items": [
    {
      "productId": "uuid",
      "quantity": 2
    }
  ]
}
```

---

### Fluxo interno

1. Para cada item:
   - consulta preço no price-service
   - reserva estoque no inventory-service
2. calcula subtotal e total
3. salva pedido
4. retorna pedido com status CONFIRMED

---

### Responses

#### 201 Created

```json
{
  "id": "uuid",
  "items": [
    {
      "id": "uuid",
      "productId": "uuid",
      "quantity": 2,
      "unitPrice": 100.0,
      "subtotal": 200.0
    }
  ],
  "totalAmount": 200.0,
  "status": "CONFIRMED",
  "createdAt": "2026-03-27T16:49:12",
  "updatedAt": "2026-03-27T16:49:12"
}
```

---

#### 400 Bad Request

- payload inválido
- quantidade inválida
- erro vindo de integrações (ex: estoque insuficiente)

Exemplo:

```json
{
  "status": 400,
  "error": "Invalid Order",
  "message": "Inventory-service returned error...",
  "timestamp": "2026-03-27T16:48:52"
}
```

---

## 2. Buscar Pedido por ID

GET /orders/{id}

---

### Response

#### 200 OK

```json
{
  "id": "uuid",
  "items": [...],
  "totalAmount": 200.0,
  "status": "CONFIRMED",
  "createdAt": "...",
  "updatedAt": "..."
}
```

---

#### 404 Not Found

Pedido não encontrado.

---

## 3. Listar Pedidos

GET /orders

---

### Response

#### 200 OK

```json
[
  {
    "id": "uuid",
    "totalAmount": 200.0,
    "status": "CONFIRMED"
  }
]
```

---

## Integrações

### Price Service

GET /prices/active/{productId}

- retorna preço ativo

---

### Inventory Service

PATCH /inventories/product/{productId}/reserve-stock

Body:

```json
{
  "quantity": 2
}
```

---

## Observações

- Order Service não recebe customerId (não implementado ainda)
- fluxo é síncrono
- erros de integração são propagados como erro de negócio
- status atual do pedido é sempre CONFIRMED após sucesso
