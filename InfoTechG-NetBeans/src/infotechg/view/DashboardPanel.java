package infotechg.view;

import infotechg.model.StatusOS;
import infotechg.model.Usuario;
import infotechg.repo.Repositorios;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Painel inicial (wireframe 02) com indicadores rapidos do sistema.
 */
public class DashboardPanel extends JPanel implements Atualizavel {

    private final Repositorios repositorios;
    private final Usuario usuarioLogado;

    private final JLabel valorClientes = new JLabel("0");
    private final JLabel valorProdutos = new JLabel("0");
    private final JLabel valorVendasHoje = new JLabel("0");
    private final JLabel valorOsAndamento = new JLabel("0");

    public DashboardPanel(Repositorios repositorios, Usuario usuarioLogado) {
        this.repositorios = repositorios;
        this.usuarioLogado = usuarioLogado;
        montarTela();
    }

    private void montarTela() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel boasVindas = new JLabel(
                "Bem-vindo(a), " + usuarioLogado.getNomeCompleto() + " (" + usuarioLogado.getTipoPerfil() + ")");
        boasVindas.setFont(UiTheme.FONTE_TITULO);
        boasVindas.setForeground(UiTheme.PRIMARIO_ESCURO);
        boasVindas.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(boasVindas, BorderLayout.NORTH);

        JPanel cartoes = new JPanel(new GridLayout(1, 4, 16, 0));
        cartoes.setOpaque(false);
        cartoes.add(criarCartao("Clientes cadastrados", valorClientes));
        cartoes.add(criarCartao("Produtos em estoque", valorProdutos));
        cartoes.add(criarCartao("Vendas hoje", valorVendasHoje));
        cartoes.add(criarCartao("OS em andamento", valorOsAndamento));
        add(cartoes, BorderLayout.CENTER);
    }

    private JPanel criarCartao(String rotulo, JLabel valor) {
        JPanel cartao = new JPanel(new BorderLayout(4, 8));
        cartao.setBackground(UiTheme.BRANCO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.TEXTO_SECUNDARIO, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel tituloLabel = new JLabel(rotulo);
        tituloLabel.setFont(UiTheme.FONTE_PADRAO);
        tituloLabel.setForeground(UiTheme.TEXTO_SECUNDARIO);

        valor.setFont(UiTheme.FONTE_TITULO.deriveFont(28f));
        valor.setForeground(UiTheme.PRIMARIO);
        valor.setHorizontalAlignment(SwingConstants.LEFT);

        cartao.add(tituloLabel, BorderLayout.NORTH);
        cartao.add(valor, BorderLayout.CENTER);
        return cartao;
    }

    @Override
    public void atualizar() {
        valorClientes.setText(String.valueOf(repositorios.clientes.findAll().size()));
        valorProdutos.setText(String.valueOf(repositorios.produtos.findAll().size()));

        long vendasHoje = repositorios.vendas.findAll().stream()
                .filter(v -> v.getDataVenda() != null && v.getDataVenda().toLocalDate().equals(LocalDate.now()))
                .count();
        valorVendasHoje.setText(String.valueOf(vendasHoje));

        long osAndamento = repositorios.ordensServico.findAll().stream()
                .filter(os -> os.getStatus() != StatusOS.ENTREGUE)
                .count();
        valorOsAndamento.setText(String.valueOf(osAndamento));
    }
}
