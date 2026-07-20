package infotechg.repo;

import infotechg.dao.ConexaoBD;
import infotechg.model.Cliente;
import infotechg.model.Produto;
import infotechg.model.Usuario;
import infotechg.model.Venda;
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

/**
 * Acesso a tabela Vendas via JDBC (Etapa 4). Cliente, Produto e Usuario
 * responsavel sao resolvidos pelos respectivos repositorios ao montar cada
 * linha (dataset pequeno de uso academico/desktop - join manual mantem a
 * SQL simples em vez de um JOIN de 4 tabelas com aliases).
 */
public class VendaRepository {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public VendaRepository(ClienteRepository clienteRepository, ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Venda> findAll() {
        String sql = "SELECT idVenda, data_venda, quantidade_vendida, valor_total_venda, "
                + "Clientes_idCliente, Produtos_idProduto, Usuarios_idUsuario "
                + "FROM Vendas ORDER BY idVenda";
        List<Venda> vendas = new ArrayList<>();
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                vendas.add(mapear(resultado));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao listar vendas", ex);
        }
        return vendas;
    }

    public Optional<Venda> findById(Integer id) {
        String sql = "SELECT idVenda, data_venda, quantidade_vendida, valor_total_venda, "
                + "Clientes_idCliente, Produtos_idProduto, Usuarios_idUsuario "
                + "FROM Vendas WHERE idVenda = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao buscar venda id=" + id, ex);
        }
    }

    public Venda save(Venda venda) {
        return venda.getIdVenda() == 0 ? inserir(venda) : atualizar(venda);
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Vendas WHERE idVenda = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao excluir venda id=" + id, ex);
        }
    }

    /**
     * Registra e confirma uma venda (RN02/RN03): a baixa no estoque do
     * produto acontece antes de o registro ser persistido, e o novo nivel
     * de estoque e gravado no banco na mesma operacao.
     *
     * @throws IllegalStateException se o produto nao tiver estoque suficiente.
     */
    public Venda registrar(int quantidade, Cliente cliente, Produto produto, Usuario vendedorResponsavel) {
        Venda venda = new Venda(0, LocalDateTime.now(), quantidade, cliente, produto, vendedorResponsavel);
        venda.confirmar();
        produtoRepository.save(produto);
        return inserir(venda);
    }

    private Venda inserir(Venda venda) {
        String sql = "INSERT INTO Vendas (data_venda, quantidade_vendida, valor_total_venda, "
                + "Clientes_idCliente, Produtos_idProduto, Usuarios_idUsuario) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(comando, venda);
            comando.executeUpdate();
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    venda.setIdVenda(chaves.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao registrar venda", ex);
        }
        return venda;
    }

    private Venda atualizar(Venda venda) {
        String sql = "UPDATE Vendas SET data_venda = ?, quantidade_vendida = ?, valor_total_venda = ?, "
                + "Clientes_idCliente = ?, Produtos_idProduto = ?, Usuarios_idUsuario = ? WHERE idVenda = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherParametros(comando, venda);
            comando.setInt(7, venda.getIdVenda());
            comando.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao atualizar venda id=" + venda.getIdVenda(), ex);
        }
        return venda;
    }

    private void preencherParametros(PreparedStatement comando, Venda venda) throws SQLException {
        comando.setTimestamp(1, Timestamp.valueOf(venda.getDataVenda()));
        comando.setInt(2, venda.getQuantidadeVendida());
        comando.setBigDecimal(3, venda.getValorTotalVenda());
        comando.setInt(4, venda.getCliente().getIdCliente());
        comando.setInt(5, venda.getProduto().getIdProduto());
        comando.setInt(6, venda.getVendedorResponsavel().getIdUsuario());
    }

    private Venda mapear(ResultSet resultado) throws SQLException {
        Cliente cliente = clienteRepository.findById(resultado.getInt("Clientes_idCliente")).orElse(null);
        Produto produto = produtoRepository.findById(resultado.getInt("Produtos_idProduto")).orElse(null);
        Usuario vendedor = usuarioRepository.findById(resultado.getInt("Usuarios_idUsuario")).orElse(null);

        Venda venda = new Venda(
                resultado.getInt("idVenda"),
                resultado.getTimestamp("data_venda").toLocalDateTime(),
                resultado.getInt("quantidade_vendida"),
                cliente, produto, vendedor);
        // valor_total_venda e um retrato do momento da venda: preserva o valor
        // gravado em vez do recalculo feito pelo construtor com o preco atual.
        venda.setValorTotalVenda(resultado.getBigDecimal("valor_total_venda"));
        return venda;
    }
}
