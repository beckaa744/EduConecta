package br.com.educonecta;

import br.com.educonecta.dao.AlunoDAO;
import br.com.educonecta.dao.AvaliacaoDAO;
import br.com.educonecta.dao.DisponibilidadeDAO;
import br.com.educonecta.dao.MateriaDAO;
import br.com.educonecta.dao.MonitorDAO;
import br.com.educonecta.dao.SessaoDAO;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Avaliacao;
import br.com.educonecta.model.Disponibilidade;
import br.com.educonecta.model.Materia;
import br.com.educonecta.model.Monitor;
import br.com.educonecta.model.Sessao;
import br.com.educonecta.model.Usuario;
import br.com.educonecta.service.AvaliacaoService;
import br.com.educonecta.service.SessaoService;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Menu de console do EduConecta: demonstra o fluxo completo (cadastro, busca, sessão e avaliação). */
public class App {

    private static final Scanner ENTRADA = new Scanner(System.in);
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] DIAS = {"", "segunda", "terça", "quarta", "quinta", "sexta", "sábado", "domingo"};

    private static final AlunoDAO alunoDAO = new AlunoDAO();
    private static final MonitorDAO monitorDAO = new MonitorDAO();
    private static final MateriaDAO materiaDAO = new MateriaDAO();
    private static final DisponibilidadeDAO disponibilidadeDAO = new DisponibilidadeDAO();
    private static final SessaoDAO sessaoDAO = new SessaoDAO();
    private static final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private static final SessaoService sessaoService = new SessaoService();
    private static final AvaliacaoService avaliacaoService = new AvaliacaoService();

    public static void main(String[] args) {
        System.out.println("EduConecta — Monitoria Acadêmica");
        boolean executando = true;
        while (executando) {
            mostrarMenu();
            try {
                String opcao = lerTexto("Escolha uma opção").toUpperCase();
                switch (opcao) {
                    case "1": cadastrarAluno(); break;
                    case "2": cadastrarMonitor(); break;
                    case "3": cadastrarMateria(); break;
                    case "4": vincularMateria(); break;
                    case "5": cadastrarDisponibilidade(); break;
                    case "6": listarCadastros(); break;
                    case "7": buscarMonitoresPorMateria(); break;
                    case "8": solicitarSessao(); break;
                    case "9": confirmarSessao(); break;
                    case "10": concluirSessao(); break;
                    case "11": cancelarSessao(); break;
                    case "12": avaliarSessao(); break;
                    case "13": listarSessoes(); break;
                    case "14": atualizarAluno(); break;
                    case "15": atualizarMonitor(); break;
                    case "16": excluirAluno(); break;
                    case "17": excluirMonitor(); break;
                    case "D": carregarDemonstracao(); break;
                    case "0": executando = false; break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (NoSuchElementException e) {
                executando = false;   // fim da entrada (Ctrl+D / Ctrl+Z)
            } catch (SQLException e) {
                tratarErroBanco(e);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use dd/MM/aaaa HH:mm (data e hora) ou HH:mm (horário).");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Operação recusada: " + e.getMessage());
            }
        }
        System.out.println("Até logo!");
    }

    // ---------------------------------------------------------------- menu

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("================ EduConecta ================");
        System.out.println(" CADASTROS");
        System.out.println("   1) Cadastrar aluno");
        System.out.println("   2) Cadastrar monitor");
        System.out.println("   3) Cadastrar matéria");
        System.out.println("   4) Vincular matéria a um monitor");
        System.out.println("   5) Cadastrar disponibilidade de um monitor");
        System.out.println(" CONSULTAS");
        System.out.println("   6) Listar alunos, monitores e matérias");
        System.out.println("   7) Buscar monitores por matéria");
        System.out.println(" SESSÕES");
        System.out.println("   8) Solicitar sessão");
        System.out.println("   9) Confirmar sessão");
        System.out.println("  10) Concluir sessão");
        System.out.println("  11) Cancelar sessão");
        System.out.println("  12) Avaliar sessão");
        System.out.println("  13) Listar sessões");
        System.out.println(" MANUTENÇÃO");
        System.out.println("  14) Atualizar aluno");
        System.out.println("  15) Atualizar monitor");
        System.out.println("  16) Excluir aluno");
        System.out.println("  17) Excluir monitor");
        System.out.println("   D) Carregar dados de demonstração");
        System.out.println("   0) Sair");
    }

    // ----------------------------------------------------------- cadastros

    private static void cadastrarAluno() throws SQLException {
        String nome = lerTexto("Nome");
        String email = lerTexto("E-mail");
        String senha = lerTexto("Senha");
        String curso = lerTexto("Curso");
        String periodo = lerTexto("Período");
        Aluno aluno = new Aluno(0, nome, email, senha, curso, periodo);
        alunoDAO.inserir(aluno);
        System.out.println("Aluno cadastrado com id " + aluno.getId() + ".");
    }

    private static void cadastrarMonitor() throws SQLException {
        String nome = lerTexto("Nome");
        String email = lerTexto("E-mail");
        String senha = lerTexto("Senha");
        String biografia = lerTexto("Biografia");
        Monitor monitor = new Monitor(0, nome, email, senha, biografia);
        monitorDAO.inserir(monitor);
        System.out.println("Monitor cadastrado com id " + monitor.getId() + ".");
    }

    private static void cadastrarMateria() throws SQLException {
        Materia materia = new Materia(0, lerTexto("Nome da matéria"));
        materiaDAO.inserir(materia);
        System.out.println("Matéria cadastrada com id " + materia.getId() + ".");
    }

    private static void vincularMateria() throws SQLException {
        mostrarMonitores();
        mostrarMaterias();
        int idMonitor = lerInt("Id do monitor");
        int idMateria = lerInt("Id da matéria");
        exigirMonitor(idMonitor);
        exigirMateria(idMateria);
        monitorDAO.vincularMateria(idMonitor, idMateria);
        System.out.println("Matéria vinculada ao monitor.");
    }

    private static void cadastrarDisponibilidade() throws SQLException {
        mostrarMonitores();
        int idMonitor = lerInt("Id do monitor");
        exigirMonitor(idMonitor);
        int dia = lerInt("Dia da semana (1 = segunda ... 7 = domingo)");
        LocalTime inicio = LocalTime.parse(lerTexto("Hora inicial (HH:mm)"));
        LocalTime fim = LocalTime.parse(lerTexto("Hora final (HH:mm)"));
        Disponibilidade disponibilidade = new Disponibilidade(idMonitor, dia, inicio, fim);
        disponibilidadeDAO.inserir(disponibilidade);
        System.out.println("Disponibilidade cadastrada: " + DIAS[dia] + ", " + inicio + " às " + fim + ".");
    }

    // ------------------------------------------------------------ consultas

    private static void listarCadastros() throws SQLException {
        // polimorfismo: a mesma chamada visualizarPerfil() devolve o texto de cada tipo de usuário
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.addAll(alunoDAO.listar());
        usuarios.addAll(monitorDAO.listar());
        System.out.println("--- Usuários ---");
        if (usuarios.isEmpty()) System.out.println("(nenhum)");
        for (Usuario u : usuarios) {
            System.out.println("  [" + u.getId() + "] " + u.visualizarPerfil());
        }
        System.out.println("--- Disponibilidades ---");
        boolean alguma = false;
        for (Monitor m : monitorDAO.listar()) {
            for (Disponibilidade d : disponibilidadeDAO.listarPorMonitor(m.getId())) {
                System.out.println("  " + m.getNome() + ": " + DIAS[d.getDiaSemana()] + ", "
                        + d.getHoraInicio() + " às " + d.getHoraFim());
                alguma = true;
            }
        }
        if (!alguma) System.out.println("(nenhuma)");
        mostrarMaterias();
    }

    private static void buscarMonitoresPorMateria() throws SQLException {
        mostrarMaterias();
        int idMateria = lerInt("Id da matéria");
        exigirMateria(idMateria);
        List<Monitor> monitores = monitorDAO.buscarPorMateria(idMateria);
        if (monitores.isEmpty()) {
            System.out.println("Nenhum monitor domina essa matéria.");
        }
        for (Monitor m : monitores) {
            System.out.println("  [" + m.getId() + "] " + m.visualizarPerfil());
        }
    }

    // -------------------------------------------------------------- sessões

    private static void solicitarSessao() throws SQLException {
        mostrarAlunos();
        mostrarMonitores();
        mostrarMaterias();
        int idAluno = lerInt("Id do aluno");
        int idMonitor = lerInt("Id do monitor");
        int idMateria = lerInt("Id da matéria");
        LocalDateTime dataHora = LocalDateTime.parse(lerTexto("Data e hora (dd/MM/aaaa HH:mm)"), DATA_HORA);
        Sessao sessao = sessaoService.solicitar(idAluno, idMonitor, idMateria, dataHora);
        System.out.println("Sessão solicitada: " + descrever(sessao));
    }

    private static void confirmarSessao() throws SQLException {
        mostrarSessoes();
        Sessao sessao = sessaoService.confirmar(lerInt("Id da sessão"));
        System.out.println("Sessão confirmada: " + descrever(sessao));
    }

    private static void concluirSessao() throws SQLException {
        mostrarSessoes();
        Sessao sessao = sessaoService.concluir(lerInt("Id da sessão"));
        System.out.println("Sessão concluída: " + descrever(sessao));
    }

    private static void cancelarSessao() throws SQLException {
        mostrarSessoes();
        Sessao sessao = sessaoService.cancelar(lerInt("Id da sessão"));
        System.out.println("Sessão cancelada: " + descrever(sessao));
    }

    private static void avaliarSessao() throws SQLException {
        mostrarSessoes();
        int idSessao = lerInt("Id da sessão");
        int nota = lerInt("Nota (1 a 5)");
        String comentario = lerTexto("Comentário");
        Avaliacao avaliacao = avaliacaoService.avaliar(idSessao, nota, comentario);
        System.out.println("Avaliação registrada (id " + avaliacao.getId() + ", nota " + avaliacao.getNota() + ").");
    }

    private static void listarSessoes() throws SQLException {
        mostrarSessoes();
    }

    // ------------------------------------------------------------ manutenção

    private static void atualizarAluno() throws SQLException {
        mostrarAlunos();
        int id = lerInt("Id do aluno");
        Aluno aluno = alunoDAO.buscarPorId(id);
        if (aluno == null) throw new IllegalArgumentException("Aluno não encontrado.");
        System.out.println("Atual: " + aluno.visualizarPerfil());
        String nome = lerTexto("Novo nome (Enter mantém)");
        String curso = lerTexto("Novo curso (Enter mantém)");
        String periodo = lerTexto("Novo período (Enter mantém)");
        if (!nome.isEmpty()) aluno.setNome(nome);
        if (!curso.isEmpty()) aluno.setCurso(curso);
        if (!periodo.isEmpty()) aluno.setPeriodo(periodo);
        alunoDAO.atualizar(aluno);
        System.out.println("Atualizado: " + alunoDAO.buscarPorId(id).visualizarPerfil());
    }

    private static void atualizarMonitor() throws SQLException {
        mostrarMonitores();
        int id = lerInt("Id do monitor");
        Monitor monitor = monitorDAO.buscarPorId(id);
        if (monitor == null) throw new IllegalArgumentException("Monitor não encontrado.");
        System.out.println("Atual: " + monitor.visualizarPerfil());
        String nome = lerTexto("Novo nome (Enter mantém)");
        String biografia = lerTexto("Nova biografia (Enter mantém)");
        if (!nome.isEmpty()) monitor.setNome(nome);
        if (!biografia.isEmpty()) monitor.setBiografia(biografia);
        monitorDAO.atualizar(monitor);
        System.out.println("Atualizado: " + monitorDAO.buscarPorId(id).visualizarPerfil());
    }

    private static void excluirAluno() throws SQLException {
        mostrarAlunos();
        int id = lerInt("Id do aluno");
        if (alunoDAO.buscarPorId(id) == null) throw new IllegalArgumentException("Aluno não encontrado.");
        if (!confirmar("Excluir o aluno " + id + "?")) {
            System.out.println("Exclusão cancelada.");
            return;
        }
        alunoDAO.deletar(id);
        System.out.println("Aluno excluído.");
    }

    private static void excluirMonitor() throws SQLException {
        mostrarMonitores();
        int id = lerInt("Id do monitor");
        if (monitorDAO.buscarPorId(id) == null) throw new IllegalArgumentException("Monitor não encontrado.");
        if (!confirmar("Excluir o monitor " + id + " (suas disponibilidades e vínculos também serão removidos)?")) {
            System.out.println("Exclusão cancelada.");
            return;
        }
        monitorDAO.deletar(id);
        System.out.println("Monitor excluído.");
    }

    // ---------------------------------------------------------- demonstração

    private static void carregarDemonstracao() throws SQLException {
        Aluno joao = new Aluno(0, "João Silva", "joao.demo@educonecta.com", "demo123", "Análise e Desenvolvimento de Sistemas", "3º");
        alunoDAO.inserir(joao);
        Monitor carlos = new Monitor(0, "Carlos Souza", "carlos.demo@educonecta.com", "demo123", "Monitor de Banco de Dados");
        monitorDAO.inserir(carlos);
        Monitor ana = new Monitor(0, "Ana Lima", "ana.demo@educonecta.com", "demo123", "Monitora sem matéria cadastrada");
        monitorDAO.inserir(ana);
        Materia bd = new Materia(0, "Banco de Dados");
        materiaDAO.inserir(bd);
        monitorDAO.vincularMateria(carlos.getId(), bd.getId());
        disponibilidadeDAO.inserir(new Disponibilidade(carlos.getId(), 2, LocalTime.of(14, 0), LocalTime.of(16, 0)));

        LocalDateTime terca15h = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY))
                .withHour(15).withMinute(0).withSecond(0).withNano(0);
        System.out.println("Dados de demonstração carregados:");
        System.out.println("  Aluno " + joao.getId() + " (João), monitor " + carlos.getId() + " (Carlos, domina Banco de Dados, "
                + "atende terça das 14h às 16h), monitora " + ana.getId() + " (Ana, sem matéria), matéria " + bd.getId() + " (Banco de Dados).");
        System.out.println("Roteiro sugerido:");
        System.out.println("  - Opção 8 com a Ana: a RN01 recusa (monitora sem matéria).");
        System.out.println("  - Opção 8 com o Carlos em " + terca15h.format(DATA_HORA) + " (terça): fica pendente.");
        System.out.println("  - Opção 12 antes de concluir: a RN03 recusa.");
        System.out.println("  - Opção 9 confirma (cabe na janela de terça); em outro dia da semana a RN02 recusaria.");
        System.out.println("  - Opções 10 e 12: conclui a sessão e registra a avaliação.");
    }

    // -------------------------------------------------------------- listagens

    private static void mostrarAlunos() throws SQLException {
        System.out.println("--- Alunos ---");
        List<Aluno> alunos = alunoDAO.listar();
        if (alunos.isEmpty()) System.out.println("(nenhum)");
        for (Aluno a : alunos) System.out.println("  [" + a.getId() + "] " + a.getNome());
    }

    private static void mostrarMonitores() throws SQLException {
        System.out.println("--- Monitores ---");
        List<Monitor> monitores = monitorDAO.listar();
        if (monitores.isEmpty()) System.out.println("(nenhum)");
        for (Monitor m : monitores) System.out.println("  [" + m.getId() + "] " + m.getNome());
    }

    private static void mostrarMaterias() throws SQLException {
        System.out.println("--- Matérias ---");
        List<Materia> materias = materiaDAO.listar();
        if (materias.isEmpty()) System.out.println("(nenhuma)");
        for (Materia m : materias) System.out.println("  [" + m.getId() + "] " + m.getNome());
    }

    private static void mostrarSessoes() throws SQLException {
        System.out.println("--- Sessões ---");
        List<Sessao> sessoes = sessaoDAO.listar();
        if (sessoes.isEmpty()) System.out.println("(nenhuma)");
        for (Sessao s : sessoes) System.out.println("  " + descrever(s));
    }

    private static String descrever(Sessao s) throws SQLException {
        Aluno aluno = alunoDAO.buscarPorId(s.getIdAluno());
        Monitor monitor = monitorDAO.buscarPorId(s.getIdMonitor());
        Materia materia = materiaDAO.buscarPorId(s.getIdMateria());
        Avaliacao avaliacao = avaliacaoDAO.buscarPorSessao(s.getId());
        return "[" + s.getId() + "] " + (aluno != null ? aluno.getNome() : "?")
                + " com " + (monitor != null ? monitor.getNome() : "?")
                + " | " + (materia != null ? materia.getNome() : "?")
                + " | " + s.getDataHora().format(DATA_HORA)
                + " | " + s.getStatus()
                + (avaliacao != null ? " | nota " + avaliacao.getNota() : "");
    }

    // ---------------------------------------------------------------- apoio

    private static void exigirMonitor(int id) throws SQLException {
        if (monitorDAO.buscarPorId(id) == null) throw new IllegalArgumentException("Monitor não encontrado.");
    }

    private static void exigirMateria(int id) throws SQLException {
        if (materiaDAO.buscarPorId(id) == null) throw new IllegalArgumentException("Matéria não encontrada.");
    }

    private static boolean confirmar(String pergunta) {
        return lerTexto(pergunta + " (S/N)").equalsIgnoreCase("S");
    }

    private static String lerTexto(String rotulo) {
        System.out.print(rotulo + ": ");
        return ENTRADA.nextLine().trim();
    }

    private static int lerInt(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }

    private static void tratarErroBanco(SQLException e) {
        String estado = e.getSQLState();
        if (estado != null && estado.startsWith("23")) {
            System.out.println("Não foi possível concluir a operação: e-mail já cadastrado, vínculo repetido, "
                    + "referência inexistente ou registro em uso por outra tabela (por exemplo, aluno com sessões).");
        } else {
            System.out.println("Erro de banco de dados: " + e.getMessage()
                    + " (confira se o MySQL está ligado e a senha em ConexaoFactory).");
        }
    }
}