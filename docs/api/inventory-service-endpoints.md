# Inventory Service API

Responsável pelo controle de estoque de produtos.

Base URL: /inventories

---

## 1. Criar Inventory

POST /inventories

Request:
{
  "productId": "uuid"
}

Responses:
- 201 Created
- 409 Conflict
- 404 Not Found

---

## 2. Buscar Inventory por Produto

GET /inventories/product/{productId}

Response:
{
  "productId": "uuid",
  "availableQuantity": 50,
  "reservedQuantity": 10,
  "totalQuantity": 60
}

---

## 3. Adicionar Estoque

PATCH /inventories/product/{productId}/add-stock

Request:
{
  "quantity": 50
}

---

## 4. Remover Estoque

PATCH /inventories/product/{productId}/remove-stock

Request:
{
  "quantity": 10
}

---

## 5. Reservar Estoque

PATCH /inventories/product/{productId}/reserve-stock

Request:
{
  "quantity": 5
}

---

## 6. Liberar Estoque Reservado

PATCH /inventories/product/{productId}/release-reserved-stock

Request:
{
  "quantity": 3
}

---

## Error Response

{
  "status": 400,
  "error": "Bad Request",
  "message": "Descrição do erro",
  "details": ["campo: erro"],
  "timestamp": "2026-03-30T18:00:00Z"
}