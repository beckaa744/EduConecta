package br.com.educonecta.service;

import br.com.educonecta.dao.AvaliacaoDAO;
import br.com.educonecta.dao.SessaoDAO;
import br.com.educonecta.model.Avaliacao;
import br.com.educonecta.model.Sessao;

import java.sql.SQLException;

/** Regra de negócio RN03: só é possível avaliar uma sessão concluída. */
public class AvaliacaoService {
    private final AvaliacaoDAO avaliacaoDAO;
    private final SessaoDAO sessaoDAO;

    public AvaliacaoService() {
        this(new AvaliacaoDAO(), new SessaoDAO());
    }

    public AvaliacaoService(AvaliacaoDAO avaliacaoDAO, SessaoDAO sessaoDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
        this.sessaoDAO = sessaoDAO;
    }

    /** RN03 pura: não acessa o banco, então pode ser testada isoladamente. */
    public static void validar(Sessao sessao, int nota) {
        if (sessao == null)
            throw new IllegalArgumentException("Sessão não encontrada.");
        if (!sessao.estaConcluida())
            throw new IllegalStateException("RN03: a avaliação só pode ser feita após a conclusão da sessão.");
        if (nota < 1 || nota > 5)
            throw new IllegalArgumentException("A nota deve estar entre 1 e 5.");
    }

    public Avaliacao avaliar(int idSessao, int nota, String comentario) throws SQLException {
        return avaliar(sessaoDAO.buscarPorId(idSessao), nota, comentario);
    }

    public Avaliacao avaliar(Sessao sessao, int nota, String comentario) throws SQLException {
        validar(sessao, nota);

        if (avaliacaoDAO.buscarPorSessao(sessao.getId()) != null)
            throw new IllegalStateException("Esta sessão já foi avaliada.");

        Avaliacao avaliacao = new Avaliacao(sessao.getId(), nota, comentario);
        avaliacaoDAO.inserir(avaliacao);
        return avaliacao;
    }
}
