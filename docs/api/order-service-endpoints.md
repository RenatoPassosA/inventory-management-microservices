### 📄 `docs/api/order-service-endpoints.md`

```markdown
# Order Service API

Orquestrador do fluxo de negócios e gerenciador de pedidos.

## 1. Criar Pedido
`POST /orders`

Inicia o fluxo do pedido. O Order Service se comunica internamente com Product, Price e Inventory.

**Request Body:**
```json
{
  "customerId": "cust-999",
  "items": [
    {
      "productId": "123e4567-e89b-12d3-a456-426614174001",
      "quantity": 2
    }
  ]
}
Responses:

201 Created: Pedido criado (Status: RESERVED ou CREATED dependendo do fluxo).

JSON
{
  "orderId": "ord-789",
  "status": "RESERVED",
  "totalAmount": 25000.00,
  "createdAt": "2023-10-05T14:30:00Z"
}
400 Bad Request: Dados ausentes.

422 Unprocessable Entity: Produto inexistente ou inativo (retornado pelo Product Service).

409 Conflict: Estoque insuficiente (retornado pelo Inventory Service).

2. Consultar Pedido
GET /orders/{id}

Responses:

200 OK:

JSON
{
  "orderId": "ord-789",
  "customerId": "cust-999",
  "status": "CONFIRMED",
  "totalAmount": 25000.00,
  "items": [...]
}
404 Not Found: Pedido não encontrado.

3. Cancelar Pedido
POST /orders/{id}/cancel

Cancela o pedido e solicita ao Inventory Service a devolução do estoque (caso estivesse reservado).

Responses:

200 OK: Status do pedido alterado para CANCELLED.

422 Unprocessable Entity: Pedido já foi despachado e não pode ser cancelado (Regra de Negócio).