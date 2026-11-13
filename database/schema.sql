CREATE DATABASE IF NOT EXISTS educonecta;
USE educonecta;

CREATE TABLE usuarios (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alunos (
  id_aluno INT PRIMARY KEY,
  curso VARCHAR(100),
  periodo VARCHAR(20),
  FOREIGN KEY (id_aluno) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

CREATE TABLE monitores (
  id_monitor INT PRIMARY KEY,
  biografia VARCHAR(255),
  media_avaliacao DECIMAL(3,2) DEFAULT 0,
  FOREIGN KEY (id_monitor) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

CREATE TABLE materias (
  id_materia INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL
);

CREATE TABLE monitor_materia (
  id_monitor INT,
  id_materia INT,
  PRIMARY KEY (id_monitor, id_materia),
  FOREIGN KEY (id_monitor) REFERENCES monitores(id_monitor) ON DELETE CASCADE,
  FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE
);

CREATE TABLE sessoes (
  id_sessao INT AUTO_INCREMENT PRIMARY KEY,
  id_aluno INT NOT NULL,
  id_monitor INT NOT NULL,
  id_materia INT NOT NULL,
  data_hora DATETIME NOT NULL,
  status ENUM('pendente','confirmada','concluida','cancelada') DEFAULT 'pendente',
  FOREIGN KEY (id_aluno) REFERENCES alunos(id_aluno),
  FOREIGN KEY (id_monitor) REFERENCES monitores(id_monitor),
  FOREIGN KEY (id_materia) REFERENCES materias(id_materia)
);

CREATE TABLE avaliacoes (
  id_avaliacao INT AUTO_INCREMENT PRIMARY KEY,
  id_sessao INT NOT NULL UNIQUE,
  nota INT,
  comentario VARCHAR(255),
  FOREIGN KEY (id_sessao) REFERENCES sessoes(id_sessao)
);

CREATE TABLE disponibilidades (
  id_disponibilidade INT AUTO_INCREMENT PRIMARY KEY,
  id_monitor INT NOT NULL,
  dia_semana TINYINT NOT NULL,        -- 1 = segunda ... 7 = domingo
  hora_inicio TIME NOT NULL,
  hora_fim TIME NOT NULL,
  FOREIGN KEY (id_monitor) REFERENCES monitores(id_monitor) ON DELETE CASCADE
);