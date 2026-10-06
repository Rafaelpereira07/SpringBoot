# ADR 0002 - Simulacao de pagamento por QR Code

## Status
Aceito

## Contexto
A plataforma precisa de um fluxo de "compra" de assinatura sem integrar um
gateway de pagamento real.

## Decisao
1. `POST /subscriptions` cria uma `Subscription` com status `PENDING` e um
   `paymentCode` aleatorio (24 bytes de `SecureRandom`, codificado em
   Base64 URL-safe) - nunca o ID numerico da assinatura.
2. Um QR Code (PNG, via ZXing) e gerado codificando apenas esse
   `paymentCode` - nenhum dado de aluno, plano ou valor monetario vai
   dentro da imagem.
3. `POST /subscriptions/{paymentCode}/confirm` simula a confirmacao do
   pagamento. E a UNICA rota que ativa a assinatura, e faz isso de forma
   idempotente (confirmar uma assinatura ja `ACTIVE` apenas retorna o
   estado atual, sem duplicar efeitos colaterais).

## Consequencias
- Abrir/acessar a imagem do QR Code, por si so, nao ativa nada - apenas o
  POST explicito de confirmacao ativa a assinatura. Isso evita a falha
  comum de tratar "o QR Code foi acessado" como prova de pagamento.
- Como o `paymentCode` e a unica credencial necessaria para confirmar,
  ele precisa ser dificil de adivinhar (daí `SecureRandom` com 24 bytes)
  e de uso controlado (assinaturas `CANCELLED`/`EXPIRED` nao podem mais
  ser confirmadas).
- Este fluxo e explicitamente uma simulacao. Nenhuma parte do sistema deve
  apresentar isso como uma transacao Pix real.
