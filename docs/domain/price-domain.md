# Price Domain

O Price Service é responsável pelo gerenciamento de preços de produtos.

Separar preços do catálogo permite maior flexibilidade de negócio.

---

## Entity

### Price

#### Fields

- id
- productId
- basePrice
- promotionalPrice
- effectiveDate
- active

---

## Responsibilities

- cadastrar preço de produto
- atualizar preço
- manter histórico de preços
- consultar preço atual
- ativar e desativar preços

---

## Domain Rules

- preço deve estar associado a um produto válido
- preço não pode ser negativo
- apenas um preço ativo por produto
- preço promocional não pode ser maior que o preço base
- um produto pode possuir histórico de preços

---

## Pricing Logic

Regra sugerida para preço final:

- se houver `promotionalPrice` ativa, usar ela
- caso contrário, usar `basePrice`

---

## Notes

O Price Service não deve conhecer detalhes internos do catálogo além do `productId` e validação de existência do produto.