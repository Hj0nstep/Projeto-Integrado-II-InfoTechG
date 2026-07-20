package infotechg.repo;

/**
 * Ponto unico de acesso aos repositorios, todos JDBC-backed (Etapa 4),
 * resolvendo a ordem de dependencia entre eles (ex.: Vendas precisa de
 * Clientes/Produtos/Usuarios para reconstruir os objetos aninhados).
 */
public class Repositorios {

    public final UsuarioRepository usuarios = new UsuarioRepository();
    public final ClienteRepository clientes = new ClienteRepository();
    public final FornecedorRepository fornecedores = new FornecedorRepository();
    public final ProdutoRepository produtos = new ProdutoRepository();
    public final VendaRepository vendas;
    public final OrdemServicoRepository ordensServico;

    public Repositorios() {
        this.vendas = new VendaRepository(clientes, produtos, usuarios);
        this.ordensServico = new OrdemServicoRepository(clientes, usuarios);
    }
}
