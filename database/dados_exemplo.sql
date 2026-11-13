USE educonecta;

-- Dados fictícios para desenvolvimento e testes locais.
-- As senhas são exemplos simples; não utilizar em produção.
INSERT INTO usuarios (nome, email, senha_hash)
VALUES
('Mariana Lima', 'mariana.aluno@educonecta.com', 'senha_demo'),
('Ana Souza', 'ana.monitor@educonecta.com', 'senha_demo')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

SET @id_aluno = (SELECT id_usuario FROM usuarios WHERE email = 'mariana.aluno@educonecta.com');
SET @id_monitor = (SELECT id_usuario FROM usuarios WHERE email = 'ana.monitor@educonecta.com');

INSERT IGNORE INTO alunos (id_aluno, curso, periodo)
VALUES (@id_aluno, 'Análise e Desenvolvimento de Sistemas', '2º período');

INSERT IGNORE INTO monitores (id_monitor, biografia)
VALUES (@id_monitor, 'Monitora de Java e Banco de Dados');

INSERT INTO materias (nome)
SELECT 'Programação Java'
WHERE NOT EXISTS (SELECT 1 FROM materias WHERE nome = 'Programação Java');

SET @id_materia = (SELECT id_materia FROM materias WHERE nome = 'Programação Java' LIMIT 1);

INSERT IGNORE INTO monitor_materia (id_monitor, id_materia)
VALUES (@id_monitor, @id_materia);

INSERT INTO disponibilidades (id_monitor, dia_semana, hora_inicio, hora_fim)
SELECT @id_monitor, 2, '08:00:00', '12:00:00'
WHERE NOT EXISTS (
  SELECT 1 FROM disponibilidades
  WHERE id_monitor = @id_monitor AND dia_semana = 2
    AND hora_inicio = '08:00:00' AND hora_fim = '12:00:00'
);
