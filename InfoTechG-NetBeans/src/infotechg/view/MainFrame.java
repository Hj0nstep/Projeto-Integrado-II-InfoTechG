package infotechg.view;

import infotechg.model.Modulos;
import infotechg.model.Usuario;
import infotechg.repo.Repositorios;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {

    private final Repositorios repositorios;
    private final Usuario usuarioLogado;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelConteudo = new JPanel(cardLayout);
    private final Map<String, Atualizavel> paineisAtualizaveis = new LinkedHashMap<>();

    public MainFrame(Repositorios repositorios, Usuario usuarioLogado) {
        super("InfoTechG");
        this.repositorios = repositorios;
        this.usuarioLogado = usuarioLogado;
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarMenuLateral(), BorderLayout.WEST);

        painelConteudo.setBackground(UiTheme.FUNDO);
        registrarPaineis();
        add(painelConteudo, BorderLayout.CENTER);

        mostrarCartao("Dashboard");
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(UiTheme.PRIMARIO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel titulo = new JLabel("InfoTechG");
        titulo.setFont(UiTheme.FONTE_TITULO);
        titulo.setForeground(UiTheme.BRANCO);
        cabecalho.add(titulo, BorderLayout.WEST);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        direita.setOpaque(false);
        JLabel usuario = new JLabel(usuarioLogado.toString());
        usuario.setForeground(UiTheme.BRANCO);
        usuario.setFont(UiTheme.FONTE_PADRAO);
        JButton botaoSair = new JButton("Sair");
        botaoSair.setFocusPainted(false);
        botaoSair.addActionListener(e -> sair());
        direita.add(usuario);
        direita.add(botaoSair);
        cabecalho.add(direita, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(UiTheme.PRIMARIO_ESCURO);
        menu.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        menu.setPreferredSize(new Dimension(190, 0));

        menu.add(criarBotaoMenu("Dashboard", null));
        if (usuarioLogado.podeAcessar(Modulos.CLIENTES)) {
            menu.add(criarBotaoMenu("Clientes", Modulos.CLIENTES));
        }
        if (usuarioLogado.podeAcessar(Modulos.PRODUTOS)) {
            menu.add(criarBotaoMenu("Produtos", Modulos.PRODUTOS));
        }
        if (usuarioLogado.podeAcessar(Modulos.FORNECEDORES)) {
            menu.add(criarBotaoMenu("Fornecedores", Modulos.FORNECEDORES));
        }
        if (usuarioLogado.podeAcessar(Modulos.VENDAS)) {
            menu.add(criarBotaoMenu("Vendas", Modulos.VENDAS));
        }
        if (usuarioLogado.podeAcessar(Modulos.ORDENS_SERVICO)) {
            menu.add(criarBotaoMenu("Ordens de Servico", Modulos.ORDENS_SERVICO));
        }

        return menu;
    }

    private JButton criarBotaoMenu(String nomeCartao, String moduloRequerido) {
        JButton botao = new JButton(nomeCartao);
        botao.setAlignmentX(JButton.LEFT_ALIGNMENT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setForeground(UiTheme.BRANCO);
        botao.setBackground(UiTheme.PRIMARIO_ESCURO);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.addActionListener(e -> mostrarCartao(nomeCartao));
        return botao;
    }

    private void registrarPaineis() {
        adicionarCartao("Dashboard", new DashboardPanel(repositorios, usuarioLogado));
        if (usuarioLogado.podeAcessar(Modulos.CLIENTES)) {
            adicionarCartao("Clientes", new ClientePanel(repositorios));
        }
        if (usuarioLogado.podeAcessar(Modulos.PRODUTOS)) {
            adicionarCartao("Produtos", new ProdutoPanel(repositorios));
        }
        if (usuarioLogado.podeAcessar(Modulos.FORNECEDORES)) {
            adicionarCartao("Fornecedores", new FornecedorPanel(repositorios));
        }
        if (usuarioLogado.podeAcessar(Modulos.VENDAS)) {
            adicionarCartao("Vendas", new VendaPanel(repositorios, usuarioLogado));
        }
        if (usuarioLogado.podeAcessar(Modulos.ORDENS_SERVICO)) {
            adicionarCartao("Ordens de Servico", new OrdemServicoPanel(repositorios, usuarioLogado));
        }
    }

    private void adicionarCartao(String nomeCartao, JPanel painel) {
        painelConteudo.add(painel, nomeCartao);
        if (painel instanceof Atualizavel atualizavel) {
            paineisAtualizaveis.put(nomeCartao, atualizavel);
        }
    }

    private void mostrarCartao(String nomeCartao) {
        Atualizavel atualizavel = paineisAtualizaveis.get(nomeCartao);
        if (atualizavel != null) {
            atualizavel.atualizar();
        }
        cardLayout.show(painelConteudo, nomeCartao);
    }

    private void sair() {
        dispose();
        new LoginFrame(repositorios).setVisible(true);
    }
}
