# ADR 0001 - Organizacao por features em vez de camadas globais

## Status
Aceito

## Contexto
Uma organizacao tradicional por camadas (`controllers/`, `services/`,
`repositories/`, `entities/`) espalha uma unica funcionalidade por varias
pastas distantes umas das outras. Para um dominio com features
relativamente independentes (alunos, planos, assinaturas, cursos, aulas,
progresso, certificados), isso aumenta o custo de leitura e o acoplamento
acidental entre features.

## Decisao
Cada feature (`students`, `plans`, `subscriptions`, `courses`, `lessons`,
`progress`, `certificates`) e um pacote autocontido com sua entidade,
repositorio, service, controller(s), DTOs e mapper. Pacotes transversais
(`security`, `config`, `exceptions`) permanecem separados por serem
genuinamente compartilhados por todas as features.

## Consequencias
- Navegacao mais simples: para entender "certificados", basta abrir um
  pacote.
- Baixo acoplamento: remover uma feature (por exemplo, apagar o pacote
  `certificates/`) so exige tratar os poucos pontos onde outra feature a
  referencia diretamente (no caso, a chamada feita por `ProgressService`) -
  nao ha uma promessa de que qualquer arquivo isolado pode ser apagado sem
  nenhum ajuste em outro lugar.
- Uma dependencia entre features ainda existe quando faz sentido de
  dominio (ex.: `progress` depende de `certificates` para emitir o
  certificado automaticamente; `lessons` depende de `subscriptions` para
  checar acesso). Isso e aceitavel: baixo acoplamento nao significa zero
  acoplamento, e sim que cada dependencia e explicita e justificada.
