package infotechg.repo;

import infotechg.dao.ConexaoBD;
import infotechg.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    public List<Cliente> findAll() {
        String sql = "SELECT idCliente, nome, telefone, email FROM Clientes ORDER BY idCliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                clientes.add(mapear(resultado));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao listar clientes", ex);
        }
        return clientes;
    }

    public Optional<Cliente> findById(Integer id) {
        String sql = "SELECT idCliente, nome, telefone, email FROM Clientes WHERE idCliente = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao buscar cliente id=" + id, ex);
        }
    }

    public Cliente save(Cliente cliente) {
        return cliente.getIdCliente() == 0 ? inserir(cliente) : atualizar(cliente);
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Clientes WHERE idCliente = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao excluir cliente id=" + id, ex);
        }
    }

    public Cliente cadastrar(String nome, String telefone, String email) {
        return inserir(new Cliente(0, nome, telefone, email));
    }

    private Cliente inserir(Cliente cliente) {
        String sql = "INSERT INTO Clientes (nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            comando.setString(1, cliente.getNome());
            comando.setString(2, cliente.getTelefone());
            comando.setString(3, cliente.getEmail());
            comando.executeUpdate();
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setIdCliente(chaves.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao cadastrar cliente", ex);
        }
        return cliente;
    }

    private Cliente atualizar(Cliente cliente) {
        String sql = "UPDATE Clientes SET nome = ?, telefone = ?, email = ? WHERE idCliente = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, cliente.getNome());
            comando.setString(2, cliente.getTelefone());
            comando.setString(3, cliente.getEmail());
            comando.setInt(4, cliente.getIdCliente());
            comando.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao atualizar cliente id=" + cliente.getIdCliente(), ex);
        }
        return cliente;
    }

    private Cliente mapear(ResultSet resultado) throws SQLException {
        return new Cliente(
                resultado.getInt("idCliente"),
                resultado.getString("nome"),
                resultado.getString("telefone"),
                resultado.getString("email"));
    }
}
