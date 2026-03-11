# Auth Domain

O Auth Service é responsável pela autenticação e autorização de usuários.

---

## Entities

### User

#### Fields

- id
- username
- email
- passwordHash
- role

---

### Role

#### Fields

- id
- name

---

## Responsibilities

- registrar usuários
- autenticar usuários
- gerar tokens JWT
- validar tokens
- controlar permissões de acesso

---

## Domain Rules

- e-mail deve ser único
- username deve ser único
- senha não deve ser armazenada em texto puro
- role deve ser válida
- token deve possuir expiração

---

## Security Model

A autenticação será baseada em **JWT (JSON Web Token)**.

Fluxo esperado:

1. usuário envia credenciais
2. sistema autentica usuário
3. sistema gera token JWT
4. cliente usa token nas próximas requisições
5. gateway ou serviços protegidos validam o token

---

## Suggested Roles

Papéis iniciais sugeridos:

- ADMIN
- USER