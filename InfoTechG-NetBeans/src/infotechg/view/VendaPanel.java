package infotechg.view;

import infotechg.model.Cliente;
import infotechg.model.Produto;
import infotechg.model.Usuario;
import infotechg.repo.Repositorios;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Registro de vendas (wireframe 06 / RF005). Monta um carrinho local antes
 * de confirmar - so ao clicar em "Finalizar Venda" cada item vira uma
 * {@link infotechg.model.Venda} persistida (RN02/RN03 aplicadas por item).
 */
public class VendaPanel extends JPanel implements Atualizavel {

    private final Repositorios repositorios;
    private final Usuario usuarioLogado;

    private final JComboBox<Cliente> comboCliente = new JComboBox<>();
    private final JComboBox<Produto> comboProduto = new JComboBox<>();
    private final JSpinner spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JLabel labelTotal = new JLabel("Total: R$ 0,00");
    private final JLabel labelAlerta = new JLabel(" ");

    private final DefaultTableModel modeloCarrinho =
            Componentes.modeloTabelaSomenteLeitura("Produto", "Quantidade", "Subtotal");
    private final JTable tabelaCarrinho = new JTable(modeloCarrinho);

    private final List<ItemCarrinho> carrinho = new ArrayList<>();

    public VendaPanel(Repositorios repositorios, Usuario usuarioLogado) {
        this.repositorios = repositorios;
        this.usuarioLogado = usuarioLogado;
        montarTela();
    }

    private void montarTela() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UiTheme.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(Componentes.tituloPainel("Vendas"), BorderLayout.NORTH);
        add(criarFormulario(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        labelTotal.setFont(UiTheme.FONTE_SUBTITULO);
        labelTotal.setForeground(UiTheme.PRIMARIO_ESCURO);
        rodape.add(labelTotal, BorderLayout.WEST);

        JPanel botoesFinais = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoesFinais.setOpaque(false);
        var botaoCancelar = Componentes.botao("Cancelar");
        var botaoFinalizar = Componentes.botaoPrimario("Finalizar Venda");
        botaoCancelar.addActionListener(e -> cancelarVenda());
        botaoFinalizar.addActionListener(e -> finalizarVenda());
        botoesFinais.add(botaoCancelar);
        botoesFinais.add(botaoFinalizar);
        rodape.add(botoesFinais, BorderLayout.EAST);

        centro.add(rodape, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        formulario.setPreferredSize(new java.awt.Dimension(280, 0));

        formulario.add(Componentes.campoComRotulo("Cliente", comboCliente));
        formulario.add(Componentes.campoComRotulo("Produto", comboProduto));
        formulario.add(Componentes.campoComRotulo("Quantidade", spinnerQuantidade));

        labelAlerta.setForeground(UiTheme.ERRO);
        labelAlerta.setFont(UiTheme.FONTE_PADRAO);
        formulario.add(labelAlerta);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 12));
        botoes.setOpaque(false);
        var botaoAdicionar = Componentes.botao("Adicionar item");
        botaoAdicionar.addActionListener(e -> adicionarItem());
        botoes.add(botaoAdicionar);
        formulario.add(botoes);

        return formulario;
    }

    private void adicionarItem() {
        labelAlerta.setText(" ");
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        Produto produto = (Produto) comboProduto.getSelectedItem();
        int quantidade = (Integer) spinnerQuantidade.getValue();

        if (cliente == null || produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione cliente e produto.", "Dados invalidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservado = carrinho.stream()
                .filter(item -> item.produto().equals(produto))
                .mapToInt(ItemCarrinho::quantidade)
                .sum();

        if (!produto.temEstoqueDisponivel(reservado + quantidade)) {
            labelAlerta.setText("! Estoque insuficiente para \"" + produto.getNome() + "\" - venda bloqueada");
            return;
        }

        carrinho.add(new ItemCarrinho(produto, quantidade));
        atualizarTabelaCarrinho();
    }

    private void finalizarVenda() {
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione ao menos um item ao carrinho.");
            return;
        }
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione o cliente da venda.");
            return;
        }

        try {
            for (ItemCarrinho item : carrinho) {
                repositorios.vendas.registrar(item.quantidade(), cliente, item.produto(), usuarioLogado);
            }
            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!");
            cancelarVenda();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, "Nao foi possivel concluir a venda: " + ex.getMessage(),
                    "Estoque insuficiente", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarVenda() {
        carrinho.clear();
        labelAlerta.setText(" ");
        atualizarTabelaCarrinho();
    }

    private void atualizarTabelaCarrinho() {
        modeloCarrinho.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrinho item : carrinho) {
            modeloCarrinho.addRow(new Object[] { item.produto().getNome(), item.quantidade(), item.subtotal() });
            total = total.add(item.subtotal());
        }
        labelTotal.setText("Total: R$ " + total.toPlainString());
        atualizar();
    }

    @Override
    public void atualizar() {
        Cliente clienteSelecionado = (Cliente) comboCliente.getSelectedItem();
        comboCliente.removeAllItems();
        for (Cliente cliente : repositorios.clientes.findAll()) {
            comboCliente.addItem(cliente);
        }
        if (clienteSelecionado != null) {
            comboCliente.setSelectedItem(clienteSelecionado);
        }

        Produto produtoSelecionado = (Produto) comboProduto.getSelectedItem();
        comboProduto.removeAllItems();
        for (Produto produto : repositorios.produtos.findAll()) {
            comboProduto.addItem(produto);
        }
        if (produtoSelecionado != null) {
            comboProduto.setSelectedItem(produtoSelecionado);
        }
    }

    private record ItemCarrinho(Produto produto, int quantidade) {
        BigDecimal subtotal() {
            return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
        }
    }
}
