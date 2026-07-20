package infotechg.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Venda de um produto para um cliente (RF005).
 *
 * O construtor apenas monta o registro da venda e calcula o total inicial
 * (com base no preco atual do produto). A baixa efetiva de estoque (RN02/RN03)
 * so acontece quando {@link #confirmar()} e chamado explicitamente - por
 * exemplo, quando o usuario clica em "Salvar" na tela de vendas (Etapa 3/4).
 * Isso evita que simplesmente criar um objeto Venda (ex.: para pre-visualizar
 * o total) tenha o efeito colateral de mexer no estoque.
 *
 * valorTotalVenda e tratado como um retrato (snapshot) do momento da venda,
 * assim como no banco de dados: alterar quantidadeVendida ou produto depois
 * de criada a venda NAO recalcula o total automaticamente.
 */
public class Venda {

    private int idVenda;
    private LocalDateTime dataVenda;
    private int quantidadeVendida;
    private BigDecimal valorTotalVenda;
    private Cliente cliente;
    private Produto produto;
    private Usuario vendedorResponsavel;

    public Venda() {
    }

    public Venda(int idVenda, LocalDateTime dataVenda, int quantidadeVendida,
                  Cliente cliente, Produto produto, Usuario vendedorResponsavel) {
        this.idVenda = idVenda;
        this.dataVenda = dataVenda;
        this.quantidadeVendida = quantidadeVendida;
        this.cliente = cliente;
        this.produto = produto;
        this.vendedorResponsavel = vendedorResponsavel;
        this.valorTotalVenda = calcularTotal();
    }

    public BigDecimal calcularTotal() {
        if (produto == null || produto.getPreco() == null) {
            return BigDecimal.ZERO;
        }
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidadeVendida));
    }

    /**
     * Efetiva a venda: valida (RN02) e da baixa no estoque do produto (RN03).
     * Deve ser chamado uma unica vez, no momento em que a venda e confirmada
     * pelo usuario (nunca automaticamente no construtor).
     *
     * @throws IllegalStateException se nao houver estoque suficiente do produto.
     */
    public void confirmar() {
        if (produto == null) {
            throw new IllegalStateException("Venda sem produto associado.");
        }
        produto.darBaixaEstoque(quantidadeVendida);
        this.valorTotalVenda = calcularTotal();
    }

    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public int getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(int quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }

    public BigDecimal getValorTotalVenda() {
        return valorTotalVenda;
    }

    public void setValorTotalVenda(BigDecimal valorTotalVenda) {
        this.valorTotalVenda = valorTotalVenda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Usuario getVendedorResponsavel() {
        return vendedorResponsavel;
    }

    public void setVendedorResponsavel(Usuario vendedorResponsavel) {
        this.vendedorResponsavel = vendedorResponsavel;
    }

    @Override
    public String toString() {
        return "Venda #" + idVenda + " - " + produto + " x" + quantidadeVendida + " -> " + cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venda other)) {
            return false;
        }
        return idVenda == other.idVenda;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idVenda);
    }
}
