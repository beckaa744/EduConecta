package br.com.educonecta.service;

import br.com.educonecta.dao.AvaliacaoDAO;
import br.com.educonecta.model.Avaliacao;
import br.com.educonecta.model.Sessao;
import java.sql.SQLException;

/**
 * Contém a regra RN03: só é permitido avaliar uma sessão concluída.
 */
public class AvaliacaoService {
    private final AvaliacaoDAO avaliacaoDAO;

    public AvaliacaoService(AvaliacaoDAO avaliacaoDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
    }

    public Avaliacao avaliar(Sessao sessao, int nota, String comentario) throws SQLException {
        if (sessao == null) throw new IllegalArgumentException("Sessão não encontrada.");
        if (!sessao.estaConcluida())
            throw new IllegalStateException("A avaliação só pode ser feita após a conclusão da sessão.");
        if (nota < 1 || nota > 5)
            throw new IllegalArgumentException("A nota deve estar entre 1 e 5.");
        if (sessao.getId() <= 0)
            throw new IllegalArgumentException("A sessão precisa ter um ID válido.");

        Avaliacao avaliacao = new Avaliacao(sessao.getId(), nota, comentario);
        avaliacaoDAO.inserir(avaliacao);
        return avaliacao;
    }
}
