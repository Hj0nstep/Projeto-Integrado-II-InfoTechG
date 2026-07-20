package infotechg.model;

import java.math.BigDecimal;

public class Produto {

    private int idProduto;
    private String nome;
    private BigDecimal preco;
    private int quantidadeEstoque;
    private Fornecedor fornecedor;

    public Produto() {
    }

    public Produto(int idProduto, String nome, BigDecimal preco, int quantidadeEstoque, Fornecedor fornecedor) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.fornecedor = fornecedor;
    }

    public boolean temEstoqueDisponivel(int quantidadeDesejada) {
        return quantidadeDesejada > 0 && quantidadeDesejada <= this.quantidadeEstoque;
    }

    public void darBaixaEstoque(int quantidadeVendida) {
        if (!temEstoqueDisponivel(quantidadeVendida)) {
            throw new IllegalStateException(
                    "Estoque insuficiente para \"" + nome + "\": disponivel " + quantidadeEstoque
                    + ", solicitado " + quantidadeVendida);
        }
        this.quantidadeEstoque -= quantidadeVendida;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException("Quantidade em estoque nao pode ser negativa: " + quantidadeEstoque);
        }
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    @Override
    public String toString() {
        return idProduto + " - " + nome + " (estoque: " + quantidadeEstoque + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produto other)) {
            return false;
        }
        return idProduto == other.idProduto;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idProduto);
    }
}
