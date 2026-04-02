# Price Domain

O Price Service é responsável pelo gerenciamento de preços de produtos, incluindo histórico e versionamento.

A separação do preço do produto permite:

- rastreabilidade
- histórico de alterações
- evolução de regras de negócio

---

## Entity

### Price

#### Fields

- id (UUID)
- productId (UUID)
- price (BigDecimal)
- currency (String)
- active (boolean)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

---

## Modelagem

### Relação

Product (1) → (N) Price

- um produto pode ter vários preços
- apenas um preço ativo por produto

---

## Responsabilidades

- criar preço
- atualizar preço (versionamento)
- manter histórico de preços
- buscar preço atual
- desativar preço anterior automaticamente

---

## Regras de Domínio

### 1. Produto deve existir
Validação feita via integração com product-service.

### 2. Preço deve ser válido
- não pode ser nulo
- deve ser maior que zero

### 3. Apenas um preço ativo por produto
Antes de criar um novo preço:
desativar preço atual → criar novo preço ativo

### 4. Atualização gera novo registro
O sistema NÃO atualiza o preço existente.

Ele:
- desativa o atual
- cria um novo registro

Isso garante:
- histórico completo
- rastreabilidade

### 5. Histórico é obrigatório
Todos os preços permanecem armazenados:
- active = true → preço atual
- active = false → histórico

---

## Application Layer

### Entrada HTTP (Adapters)

- CreatePriceRequest
- UpdatePriceRequest
- PriceResponse
- PriceHistoryResponse

### Application

- CreatePriceCommand
- UpdatePriceCommand
- PriceResult
- PriceHistoryResult

### Conversão

- PriceWebMapper (HTTP ↔ Application)
- PriceMapper (Domain ↔ Result)

---

## Integração com Product Service

- valida existência do produto
- via ProductClient
- comunicação REST com WebClient

---

## Limitações atuais

- não suporta múltiplas moedas avançadas
- não possui price list
- não possui promoções
- não possui vigência temporal avançada

---