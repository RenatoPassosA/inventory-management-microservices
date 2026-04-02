# Inventory Domain

O **Inventory Service** é responsável pelo controle de estoque de produtos.

Ele mantém o saldo disponível e o saldo reservado, garantindo consistência nas operações.

---

## Entities

### Inventory

Representa o estado atual do estoque de um produto.

#### Fields

- id (UUID)
- productId (UUID)
- availableQuantity (Integer)
- reservedQuantity (Integer)
- createdAt (OffsetDateTime)
- updatedAt (OffsetDateTime)

---

## Responsibilities

- Criar registro de estoque para um produto
- Adicionar estoque (entrada)
- Remover estoque (saída)
- Reservar estoque
- Liberar estoque reservado
- Consultar saldo disponível
- Validar disponibilidade

---

## Domain Rules

- availableQuantity ≥ 0
- reservedQuantity ≥ 0
- Não é permitido remover mais do que disponível
- Não é permitido reservar mais do que disponível
- Operações devem usar quantidade positiva (> 0)
- Produto deve existir no Product Service
- Não pode existir mais de um inventory por productId

---

## Derived Values

- available = availableQuantity  
- reserved = reservedQuantity  
- total = available + reserved

---

## Operations

### Add Stock
- Aumenta availableQuantity

### Remove Stock
- Reduz availableQuantity

### Reserve Stock
- Reduz availableQuantity
- Aumenta reservedQuantity

### Release Reserved Stock
- Reduz reservedQuantity
- Aumenta availableQuantity

---

## Exceptions (Domain)

- InventoryNotFoundException
- InventoryAlreadyExistsException
- InventoryInsufficientStockException
- InvalidInventoryMovementException

---