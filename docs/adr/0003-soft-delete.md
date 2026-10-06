# ADR 0003 - Soft delete em Plan, Course e Lesson

## Status
Aceito

## Contexto
Excluir fisicamente um curso ou aula destruiria o historico de progresso
e certificados de alunos que ja concluiram (ou estao cursando) aquele
conteudo, alem de quebrar as foreign keys de `progress` e `certificates`.

## Decisao
`Plan`, `Course` e `Lesson` possuem uma coluna `deleted_at` (nullable).
"Excluir" um registro apenas define `deleted_at = now()`. Todas as
consultas publicas (listagens, detalhe de curso, checagem de acesso a
video) filtram explicitamente `deletedAt IS NULL` no repositorio - o
filtro nao e implicito nem automatico via anotacao magica, e cada query
relevante foi revisada individualmente.

## Consequencias
- `progress` e `certificates` continuam validos e consultaveis mesmo
  depois que o curso/aula associado e "excluido".
- Reativar um registro (se necessario no futuro) e trivial: bastaria
  zerar `deleted_at`. Essa operacao nao esta exposta na v1 porque nao foi
  solicitada, mas o modelo de dados ja suporta.
- Contrapartida: cada novo repositorio/consulta sobre essas tres entidades
  precisa lembrar explicitamente do filtro `deletedAt IS NULL`. Isso e
  uma decisao consciente (nao usar um mecanismo automatico "por magica")
  para que cada consulta seja revisada e intencional.
