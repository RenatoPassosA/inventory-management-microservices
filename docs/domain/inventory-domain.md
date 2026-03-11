# Inventory Domain

O Inventory Service é responsável pelo controle de estoque.

---

## Entities

### InventoryItem

Representa a posição atual de estoque de um produto.

#### Fields

- id
- productId
- availableQuantity
- reservedQuantity

---

### InventoryMovement

Representa movimentações de estoque.

#### Fields

- id
- productId
- type
- quantity
- createdAt

---

### StockReservation

Representa a reserva de estoque para um pedido.

#### Fields

- id
- productId
- orderId
- quantity
- status
- createdAt

---

## Responsibilities

- registrar entradas de estoque
- registrar saídas de estoque
- reservar estoque
- liberar reservas
- consultar disponibilidade
- manter histórico de movimentações

---

## Domain Rules

- estoque disponível não pode ser negativo
- reservas não podem ultrapassar disponibilidade
- entradas devem possuir quantidade positiva
- saídas devem possuir quantidade positiva
- uma reserva deve estar associada a um pedido

---

## Movement Types

Tipos sugeridos de movimentação:

- ENTRY
- EXIT
- RESERVATION
- RESERVATION_RELEASE
- ADJUSTMENT

---

## Reservation Status

Status sugeridos para reserva:

- RESERVED
- CANCELLED
- CONSUMED