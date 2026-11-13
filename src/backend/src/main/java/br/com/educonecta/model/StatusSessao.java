package br.com.educonecta.model;

public enum StatusSessao {
    PENDENTE("pendente"),
    CONFIRMADA("confirmada"),
    CONCLUIDA("concluida"),
    CANCELADA("cancelada");

    private final String valor;

    StatusSessao(String valor) { this.valor = valor; }

    public String getValor() { return valor; }

    public static StatusSessao deBanco(String valor) {
        for (StatusSessao s : values()) {
            if (s.valor.equalsIgnoreCase(valor)) return s;
        }
        throw new IllegalArgumentException("Status inválido: " + valor);
    }
}