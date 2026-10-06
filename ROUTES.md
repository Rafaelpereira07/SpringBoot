# Rotas da API

## Visão geral de autenticação

A aplicação usa dois mecanismos de segurança:

- JWT de aluno: header `Authorization: Bearer <token>`
  - gerado em `POST /auth/login`
  - validado por `JwtAuthenticationFilter`
  - o token contém `sub` com o `studentId` e o claim `email`
- API Key de admin: header `X-Admin-Api-Key: <chave>`
  - exigido em todas as rotas sob `/admin/**`
  - validado por `AdminApiKeyFilter`

Rotas públicas não exigem autenticação.

---

## 1) Rotas públicas

### GET /

- Descrição: landing page / health check
- Auth: nenhuma
- Body: nenhum
- Função: retorna nome da API, link para `/docs` e status
- Resposta exemplo:
  ```json
  {
    "name": "API de cursos do Wagner",
    "docs": "/docs",
    "status": "ok"
  }
  ```

### GET /plans

- Descrição: lista planos ativos
- Auth: nenhuma
- Body: nenhum
- Função: catálogo de planos disponíveis para assinatura

### GET /courses

- Descrição: lista cursos publicados
- Auth: nenhuma
- Body: nenhum
- Função: exibe catálogo público de cursos

### GET /courses/{slug}

- Descrição: detalhe de um curso publicado
- Auth: nenhuma
- Body: nenhum
- Função: retorna descrição, informações do curso e aulas públicas

### GET /certificates/{code}

- Descrição: download do certificado em PDF
- Auth: nenhuma
- Body: nenhum
- Função: entrega uma cópia do certificado em PDF
- Headers: `Content-Disposition: inline; filename="certificado-{code}.pdf"`

### GET /certificates/{code}/validate

- Descrição: valida um certificado
- Auth: nenhuma
- Body: nenhum
- Função: verifica se o código do certificado é válido

### POST /subscriptions/{paymentCode}/confirm

- Descrição: confirmação simulada de pagamento do QR Code
- Auth: nenhuma
- Body: nenhum
- Função: callback público usado para confirmar pagamento de uma assinatura pendente
- Importante: o sistema valida somente o `paymentCode` específico, sem depender de JWT

### GET /docs, /api-docs, /swagger-ui/\*\*

- Descrição: documentação Swagger/OpenAPI
- Auth: nenhuma
- Body: nenhum
- Função: acesso às docs da API

---

## 2) Autenticação do aluno

### POST /auth/login

- Descrição: autentica um aluno e gera JWT
- Auth: nenhuma
- Body (JSON):
  ```json
  {
    "email": "aluno@email.com",
    "password": "senha123"
  }
  ```
- Campos:
  - `email`: obrigatório, email válido
  - `password`: obrigatório
- Função: valida credenciais e retorna um token JWT para uso em rotas protegidas
- Resposta típica:
  ```json
  {
    "token": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "student": {
      "id": 1,
      "name": "Aluno",
      "email": "aluno@email.com"
    }
  }
  ```

### POST /students/register

- Descrição: cadastro de novo aluno
- Auth: nenhuma
- Body (JSON):
  ```json
  {
    "name": "Nome do aluno",
    "email": "aluno@email.com",
    "password": "senha12345"
  }
  ```
- Campos:
  - `name`: obrigatório, máximo 150 caracteres
  - `email`: obrigatório, válido, máximo 180 caracteres
  - `password`: obrigatório, mínimo 8 caracteres
- Função: cria um novo estudante no sistema

---

## 3) Rotas protegidas por JWT do aluno

### GET /account

- Descrição: retorna os dados do aluno autenticado
- Auth: JWT obrigatório
- Header:
  ```http
  Authorization: Bearer <token>
  ```
- Body: nenhum
- Função: consulta o perfil do aluno logado

### POST /progress/complete

- Descrição: marca uma aula como concluída
- Auth: JWT obrigatório
- Header:
  ```http
  Authorization: Bearer <token>
  ```
- Body (JSON):
  ```json
  {
    "courseSlug": "python-para-iniciantes",
    "lessonSlug": "introducao"
  }
  ```
- Campos:
  - `courseSlug`: obrigatório
  - `lessonSlug`: obrigatório
- Função: registra progresso do aluno em uma aula específica

### GET /progress/courses/{courseSlug}

- Descrição: retorna progresso do aluno em um curso
- Auth: JWT obrigatório
- Header:
  ```http
  Authorization: Bearer <token>
  ```
- Body: nenhum
- Função: consulta percentual/estado de progresso do aluno no curso

### POST /subscriptions

- Descrição: inicia inscrição com plano selecionado
- Auth: JWT obrigatório
- Header:
  ```http
  Authorization: Bearer <token>
  ```
- Body (JSON):
  ```json
  {
    "planType": "MONTHLY"
  }
  ```
- Campos:
  - `planType`: obrigatório, enum de plano
- Função: cria uma assinatura pendente e devolve QR Code de pagamento simulado

### GET /subscriptions/me

- Descrição: lista assinaturas do aluno autenticado
- Auth: JWT obrigatório
- Header:
  ```http
  Authorization: Bearer <token>
  ```
- Body: nenhum
- Função: consulta as assinaturas do aluno atual

