# Product Domain

O Product Service é responsável pelo gerenciamento do catálogo de produtos.

O domínio foi modelado seguindo princípios de **Clean Architecture**, isolando regras de negócio da camada de transporte (HTTP) e da infraestrutura.

---

# Entity

## Product

Representa um produto disponível no sistema.

### Fields

- id (UUID)
- name (String)
- description (String)
- sku (String)
- category (String)
- status (ProductStatus)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

---

# Status

Possíveis estados do produto:

- ACTIVE
- INACTIVE

---

# Responsibilities

O domínio de produto é responsável por:

- criar produtos
- atualizar produtos
- deletar produtos
- listar produtos (com paginação)
- consultar produto por id
- consultar produto por SKU

---

# Domain Rules

Regras de negócio implementadas:

- SKU deve ser único
- nome do produto é obrigatório
- produto deve possuir categoria
- produto é criado com status ACTIVE por padrão
- produto deve existir para ser atualizado ou deletado

---

# Repository Contract

O domínio define a interface:

ProductRepository

Responsável por:

- save(Product)
- findById(UUID)
- findBySku(String)
- existsBySku(String)
- deleteById(UUID)
- findAll(Pageable)

A implementação dessa interface é feita na camada de infraestrutura.

---

# Application Layer (Use Case)

O domínio é exposto através do `ProductUseCase`, que recebe e retorna objetos desacoplados de HTTP:

## Commands

- CreateProductCommand
- UpdateProductCommand

## Results

- ProductResult

Esses objetos representam o contrato interno da aplicação.

---

# Architecture Decisions

- O domínio não depende de frameworks
- O acesso ao banco é abstraído via `ProductRepository`
- A aplicação utiliza `command/result` ao invés de `request/response`
- O controller faz a conversão via `WebMapper`
