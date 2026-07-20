package infotechg.model;

/**
 * Status possiveis de uma Ordem de Servico (RN05).
 */
public enum StatusOS {
    EM_ANALISE("Em analise"),
    AGUARDANDO_PECA("Aguardando peca"),
    CONCLUIDO("Concluido"),
    ENTREGUE("Entregue");

    private final String descricao;

    StatusOS(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
