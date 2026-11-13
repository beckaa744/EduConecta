# EduConecta — Monitoria Acadêmica

Plataforma para conexão entre alunos e monitores, aproximando quem precisa de apoio em disciplinas específicas de quem tem domínio para oferecer esse suporte.

> Atividade de Estudo Programada (AEP) — 4º semestre (2026.2) — Engenharia de Software — UniCesumar
>
> Repositório: https://github.com/beckaa744/EduConecta

## Sumário

1. [Sobre o projeto](#sobre-o-projeto)
2. [Integrantes](#integrantes)
3. [Status das entregas](#status-das-entregas)
4. [Como executar](#como-executar)
5. [Requisitos funcionais](#requisitos-funcionais)
6. [Regras de negócio](#regras-de-negócio)
7. [Partes interessadas](#partes-interessadas)
8. [Cronograma](#cronograma)
9. [Arquitetura e tecnologias](#arquitetura-e-tecnologias)
10. [Estrutura do repositório](#estrutura-do-repositório)
11. [Modelagem](#modelagem)
12. [Programação Orientada a Objetos](#programação-orientada-a-objetos)
13. [CRUD e persistência](#crud-e-persistência)
14. [Alterações em relação à 1ª entrega](#alterações-em-relação-à-1ª-entrega)
15. [Limitações conhecidas](#limitações-conhecidas)
16. [Documentação completa](#documentação-completa)

## Sobre o projeto

A busca por apoio acadêmico acontece hoje de forma informal (grupos de mensagens, indicações boca a boca), sem histórico, sem avaliação prévia e sem garantia de qualidade. Isso gera insegurança na escolha do monitor e, em alguns casos, leva o aluno a desistir de procurar ajuda. Do lado do monitor, falta um canal para divulgar as matérias que domina e organizar horários.

O EduConecta organiza esse processo em um fluxo estruturado: **busca → solicitação → confirmação → atendimento → avaliação**.

O projeto está alinhado ao **ODS 4 — Educação de Qualidade** (Agenda 2030 da ONU), com foco na meta 4.3 (acesso igualitário à educação superior de qualidade). Ele promove equidade no acesso a apoio educacional complementar e fortalece a aprendizagem colaborativa entre pares.

## Integrantes

- Livia Cilião Barião Gavioli
- Rebecca de Assis Nezio

## Status das entregas

- [x] **1ª entrega:** Descoberta, Concepção, Justificativa Técnica, Diagrama de Classes e DER (`docs/entrega-1.pdf`)
- [ ] **2ª entrega (prazo: 13/11/2026):** software funcional com CRUD, persistência em MySQL e regras de negócio (em andamento; veja a coluna *Situação* na tabela de requisitos)

## Como executar

### Pré-requisitos

| Ferramenta | Versão | Observação |
|---|---|---|
| JDK | 17 ou superior | Verifique com `java -version` |
| Maven | 3.9 ou superior | O IntelliJ IDEA já traz um Maven embutido |
| MySQL Server | 8.x | Desenvolvido e testado com a 8.0.46 |
| IntelliJ IDEA | Community ou Ultimate | Recomendado; o projeto também compila pelo terminal |

### 1. Obter o código

```bash
git clone https://github.com/beckaa744/EduConecta.git
cd EduConecta
```

### 2. Ligar o MySQL

O serviço do MySQL precisa estar rodando antes de qualquer execução.

| Sistema | Comando |
|---|---|
| Windows | `net start MySQL80` (o nome do serviço pode variar; confira em `services.msc`) |
| Linux | `sudo systemctl start mysql` |
| macOS (Homebrew) | `brew services start mysql` |

### 3. Criar o banco

Na raiz do repositório:

```bash
mysql -u root -p < database/schema.sql
```

No Windows, também funciona pelo *MySQL Command Line Client*:

```sql
source C:/caminho/para/EduConecta/database/schema.sql;
```

Confira com `USE educonecta; SHOW TABLES;`. Devem aparecer **8 tabelas**: `alunos`, `avaliacoes`, `disponibilidades`, `materias`, `monitor_materia`, `monitores`, `sessoes` e `usuarios`.

Para recriar o banco do zero (apaga todos os dados): `DROP DATABASE educonecta;` e rode o script de novo.

### 4. Configurar a conexão

Abra `src/backend/src/main/java/br/com/educonecta/config/ConexaoFactory.java` e ajuste:

```java
private static final String URL = "jdbc:mysql://localhost:3306/educonecta";
private static final String USUARIO = "root";
private static final String SENHA = "sua_senha";   // troque pela senha do seu MySQL
```

> A senha do repositório é um valor de exemplo (`sua_senha`). Cada pessoa usa a senha do próprio MySQL e **não deve enviá-la ao GitHub**.

### 5. Executar

**Pelo IntelliJ IDEA (recomendado)**

1. **File → Open** e escolha a pasta `src/backend` (onde está o `pom.xml`).
2. Se o IntelliJ perguntar, escolha **Trust Project** e clique em **Load Maven Changes** para baixar o driver do MySQL.
3. Em **File → Project Structure → Project**, confira o SDK (JDK 17 ou superior).
4. Abra uma das classes da tabela abaixo e clique no triângulo verde ao lado do `main`.

**Pelo terminal** (a partir de `src/backend`):

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass=br.com.educonecta.App
```

Para executar outra classe, troque o valor de `-Dexec.mainClass`.

### 6. Demonstração

O sistema roda pelo console. As classes abaixo, em `src/backend/src/main/java/br/com/educonecta/`, têm um método `main` e exercitam as funcionalidades:

| Classe | O que demonstra |
|---|---|
| `App` | Cadastro de monitor, disponibilidade semanal e a validação de horário da RN02, com persistência no MySQL |
| `TesteMateria` | CRUD de matéria e vínculo monitor ↔ matéria (RN01) |
| `TesteSessao` | Criação de sessão, mudança de status e detecção de conflito de horário |
| `TesteSessaoService` | Regras RN01 e RN02 e o ciclo completo da sessão: solicitar, confirmar e concluir. Mostra também os casos recusados |

Cada teste apaga os registros que criou. Se uma execução for interrompida por erro, podem sobrar dados e a próxima dará erro de e-mail duplicado. Em um banco de testes, limpe com:

```sql
USE educonecta;
DELETE FROM sessoes;
DELETE FROM usuarios;
DELETE FROM materias;
```

### Problemas comuns

| Mensagem | Causa provável |
|---|---|
| `Communications link failure` | O serviço do MySQL está desligado |
| `Access denied for user 'root'` | A senha em `ConexaoFactory` não é a do seu MySQL |
| `Unknown database 'educonecta'` | O `schema.sql` ainda não foi executado |
| `No suitable driver found` | O Maven não carregou o driver: use **Load Maven Changes** |
| `Duplicate entry '...' for key 'email'` | Sobraram dados de uma execução anterior (veja a limpeza acima) |

## Requisitos funcionais

Lista completa do documento da 1ª entrega (`docs/entrega-1.pdf`), com a situação atual de cada um.

| Código | Requisito | Situação |
|---|---|---|
| RF01 | O sistema deve permitir o cadastro de usuários com os dados básicos definidos para a classe Usuário. | Implementado (`AlunoDAO`, `MonitorDAO`) |
| RF02 | O sistema deve permitir diferenciar usuários que atuam como Aluno e Monitor. | Implementado (herança `Usuario` → `Aluno`/`Monitor`; tabelas `alunos` e `monitores`) |
| RF03 | O sistema deve permitir ao Monitor cadastrar uma ou mais matérias de domínio. | Implementado (`MateriaDAO`, `MonitorDAO.vincularMateria`) |
| RF04 | O sistema deve permitir ao Aluno buscar monitores por matéria. | Implementado (`MonitorDAO.buscarPorMateria`) |
| RF05 | O sistema deve permitir ao Aluno solicitar uma sessão informando monitor, matéria e horário desejado. | Implementado (`SessaoService.solicitar`) |
| RF06 | O sistema deve validar a compatibilidade do horário solicitado com a disponibilidade cadastrada pelo Monitor antes da confirmação. | Implementado (`SessaoService.confirmar`, `Disponibilidade.cobre`) |
| RF07 | O sistema deve registrar a Sessão e seu status. | Implementado (`SessaoDAO`, `StatusSessao`) |
| RF08 | O sistema deve permitir concluir uma Sessão. | Implementado (`SessaoService.concluir`) |
| RF09 | O sistema deve permitir ao Aluno avaliar o Monitor somente após a conclusão da Sessão. | Em andamento (`AvaliacaoService`) |
| RF10 | O sistema deve armazenar nota e comentário da Avaliação. | Implementado (`AvaliacaoDAO`) |
| RF11 | O sistema deve permitir o envio de notificação por e-mail relacionada à Sessão. | Parcial: `NotificacaoEmail` simula o envio no console |
| RF12 | O sistema deve manter relacionamentos consistentes entre usuários, monitores, matérias, sessões e avaliações. | Implementado (chaves estrangeiras do `schema.sql`) |
| RF13 | O sistema deve permitir visualizar o perfil dos usuários conforme o papel desempenhado. | Implementado (`visualizarPerfil()` em `Aluno` e `Monitor`) |

## Regras de negócio

| Código | Regra | Onde é aplicada |
|---|---|---|
| RN01 | Um Monitor deve ter ao menos uma matéria de domínio cadastrada antes de receber solicitações de sessão. | `SessaoService.solicitar`: recusa o monitor sem matéria e também o que não domina a matéria escolhida |
| RN02 | Uma Sessão só pode ser confirmada se houver compatibilidade entre o horário solicitado pelo Aluno e a disponibilidade cadastrada pelo Monitor. | `SessaoService.confirmar`: a sessão (60 minutos) precisa caber inteira em uma janela semanal do monitor (`Disponibilidade.cobre`). Também recusa conflito com outra sessão do mesmo monitor |
| RN03 | Um Aluno só pode avaliar um Monitor após a conclusão efetiva de uma Sessão. | `AvaliacaoService` (em andamento) |

## Partes interessadas

| Tipo | Quem | Necessidade | Valor entregue |
|---|---|---|---|
| Direto | Aluno | Encontrar apoio confiável em uma matéria e solicitar uma sessão | Busca centralizada, histórico de sessões e possibilidade de avaliar o atendimento |
| Direto | Monitor | Divulgar conhecimentos e organizar disponibilidade | Cadastro de matérias, recebimento de solicitações e histórico |
| Direto | Administrador/Moderador | Acompanhar cadastros e governar o conteúdo | Maior controle sobre monitores e matérias cadastradas |
| Indireto | Professor | Acompanhar dificuldades e orientar estudantes | Possibilidade de indicar ou reconhecer canais de apoio entre pares |
| Indireto | Coordenação/Instituição | Reduzir dificuldades acadêmicas e fortalecer o apoio ao estudante | Estrutura formal de monitoria e dados organizados sobre atendimentos |

## Cronograma

Período do projeto: **01/08/2026 a 13/11/2026**.

### 1ª entrega (documento, diagramas e estrutura)

Espelha o cronograma do documento `docs/entrega-1.pdf`.

| Data | Atividade | Responsável |
|---|---|---|
| 07/08 | Reunião de alinhamento inicial sobre a Plataforma de Monitoria Acadêmica. | Livia Cilião Barião Gavioli e Rebecca de Assis Nezio |
| 24/08 | Definição da Concepção e Alinhamento com o ODS 4; levantamento de requisitos funcionais. | Livia Cilião Barião Gavioli |
| 24/08 | Descoberta: levantamento dos principais problemas (dificuldade de localizar monitor), dores e necessidades das partes interessadas. | Rebecca de Assis Nezio |
| 25/08 | Listagem e definição das regras de negócio e estruturação do fluxo conceitual da plataforma. | Livia Cilião Barião Gavioli |
| 03/09 | Criação do Diagrama de Classes (incluindo especialização de Usuário em Aluno e Monitor) e descrição dos elementos. | Rebecca de Assis Nezio |
| 03/09 | Definição e criação das justificativas técnicas e arquiteturais do sistema (Java, MySQL, JDBC). | Livia Cilião Barião Gavioli |
| 04/09 | Finalização do Diagrama de Entidade-Relacionamento (DER). | Rebecca de Assis Nezio |
| 05/09 | Revisão da estruturação do documento em formato acadêmico e início dos ajustes. | Livia Cilião Barião Gavioli e Rebecca de Assis Nezio |
| 08/09 | Finalização dos ajustes e conclusão do material da Atividade de Estudo Programada. | Livia Cilião Barião Gavioli e Rebecca de Assis Nezio |

### 2ª entrega (implementação)

Prazo de entrega: **13/11/2026**.

| Data | Atividade | Responsável | Situação |
|---|---|---|---|
| 11/09 | Estrutura inicial do repositório (pastas, README e diagramas da 1ª entrega) | Rebecca de Assis Nezio | Concluído |
| 08/11 | Conexão com o MySQL (`ConexaoFactory`), `schema.sql` e CRUD de Monitor (`MonitorDAO`) | Rebecca de Assis Nezio | Concluído |
| 13/11 | DAOs de Matéria, Disponibilidade e Sessão; models `Sessao`, `StatusSessao` e `Disponibilidade`; vínculo monitor ↔ matéria; `SessaoService` (RN01 e RN02); classes de demonstração | Rebecca de Assis Nezio | Concluído |
| 13/11 | `AlunoDAO`, `Avaliacao`, `AvaliacaoDAO`, `Notificacao` e `NotificacaoEmail` | Livia Cilião Barião Gavioli | Concluído |
| 13/11 | Atualização do DER e do Diagrama de Classes (`disponibilidades`, `id_materia`, `StatusSessao`, serviços) | Livia Cilião Barião Gavioli e Rebecca de Assis Nezio | Concluído |
| 13/11 | `AvaliacaoService` (RN03) e teste | Rebecca de Assis Nezio | Em andamento |
| 13/11 | Menu de console no `App.java` demonstrando o fluxo completo | Rebecca de Assis Nezio | A fazer |
| 13/11 | README final e registro de alterações | Livia Cilião Barião Gavioli e Rebecca de Assis Nezio | Em andamento |
| 13/11 | Entrega da 2ª etapa (código-fonte e script do banco no GitHub) | Livia Cilião Barião Gavioli e Rebecca de Assis Nezio | A fazer |

## Arquitetura e tecnologias

| Camada | Tecnologia | Motivo |
|---|---|---|
| Linguagem | Java 17 | Forte suporte a POO (abstração, herança, polimorfismo, composição), alinhado à disciplina |
| Build e dependências | Maven | Gerencia o driver do MySQL e padroniza a estrutura do projeto |
| Persistência | JDBC (sem ORM) | Transparência sobre as consultas, sem a complexidade de configurar um ORM |
| Banco de dados | MySQL 8.x | Modelo relacional adequado a um domínio com entidades e relacionamentos bem definidos |
| Interface | Console | Foco em arquitetura, POO, regras de negócio e persistência (veja *Alterações*) |
| Versionamento | Git + GitHub | Branch `main`, com contribuição de todas as integrantes |

Organização em camadas, dentro de `br.com.educonecta`:

| Pacote | Responsabilidade |
|---|---|
| `model` | Entidades do domínio e suas regras (`Usuario`, `Aluno`, `Monitor`, `Materia`, `Disponibilidade`, `Sessao`, `StatusSessao`, `Avaliacao`) |
| `dao` | Acesso ao banco via JDBC, um DAO por entidade (`PreparedStatement`, transações onde há mais de uma tabela) |
| `service` | Regras de negócio RN01, RN02 e RN03 |
| `notificacao` | Interface `Notificacao` e a implementação `NotificacaoEmail` |
| `config` | `ConexaoFactory`: URL, usuário e senha do banco em um único lugar |
| `controller` | Reservado para uma futura API HTTP (não implementado nesta entrega) |

## Estrutura do repositório

```
EduConecta/
├── README.md
├── .gitignore
├── docs/
│   ├── der.jpeg
│   ├── diagrama_classes.png
│   └── entrega-1.pdf
├── database/
│   └── schema.sql
├── frontend/                  (estrutura prevista na 1ª entrega; sem implementação)
│   ├── index.html
│   ├── css/style.css
│   └── js/main.js
└── src/
    └── backend/
        ├── pom.xml
        └── src/main/java/br/com/educonecta/
            ├── App.java
            ├── TesteMateria.java
            ├── TesteSessao.java
            ├── TesteSessaoService.java
            ├── config/
            │   └── ConexaoFactory.java
            ├── model/
            │   ├── Usuario.java
            │   ├── Aluno.java
            │   ├── Monitor.java
            │   ├── Materia.java
            │   ├── Disponibilidade.java
            │   ├── Sessao.java
            │   ├── StatusSessao.java
            │   └── Avaliacao.java
            ├── dao/
            │   ├── AlunoDAO.java
            │   ├── MonitorDAO.java
            │   ├── MateriaDAO.java
            │   ├── DisponibilidadeDAO.java
            │   ├── SessaoDAO.java
            │   └── AvaliacaoDAO.java
            ├── notificacao/
            │   ├── Notificacao.java
            │   └── NotificacaoEmail.java
            ├── service/
            │   ├── SessaoService.java
            │   └── AvaliacaoService.java
            └── controller/    (reservado)
```

## Modelagem

- **Diagrama de Classes** (`docs/diagrama_classes.png`): `Usuario` é a classe abstrata base, com `Aluno` e `Monitor` como especializações. Contempla herança, polimorfismo (`visualizarPerfil()` sobrescrito), composição 1:N (`Monitor` → `Sessao` e `Monitor` → `Disponibilidade`), a interface `Notificacao` e as classes de serviço.
- **DER** (`docs/der.jpeg`): 8 tabelas: `usuarios`, `alunos`, `monitores`, `materias`, `monitor_materia` (relação N:N entre Monitor e Matéria), `sessoes`, `avaliacoes` e `disponibilidades`.

| Diagrama de Classes | DER |
|---|---|
| `Usuario` | `usuarios` |
| `Aluno` | `alunos` |
| `Monitor` | `monitores` |
| `Materia` | `materias` |
| `Monitor` ↔ `Materia` | `monitor_materia` |
| `Sessao` | `sessoes` |
| `Avaliacao` | `avaliacoes` |
| `Disponibilidade` | `disponibilidades` |
| `StatusSessao` | coluna `status` de `sessoes` |
| `Notificacao`, `NotificacaoEmail`, `SessaoService`, `AvaliacaoService` | sem tabela (comunicação e regras) |

## Programação Orientada a Objetos

| Pilar | Onde aparece |
|---|---|
| Classe abstrata | `Usuario` (com o método abstrato `visualizarPerfil()`) |
| Interface | `Notificacao`, implementada por `NotificacaoEmail` |
| Herança | `Aluno` e `Monitor` estendem `Usuario` |
| Polimorfismo (`@Override`) | `visualizarPerfil()` em `Aluno` e `Monitor`; `enviar()` em `NotificacaoEmail` |
| Encapsulamento | Atributos privados com getters e setters nas entidades |
| Composição | `Monitor` possui `Sessao` e `Disponibilidade` (modelo de classes e chaves estrangeiras com `ON DELETE CASCADE` no caso das disponibilidades) |

O código usa tipagem explícita e a convenção CamelCase.

## CRUD e persistência

Todos os dados são gravados em um banco MySQL real, por JDBC. Nenhuma funcionalidade usa listas em memória ou arquivos de texto.

| Entidade | DAO | Create | Read | Update | Delete |
|---|---|---|---|---|---|
| Monitor | `MonitorDAO` | `inserir` | `buscarPorId`, `listar`, `buscarPorMateria` | `atualizar` | `deletar` |
| Aluno | `AlunoDAO` | `inserir` | `buscarPorId`, `listar` | `atualizar` | `deletar` |
| Matéria | `MateriaDAO` | `inserir` | `buscarPorId`, `listar` | `atualizar` | `deletar` |
| Disponibilidade | `DisponibilidadeDAO` | `inserir` | `buscarPorId`, `listarPorMonitor` | `atualizar` | `deletar` |
| Sessão | `SessaoDAO` | `inserir` | `buscarPorId`, `listar`, `listarPorAluno`, `listarPorMonitor` | `atualizar` | `deletar` |
| Avaliação | `AvaliacaoDAO` | `inserir` | `buscarPorSessao`, `listar` | não se aplica | não se aplica |

Cadastros que gravam em duas tabelas (`usuarios` + `alunos` ou `monitores`) usam transação, com `rollback` em caso de erro. O script de criação das tabelas está em `database/schema.sql`.

## Alterações em relação à 1ª entrega

Mudanças feitas durante a implementação, com a justificativa de cada uma.

| # | Alteração | Na 1ª entrega | Agora | Justificativa |
|---|---|---|---|---|
| 1 | Pasta `src/` agrupando o backend | `backend/` na raiz | `src/backend/`; o `frontend/` permanece na raiz | Seguir a estrutura `/src`, `/docs` e `/database` pedida nas diretrizes da AEP |
| 2 | Pasta `database/` na raiz | Dentro de `backend/` | Na raiz, com o `schema.sql` | Atender as diretrizes, que pedem o script SQL na pasta `/database` |
| 3 | Nome oficial do projeto | "Monitoria Acadêmica" (e "Nexus Acadêmico" no cronograma) | **EduConecta** (pacote `br.com.educonecta`) | Padronizar o nome no código, no README e na documentação |
| 4 | Pacote `config` com `ConexaoFactory` | Não descrito | Criado | Centralizar a conexão JDBC com o MySQL |
| 5 | `Usuario` como classe abstrata | Classe concreta no diagrama | `abstract`, com `visualizarPerfil()` abstrato | Atender o requisito de POO (classe abstrata e `@Override`) |
| 6 | Tabela `disponibilidades` | DER com 7 tabelas | 8 tabelas | Permitir a validação de RN02 e RF06, que exigem a disponibilidade cadastrada do monitor |
| 7 | Remoção do `AppTest` | Arquivo de exemplo gerado pelo Maven (`br.com.ans`) | Removido | Não tinha relação com o sistema |
| 8 | Classe `Disponibilidade` e `DisponibilidadeDAO` | Não estavam no diagrama de classes | Criadas | Implementar a RN02 (a sessão de 60 minutos precisa caber na janela do monitor) |
| 9 | Coluna `id_materia` em `sessoes` | `sessoes` sem matéria | Com `id_materia` e chave estrangeira para `materias` | O RF05 exige informar a matéria na solicitação, e a RN01 valida o domínio sobre ela |
| 10 | Enum `StatusSessao` | Status como texto (`String`) | Enum | Evitar valores inválidos e concentrar as transições de estado |
| 11 | Interface `Notificacao` e pacote `notificacao` | Apenas a classe `NotificacaoEmail` | Interface + implementação | Polimorfismo e troca simples por um envio real |
| 12 | Camada `service` | Não descrita | `SessaoService` (RN01, RN02) e `AvaliacaoService` (RN03) | Separar as regras de negócio do acesso a dados |
| 13 | API HTTP e frontend não implementados | `HttpServer` nativo, HTML, CSS, JavaScript e Bootstrap | Interface por console | Concentrar o esforço em POO, banco de dados e regras de negócio, que são o foco da avaliação. A pasta `frontend/` e o pacote `controller` ficam como estrutura prevista |
| 14 | Métodos de negócio fora das entidades | `Aluno` com `buscarMonitor`, `solicitarSessao` e `avaliarMonitor`; `Monitor` com `cadastrarMateria` | Essas ações ficam em `SessaoService`, `AvaliacaoService` e `MonitorDAO` | Manter as entidades simples e as regras em uma camada própria |
| 15 | Gson removido da stack | Citado no README da 1ª entrega | Não utilizado | Sem API HTTP, não há serialização JSON; o `pom.xml` só depende do driver do MySQL |

## Limitações conhecidas

- A notificação por e-mail é **simulada**: `NotificacaoEmail` imprime a mensagem no console. O envio real exigiria um servidor SMTP.
- As senhas são gravadas como recebidas, sem hash, apesar do nome da coluna `senha_hash`. Para uso real, seria necessário aplicar BCrypt ou similar.
- Não há interface gráfica nem API HTTP (veja o item 13 das alterações).
- Não há autenticação nem controle de acesso por perfil.

## Documentação completa

O documento completo da 1ª entrega (Descoberta, Concepção e Alinhamento, Justificativa Técnica e Arquitetural, Lista de Requisitos e Cronograma) está em [`docs/entrega-1.pdf`](docs/entrega-1.pdf).
