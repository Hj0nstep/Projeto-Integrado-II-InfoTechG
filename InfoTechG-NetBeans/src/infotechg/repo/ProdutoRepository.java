package infotechg.repo;

import infotechg.dao.ConexaoBD;
import infotechg.model.Fornecedor;
import infotechg.model.Produto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepository {

    private static final String SELECT_BASE =
            "SELECT p.idProduto, p.nome, p.preco, p.quantidade_estoque, "
            + "f.idFornecedor AS forn_id, f.nome_empresa AS forn_nome, "
            + "f.telefone_contato AS forn_telefone, f.email_contato AS forn_email "
            + "FROM Produtos p JOIN Fornecedores f ON p.Fornecedores_idFornecedor = f.idFornecedor";

    public List<Produto> findAll() {
        String sql = SELECT_BASE + " ORDER BY p.idProduto";
        List<Produto> produtos = new ArrayList<>();
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                produtos.add(mapear(resultado));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao listar produtos", ex);
        }
        return produtos;
    }

    public Optional<Produto> findById(Integer id) {
        String sql = SELECT_BASE + " WHERE p.idProduto = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao buscar produto id=" + id, ex);
        }
    }

    public Produto save(Produto produto) {
        return produto.getIdProduto() == 0 ? inserir(produto) : atualizar(produto);
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Produtos WHERE idProduto = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao excluir produto id=" + id, ex);
        }
    }

    public Produto cadastrar(String nome, BigDecimal preco, int quantidadeEstoque, Fornecedor fornecedor) {
        return inserir(new Produto(0, nome, preco, quantidadeEstoque, fornecedor));
    }

    private Produto inserir(Produto produto) {
        String sql = "INSERT INTO Produtos (nome, preco, quantidade_estoque, Fornecedores_idFornecedor) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            comando.setString(1, produto.getNome());
            comando.setBigDecimal(2, produto.getPreco());
            comando.setInt(3, produto.getQuantidadeEstoque());
            comando.setInt(4, produto.getFornecedor().getIdFornecedor());
            comando.executeUpdate();
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setIdProduto(chaves.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao cadastrar produto", ex);
        }
        return produto;
    }

    private Produto atualizar(Produto produto) {
        String sql = "UPDATE Produtos SET nome = ?, preco = ?, quantidade_estoque = ?, "
                + "Fornecedores_idFornecedor = ? WHERE idProduto = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, produto.getNome());
            comando.setBigDecimal(2, produto.getPreco());
            comando.setInt(3, produto.getQuantidadeEstoque());
            comando.setInt(4, produto.getFornecedor().getIdFornecedor());
            comando.setInt(5, produto.getIdProduto());
            comando.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao atualizar produto id=" + produto.getIdProduto(), ex);
        }
        return produto;
    }

    private Produto mapear(ResultSet resultado) throws SQLException {
        Fornecedor fornecedor = new Fornecedor(
                resultado.getInt("forn_id"),
                resultado.getString("forn_nome"),
                resultado.getString("forn_telefone"),
                resultado.getString("forn_email"));
        return new Produto(
                resultado.getInt("idProduto"),
                resultado.getString("nome"),
                resultado.getBigDecimal("preco"),
                resultado.getInt("quantidade_estoque"),
                fornecedor);
    }
}
