package br.com.educonecta.notificacao;


public class NotificacaoEmail implements Notificacao {
    @Override
    public void enviar(String destinatario, String mensagem) {
        System.out.println("===== NOTIFICAÇÃO POR E-MAIL (SIMULAÇÃO) =====");
        System.out.println("Para: " + destinatario);
        System.out.println("Mensagem: " + mensagem);
        System.out.println("==============================================");
    }
}
