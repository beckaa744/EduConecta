package br.com.educonecta;

import br.com.educonecta.notificacao.Notificacao;
import br.com.educonecta.notificacao.NotificacaoEmail;

public class App {
    public static void main(String[] args) {
        Notificacao notificacao = new NotificacaoEmail();
        notificacao.enviar("aluno@educonecta.com",
                "Sua solicitação de monitoria foi registrada.");
        System.out.println("EduConecta iniciado. Configure o MySQL para utilizar os DAOs.");
    }
}
