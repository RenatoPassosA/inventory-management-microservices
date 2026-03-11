# Order Domain

O Order Service é responsável pela criação e gerenciamento de pedidos.

Ele atua como **orquestrador do fluxo de negócio**.

---

## Entities

### Order

#### Fields

- id
- status
- totalAmount
- createdAt

---

### OrderItem

#### Fields

- id
- orderId
- productId
- quantity
- unitPrice
- totalPrice

---

## Responsibilities

- criar pedido
- validar produtos
- consultar preços
- reservar estoque
- persistir pedido
- cancelar pedido
- confirmar pedido

---

## Order Status

Possíveis estados:

- CREATED
- RESERVED
- CONFIRMED
- CANCELLED
- FAILED

---

## Domain Rules

- um pedido deve possuir ao menos um item
- quantidade de item deve ser maior que zero
- produto deve existir
- produto deve estar ativo
- item deve ter preço válido
- pedido só pode ser confirmado se estiver reservado
- pedido cancelado não pode ser confirmado

---

## Notes

O Order Service não deve manipular diretamente banco de dados de outros serviços.

Toda integração deve ocorrer via APIs REST.