# DAO (Data Access Object)

Esta pasta contém as classes responsáveis pelo acesso direto ao banco de dados MySQL, utilizando JDBC.

Cada classe DAO é responsável por uma entidade do domínio (ex: `MonitorDAO`, `SessaoDAO`) e implementa as operações de persistência (inserir, buscar, atualizar, remover) correspondentes, isolando o restante da aplicação dos detalhes de SQL e conexão com o banco.