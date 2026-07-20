package infotechg.repo;

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
