# Order Domain

O Order Service é responsável pela criação e gerenciamento de pedidos.

Ele atua como **orquestrador do fluxo de negócio**, integrando com:
- price-service (para obter preço)
- inventory-service (para reservar estoque)

---

## Entities

### Order

#### Fields

- id (UUID)
- status (OrderStatus)
- totalAmount (BigDecimal)
- createdAt (OffsetDateTime)
- updatedAt (OffsetDateTime)
- items (List<OrderItem>)

---

### OrderItem

#### Fields

- id (UUID)
- productId (UUID)
- quantity (Integer)
- unitPrice (BigDecimal)
- subtotal (BigDecimal)

---

## Responsibilities

- criar pedido
- validar itens
- consultar preço do produto
- reservar estoque
- calcular total
- persistir pedido
- buscar pedido por id
- listar pedidos

---

## Order Status

Estados atualmente utilizados:

- CONFIRMED

> Observação: o fluxo atual não implementa estados intermediários (RESERVED, CREATED, etc).
O pedido já é criado como **CONFIRMED** após sucesso das integrações.

---

## Domain Rules

- um pedido deve possuir ao menos um item
- quantidade deve ser maior que zero
- productId não pode ser nulo
- preço deve existir no price-service
- estoque deve estar disponível no inventory-service
- totalAmount = soma dos subtotais dos itens
- subtotal = unitPrice * quantity

---

## Integrações

### Price Service
- busca preço ativo do produto
- erro se não existir preço

### Inventory Service
- reserva estoque
- erro se não houver estoque suficiente

---

## Errors (Domínio)

- InvalidOrderException
- InvalidOrderItemException

Erros externos (via integração):
- falha ao obter preço → erro de pedido inválido
- falha ao reservar estoque → erro de pedido inválido

---