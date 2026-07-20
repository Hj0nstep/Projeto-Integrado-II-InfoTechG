package infotechg.repo;

import infotechg.dao.ConexaoBD;
import infotechg.model.Cliente;
import infotechg.model.OrdemServico;
import infotechg.model.StatusOS;
import infotechg.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdemServicoRepository {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public OrdemServicoRepository(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<OrdemServico> findAll() {
        String sql = "SELECT idOS, defeito_relatado, status_servico, data_abertura, "
                + "Clientes_idCliente, Usuarios_idUsuario "
                + "FROM Ordens_Servico ORDER BY idOS";
        List<OrdemServico> ordens = new ArrayList<>();
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                ordens.add(mapear(resultado));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao listar ordens de servico", ex);
        }
        return ordens;
    }

    public Optional<OrdemServico> findById(Integer id) {
        String sql = "SELECT idOS, defeito_relatado, status_servico, data_abertura, "
                + "Clientes_idCliente, Usuarios_idUsuario "
                + "FROM Ordens_Servico WHERE idOS = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao buscar ordem de servico id=" + id, ex);
        }
    }

    public OrdemServico save(OrdemServico ordemServico) {
        return ordemServico.getIdOS() == 0 ? inserir(ordemServico) : atualizar(ordemServico);
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Ordens_Servico WHERE idOS = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao excluir ordem de servico id=" + id, ex);
        }
    }

    public OrdemServico abrir(String defeitoRelatado, Cliente cliente, Usuario tecnicoResponsavel) {
        OrdemServico ordemServico = new OrdemServico(0, LocalDateTime.now(), defeitoRelatado,
                cliente, tecnicoResponsavel);
        return inserir(ordemServico);
    }

    private OrdemServico inserir(OrdemServico ordemServico) {
        String sql = "INSERT INTO Ordens_Servico (defeito_relatado, status_servico, data_abertura, "
                + "Clientes_idCliente, Usuarios_idUsuario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(comando, ordemServico);
            comando.executeUpdate();
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    ordemServico.setIdOS(chaves.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao abrir ordem de servico", ex);
        }
        return ordemServico;
    }

    private OrdemServico atualizar(OrdemServico ordemServico) {
        String sql = "UPDATE Ordens_Servico SET defeito_relatado = ?, status_servico = ?, data_abertura = ?, "
                + "Clientes_idCliente = ?, Usuarios_idUsuario = ? WHERE idOS = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherParametros(comando, ordemServico);
            comando.setInt(6, ordemServico.getIdOS());
            comando.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao atualizar ordem de servico id="
                    + ordemServico.getIdOS(), ex);
        }
        return ordemServico;
    }

    private void preencherParametros(PreparedStatement comando, OrdemServico ordemServico) throws SQLException {
        comando.setString(1, ordemServico.getDefeitoRelatado());
        comando.setString(2, ordemServico.getStatus().name());
        comando.setTimestamp(3, Timestamp.valueOf(ordemServico.getDataAbertura()));
        comando.setInt(4, ordemServico.getCliente().getIdCliente());
        comando.setInt(5, ordemServico.getTecnicoResponsavel().getIdUsuario());
    }

    private OrdemServico mapear(ResultSet resultado) throws SQLException {
        Cliente cliente = clienteRepository.findById(resultado.getInt("Clientes_idCliente")).orElse(null);
        Usuario tecnico = usuarioRepository.findById(resultado.getInt("Usuarios_idUsuario")).orElse(null);

        OrdemServico ordemServico = new OrdemServico(
                resultado.getInt("idOS"),
                resultado.getTimestamp("data_abertura").toLocalDateTime(),
                resultado.getString("defeito_relatado"),
                cliente, tecnico);
        ordemServico.atualizarStatus(StatusOS.valueOf(resultado.getString("status_servico")));
        return ordemServico;
    }
}