### GET /courses/{courseSlug}/{lessonSlug}

- Descrição: acesso ao vídeo da aula
- Auth: JWT obrigatório
- Header:
  ```http
  Authorization: Bearer <token>
  ```
- Body: nenhum
- Função: entrega o vídeo da aula específica, mas exige que o aluno tenha assinatura ativa;
  a validação de assinatura é feita no serviço e não apenas pela existência do JWT

---

## 4) Rotas de admin (API key)

### POST /admin/courses

- Descrição: cria um curso
- Auth: API key de admin obrigatória
- Header:
  ```http
  X-Admin-Api-Key: <chave>
  ```
- Body (JSON):
  ```json
  {
    "title": "Curso de Java",
    "description": "Descrição do curso",
    "language": "pt-BR",
    "level": "BEGINNER",
    "thumbnailUrl": "https://...",
    "durationMinutes": 120
  }
  ```
- Campos:
  - `title`: obrigatório, máximo 150 caracteres
  - `description`: obrigatória
  - `language`: opcional
  - `level`: obrigatório
  - `thumbnailUrl`: opcional
  - `durationMinutes`: opcional, não pode ser negativo
- Função: cria um novo curso na plataforma

### PUT /admin/courses/{id}

- Descrição: atualiza um curso
- Auth: API key de admin obrigatória
- Header:
  ```http
  X-Admin-Api-Key: <chave>
  ```
- Body (JSON): mesma estrutura de `CourseRequest`
- Função: altera um curso existente

### DELETE /admin/courses/{id}

- Descrição: soft delete de um curso
- Auth: API key de admin obrigatória
- Header:
  ```http
  X-Admin-Api-Key: <chave>
  ```
- Body: nenhum
- Função: remove logicamente o curso sem excluir o registro permanentemente

### POST /admin/courses/{courseId}/lessons

- Descrição: cria uma aula para um curso
- Auth: API key de admin obrigatória
- Header:
  ```http
  X-Admin-Api-Key: <chave>
  ```
- Body (JSON):
  ```json
  {
    "title": "Introdução",
    "description": "Aula inicial",
    "thumbnailUrl": "https://...",
    "videoUrl": "https://.../video.mp4",
    "durationSeconds": 300,
    "order": 1
  }
  ```
- Campos:
  - `title`: obrigatório
  - `description`: opcional
  - `thumbnailUrl`: opcional
  - `videoUrl`: obrigatória
  - `durationSeconds`: opcional, não pode ser negativo
  - `order`: opcional, não pode ser negativo
- Função: adiciona uma nova aula a um curso

### PUT /admin/lessons/{id}

- Descrição: atualiza uma aula
- Auth: API key de admin obrigatória
- Header:
  ```http
  X-Admin-Api-Key: <chave>
  ```
- Body (JSON): mesma estrutura de `LessonRequest`
- Função: altera uma aula existente

### DELETE /admin/lessons/{id}

- Descrição: soft delete de uma aula
- Auth: API key de admin obrigatória
- Header:
  ```http
  X-Admin-Api-Key: <chave>
  ```
- Body: nenhum
- Função: remove logicamente a aula sem excluir permanentemente o registro

---

## 5) Matriz de segurança

| Rota                                 | Autenticação  | Body                        | Finalidade             |
| ------------------------------------ | ------------- | --------------------------- | ---------------------- |
| `/`                                  | Pública       | Nenhum                      | Landing page           |
| `/plans`                             | Pública       | Nenhum                      | Catálogo de planos     |
| `/courses`                           | Pública       | Nenhum                      | Lista de cursos        |
| `/courses/{slug}`                    | Pública       | Nenhum                      | Detalhes do curso      |
| `/auth/login`                        | Pública       | `email`, `password`         | Login do aluno         |
| `/students/register`                 | Pública       | `name`, `email`, `password` | Cadastro               |
| `/account`                           | JWT           | Nenhum                      | Perfil do aluno        |
| `/progress/*`                        | JWT           | `courseSlug`, `lessonSlug`  | Progresso              |
| `/subscriptions`                     | JWT           | `planType`                  | Criação de assinatura  |
| `/subscriptions/me`                  | JWT           | Nenhum                      | Minhas assinaturas     |
| `/courses/{courseSlug}/{lessonSlug}` | JWT           | Nenhum                      | Vídeo da aula          |
| `/certificates/*`                    | Pública       | Nenhum                      | Download/validação     |
| `/admin/**`                          | API key admin | Conforme endpoint           | Gestão de cursos/aulas |

---

## 6) Observações importantes do projeto

- Todas as rotas protegidas por JWT usam `CurrentStudentProvider` para obter o `studentId` do contexto de segurança, nunca confiando no valor enviado pelo cliente.
- A rota `/courses/{courseSlug}/{lessonSlug}` exige autenticação do aluno, mas a autorização real para assistir o vídeo também exige que o aluno tenha assinatura ativa. O JWT sozinho não basta.
- A rota `/subscriptions/{paymentCode}/confirm` foi desenhada como callback público para simular a confirmação do QR code.
- O sistema usa `csrf` desabilitado e `STATELESS` no Spring Security.
- A autenticação de admin é feita com header fixo `X-Admin-Api-Key`, sem JWT.
