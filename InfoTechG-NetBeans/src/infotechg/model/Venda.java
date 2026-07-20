package infotechg.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
