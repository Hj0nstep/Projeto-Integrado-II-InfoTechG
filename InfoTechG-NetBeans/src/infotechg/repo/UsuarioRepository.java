package infotechg.repo;

import infotechg.dao.ConexaoBD;
import infotechg.model.Gerente;
import infotechg.model.Tecnico;
import infotechg.model.Usuario;
import infotechg.model.Vendedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioRepository {

    private static final String SELECT_BASE =
            "SELECT idUsuario, login, senha_hash, nome_completo, tipo_perfil FROM Usuarios";

    public Optional<Usuario> findById(Integer id) {
        String sql = SELECT_BASE + " WHERE idUsuario = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao buscar usuario id=" + id, ex);
        }
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        String sql = SELECT_BASE + " WHERE login = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, login);
            try (ResultSet resultado = comando.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }
                Usuario usuario = mapear(resultado);
                return usuario.autenticar(senha) ? Optional.of(usuario) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao autenticar usuario login=" + login, ex);
        }
    }

    private Usuario mapear(ResultSet resultado) throws SQLException {
        int id = resultado.getInt("idUsuario");
        String login = resultado.getString("login");
        String senhaHash = resultado.getString("senha_hash");
        String nomeCompleto = resultado.getString("nome_completo");
        String tipoPerfil = resultado.getString("tipo_perfil");
        return switch (tipoPerfil) {
            case "Gerente" -> new Gerente(id, login, senhaHash, nomeCompleto);
            case "Vendedor" -> new Vendedor(id, login, senhaHash, nomeCompleto);
            case "Tecnico" -> new Tecnico(id, login, senhaHash, nomeCompleto);
            default -> throw new IllegalStateException("Tipo de perfil desconhecido: " + tipoPerfil);
        };
    }
}
