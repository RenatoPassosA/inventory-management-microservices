# Product Domain

O Product Service é responsável pelo gerenciamento do catálogo de produtos.

---

# Entity

## Product

Representa um produto disponível no sistema.

### Fields

- id
- name
- description
- sku
- category
- status

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
- listar produtos
- consultar produto por id
- ativar ou inativar produto

---

# Domain Rules

Algumas regras importantes:

- SKU deve ser único
- produto inativo não pode ser vendido
- nome do produto é obrigatório