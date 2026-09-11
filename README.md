# Monitoria Acadêmica (EduConecta)

Plataforma web para conexão entre alunos e monitores, aproximando quem precisa de apoio em disciplinas específicas de quem tem domínio para oferecer esse suporte.

> Atividade de Estudo Programado (AEP) — Engenharia de Software / Análise e Projeto Orientado a Objeto — UniCesumar

## Sobre o projeto

A busca por apoio acadêmico hoje acontece de forma informal (grupos de mensagens, indicações boca a boca), sem histórico, avaliação prévia ou garantia de qualidade. A plataforma organiza esse processo em um fluxo estruturado: **busca → solicitação → confirmação → atendimento → avaliação**.

O projeto está alinhado ao **ODS 4 — Educação de Qualidade** (Agenda 2030 da ONU), promovendo equidade no acesso a apoio educacional complementar e fortalecendo a aprendizagem colaborativa entre pares.

## Stakeholders

| Tipo | Quem | Papel |
|---|---|---|
| Direto | Aluno | Busca apoio confiável em uma matéria e solicita sessões |
| Direto | Monitor | Divulga conhecimento, organiza disponibilidade e atende sessões |
| Direto | Administrador/Moderador | Aprova cadastros de monitores e gerencia matérias |
| Indireto | Professor | Acompanha dificuldades e pode indicar monitores |
| Indireto | Coordenação/Instituição | Reduz evasão e reprovação, apoia formalmente a iniciativa |

## Regras de negócio

| Código | Regra |
|---|---|
| RN01 | Um Monitor deve ter ao menos uma matéria de domínio cadastrada antes de receber solicitações |
| RN02 | Uma Sessão só pode ser confirmada se houver compatibilidade de horário entre Aluno e Monitor |
| RN03 | Um Aluno só pode avaliar um Monitor após a conclusão efetiva da Sessão |

## Requisitos funcionais (resumo)

- Cadastro de usuários, com diferenciação entre Aluno e Monitor
- Cadastro de matérias de domínio pelo Monitor
- Busca de monitores por matéria
- Solicitação de sessão (monitor, matéria, horário)
- Validação de compatibilidade de horário antes da confirmação
- Registro e atualização do status da Sessão (pendente → confirmada → concluída)
- Avaliação (nota + comentário) somente após sessão concluída
- Notificação por e-mail relacionada à Sessão
- Visualização de perfil conforme o papel do usuário

*Lista completa (RF01–RF13) disponível no documento da 1ª entrega, em `docs/`.*

## Arquitetura e stack

| Camada | Tecnologia | Motivo |
|---|---|---|
| Linguagem/Backend | Java | Forte suporte a POO (herança, polimorfismo, composição), alinhado à disciplina |
| Servidor HTTP | `HttpServer` nativo do JDK | Evita a complexidade de um framework completo (Spring Boot) |
| Build/Dependências | Maven | Gerenciamento de bibliotecas (driver MySQL, Gson) e estrutura padronizada |
| Persistência | JDBC (sem ORM) | Transparência sobre as queries, sem a complexidade de configurar um ORM |
| Banco de dados | MySQL | Modelo relacional adequado a um domínio com relacionamentos bem definidos |
| Frontend | HTML, CSS, JavaScript + Bootstrap | Interface simples e responsiva, sem build step adicional |
| Versionamento | Git + GitHub | Branch `main` estável, `feature/nome-da-funcionalidade` para desenvolvimento |

## Estrutura do repositório

```
EduConecta/
├── README.md
├── .gitignore
├── docs/
│   ├── diagrama-classe.png
│   ├── der.png
│   └── entrega-1.pdf
├── backend/
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── br/com/educonecta/
│                   ├── App.java
│                   ├── model/
│                   │   ├── Usuario.java
│                   │   ├── Aluno.java
│                   │   ├── Monitor.java
│                   │   ├── Materia.java
│                   │   ├── Sessao.java
│                   │   └── Avaliacao.java
│                   ├── dao/
│                   └── controller/
└── frontend/
    ├── index.html
    ├── css/
    │   └── style.css
    └── js/
        └── main.js
```

## Modelagem

- **Diagrama de Classes** (`docs/diagrama-classe.png`): `Usuario` como classe base, com `Aluno` e `Monitor` como especializações. Contempla herança, polimorfismo (`visualizarPerfil()` sobrescrito) e composição 1:N (`Monitor` → `Sessao`).
- **DER** (`docs/der.png`): tabelas `usuarios`, `alunos`, `monitores`, `materias`, `sessoes`, `avaliacoes` e a tabela associativa `monitor_materia` (relação N:N entre Monitor e Matéria).

| Diagrama de Classes | DER |
|---|---|
| Usuario | usuarios |
| Aluno | alunos |
| Monitor | monitores |
| Materia | materias |
| Sessao | sessoes |
| Avaliacao | avaliacoes |
| Monitor ↔ Materia | monitor_materia |

## Status do projeto

- [x] 1ª Entrega — Descoberta, Concepção, Justificativa Técnica, Diagrama de Classe, DER
- [ ] 2ª Entrega — CRUD completo, persistência real em MySQL, integração frontend/backend

## 👤 Integrantes

- Livia Cilião Barião Gavioli
- Rebecca de Assis Nezio

## Documentação completa

O documento completo da 1ª entrega (Descoberta, Concepção e Alinhamento, Justificativa Técnica e Arquitetural, Lista de Requisitos e Cronograma) está disponível em [`docs/entrega-1.pdf`](docs/entrega-1.pdf).