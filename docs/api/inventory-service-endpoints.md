### 📄 `docs/api/inventory-service-endpoints.md`

```markdown
# Inventory Service API

Responsável pelo controle de saldo e movimentações de estoque.

## 1. Adicionar Entrada de Estoque
`POST /inventory/entries`

Adiciona saldo ao estoque de um produto.

**Request Body:**
```json
{
  "productId": "123e4567-e89b-12d3-a456-426614174001",
  "quantity": 50,
  "reason": "PURCHASE_RECEIPT"
}
Responses:

201 Created: Entrada registrada.

400 Bad Request: Quantidade inválida (ex: menor ou igual a zero).

422 Unprocessable Entity: Produto não existe no Product Service.

2. Consultar Saldo Total de um Produto
GET /inventory/products/{productId}

Retorna o saldo real e o saldo reservado.

Responses:

200 OK:

JSON
{
  "productId": "123e4567-e89b-12d3-a456-426614174001",
  "totalQuantity": 50,
  "reservedQuantity": 5,
  "availableQuantity": 45
}
404 Not Found: Sem registro de estoque para o produto.

3. Consultar Disponibilidade
GET /inventory/products/{productId}/availability?quantity=2

Validação rápida se há saldo disponível para atender uma requisição.

Responses:

200 OK:

JSON
{
  "available": true
}
4. Criar Reserva de Estoque
POST /inventory/reservations

Acionado pelo Order Service no momento da criação de um pedido.

Request Body:

JSON
{
  "orderId": "ord-789",
  "items": [
    {
      "productId": "123e4567-e89b-12d3-a456-426614174001",
      "quantity": 2
    }
  ]
}
Responses:

201 Created: Reserva efetuada com sucesso. Retorna o ID da reserva.

409 Conflict: Estoque insuficiente para um ou mais produtos (Business Error).

400 Bad Request: Estrutura do body inválida.

5. Cancelar/Desfazer Reserva
DELETE /inventory/reservations/{reservationId}

Acionado se o pagamento falhar ou o pedido for cancelado.

Responses:

204 No Content: Reserva removida, saldo liberado.

404 Not Found: Reserva não encontrada.