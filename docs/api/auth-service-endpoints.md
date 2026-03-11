### 📄 `docs/api/auth-service-endpoints.md`

```markdown
# Auth Service API

Responsável pela autenticação e emissão de tokens JWT (ponto de segurança por trás do API Gateway).

## 1. Login de Usuário
`POST /auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "strongPassword123"
}
Responses:

200 OK: Login bem-sucedido.

JSON
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5c...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
401 Unauthorized: Credenciais inválidas (email ou senha incorretos).

400 Bad Request: Body incompleto.

2. Registrar Novo Usuário
POST /auth/register

Request Body:

JSON
{
  "name": "João Silva",
  "email": "user@example.com",
  "password": "strongPassword123",
  "role": "CUSTOMER"
}
Responses:

201 Created: Usuário criado com sucesso. Retorna dados básicos (sem a senha).

409 Conflict: E-mail já cadastrado no sistema.

400 Bad Request: Senha fraca ou dados inválidos.