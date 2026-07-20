package infotechg.repo;

import infotechg.dao.ConexaoBD;
import infotechg.model.Fornecedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FornecedorRepository {

    public List<Fornecedor> findAll() {
        String sql = "SELECT idFornecedor, nome_empresa, telefone_contato, email_contato "
                + "FROM Fornecedores ORDER BY idFornecedor";
        List<Fornecedor> fornecedores = new ArrayList<>();
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                fornecedores.add(mapear(resultado));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao listar fornecedores", ex);
        }
        return fornecedores;
    }

    public Optional<Fornecedor> findById(Integer id) {
        String sql = "SELECT idFornecedor, nome_empresa, telefone_contato, email_contato "
                + "FROM Fornecedores WHERE idFornecedor = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao buscar fornecedor id=" + id, ex);
        }
    }

    public Fornecedor save(Fornecedor fornecedor) {
        return fornecedor.getIdFornecedor() == 0 ? inserir(fornecedor) : atualizar(fornecedor);
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Fornecedores WHERE idFornecedor = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao excluir fornecedor id=" + id, ex);
        }
    }

    public Fornecedor cadastrar(String nomeEmpresa, String telefoneContato, String emailContato) {
        return inserir(new Fornecedor(0, nomeEmpresa, telefoneContato, emailContato));
    }

    private Fornecedor inserir(Fornecedor fornecedor) {
        String sql = "INSERT INTO Fornecedores (nome_empresa, telefone_contato, email_contato) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            comando.setString(1, fornecedor.getNomeEmpresa());
            comando.setString(2, fornecedor.getTelefoneContato());
            comando.setString(3, fornecedor.getEmailContato());
            comando.executeUpdate();
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    fornecedor.setIdFornecedor(chaves.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao cadastrar fornecedor", ex);
        }
        return fornecedor;
    }

    private Fornecedor atualizar(Fornecedor fornecedor) {
        String sql = "UPDATE Fornecedores SET nome_empresa = ?, telefone_contato = ?, email_contato = ? "
                + "WHERE idFornecedor = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, fornecedor.getNomeEmpresa());
            comando.setString(2, fornecedor.getTelefoneContato());
            comando.setString(3, fornecedor.getEmailContato());
            comando.setInt(4, fornecedor.getIdFornecedor());
            comando.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao atualizar fornecedor id=" + fornecedor.getIdFornecedor(), ex);
        }
        return fornecedor;
    }

    private Fornecedor mapear(ResultSet resultado) throws SQLException {
        return new Fornecedor(
                resultado.getInt("idFornecedor"),
                resultado.getString("nome_empresa"),
                resultado.getString("telefone_contato"),
                resultado.getString("email_contato"));
    }
}
