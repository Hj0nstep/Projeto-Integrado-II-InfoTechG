package infotechg.view;

import infotechg.model.Fornecedor;
import infotechg.model.Produto;
import infotechg.repo.Repositorios;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ProdutoPanel extends JPanel implements Atualizavel {

    private final Repositorios repositorios;

    private final JTextField campoNome = new JTextField();
    private final JTextField campoPreco = new JTextField();
    private final JTextField campoEstoque = new JTextField();
    private final JComboBox<Fornecedor> comboFornecedor = new JComboBox<>();

    private final DefaultTableModel modeloTabela =
            Componentes.modeloTabelaSomenteLeitura("ID", "Nome", "Preco", "Estoque", "Fornecedor");
    private final JTable tabela = new JTable(modeloTabela);

    private Integer idSelecionado;

    public ProdutoPanel(Repositorios repositorios) {
        this.repositorios = repositorios;
        montarTela();
    }

    private void montarTela() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UiTheme.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(Componentes.tituloPainel("Produtos"), BorderLayout.NORTH);
        add(criarFormulario(), BorderLayout.WEST);
        add(criarTabela(), BorderLayout.CENTER);
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        formulario.setPreferredSize(new java.awt.Dimension(280, 0));

        formulario.add(Componentes.campoComRotulo("Nome", campoNome));
        formulario.add(Componentes.campoComRotulo("Preco (R$)", campoPreco));
        formulario.add(Componentes.campoComRotulo("Quantidade em estoque", campoEstoque));
        formulario.add(Componentes.campoComRotulo("Fornecedor", comboFornecedor));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 12));
        botoes.setOpaque(false);
        var botaoSalvar = Componentes.botaoPrimario("Salvar");
        var botaoEditar = Componentes.botao("Editar");
        var botaoExcluir = Componentes.botao("Excluir");
        var botaoLimpar = Componentes.botao("Limpar");
        botoes.add(botaoSalvar);
        botoes.add(botaoEditar);
        botoes.add(botaoExcluir);
        botoes.add(botaoLimpar);
        formulario.add(botoes);

        botaoSalvar.addActionListener(e -> salvar());
        botaoEditar.addActionListener(e -> editar());
        botaoExcluir.addActionListener(e -> excluir());
        botaoLimpar.addActionListener(e -> limparFormulario());

        return formulario;
    }

    private JScrollPane criarTabela() {
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new RendererEstoque());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecao();
            }
        });
        return new JScrollPane(tabela);
    }

    private void salvar() {
        Fornecedor fornecedor = (Fornecedor) comboFornecedor.getSelectedItem();
        if (fornecedor == null) {
            JOptionPane.showMessageDialog(this, "Cadastre um fornecedor antes de cadastrar produtos.",
                    "Dados invalidos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FormularioProduto dados = lerFormulario();
        if (dados == null) {
            return;
        }
        repositorios.produtos.cadastrar(dados.nome, dados.preco, dados.estoque, fornecedor);
        limparFormulario();
        atualizar();
    }

    private void editar() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para editar.");
            return;
        }
        Fornecedor fornecedor = (Fornecedor) comboFornecedor.getSelectedItem();
        if (fornecedor == null) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor.", "Dados invalidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        FormularioProduto dados = lerFormulario();
        if (dados == null) {
            return;
        }
        Produto produto = new Produto(idSelecionado, dados.nome, dados.preco, dados.estoque, fornecedor);
        repositorios.produtos.save(produto);
        limparFormulario();
        atualizar();
    }

    private void excluir() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para excluir.");
            return;
        }
        try {
            repositorios.produtos.deleteById(idSelecionado);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    "Nao e possivel excluir: existem vendas vinculadas a este produto.",
                    "Exclusao bloqueada", JOptionPane.ERROR_MESSAGE);
            return;
        }
        limparFormulario();
        atualizar();
    }

    private FormularioProduto lerFormulario() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do produto.", "Dados invalidos",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        BigDecimal preco;
        try {
            preco = new BigDecimal(campoPreco.getText().trim().replace(",", "."));
            if (preco.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException("preco negativo");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um preco valido (ex.: 199.90).", "Dados invalidos",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int estoque;
        try {
            estoque = Integer.parseInt(campoEstoque.getText().trim());
            if (estoque < 0) {
                throw new NumberFormatException("estoque negativo");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe uma quantidade de estoque valida (numero inteiro >= 0).",
                    "Dados invalidos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new FormularioProduto(nome, preco, estoque);
    }

    private void carregarSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = (Integer) modeloTabela.getValueAt(linha, 0);
        campoNome.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));

        Produto produto = repositorios.produtos.findById(idSelecionado).orElse(null);
        if (produto == null) {
            return;
        }
        campoPreco.setText(produto.getPreco().toPlainString());
        campoEstoque.setText(String.valueOf(produto.getQuantidadeEstoque()));
        comboFornecedor.setSelectedItem(produto.getFornecedor());
    }

    private void limparFormulario() {
        idSelecionado = null;
        campoNome.setText("");
        campoPreco.setText("");
        campoEstoque.setText("");
        if (comboFornecedor.getItemCount() > 0) {
            comboFornecedor.setSelectedIndex(0);
        }
        tabela.clearSelection();
    }

    @Override
    public void atualizar() {
        comboFornecedor.removeAllItems();
        for (Fornecedor fornecedor : repositorios.fornecedores.findAll()) {
            comboFornecedor.addItem(fornecedor);
        }

        modeloTabela.setRowCount(0);
        for (Produto produto : repositorios.produtos.findAll()) {
            modeloTabela.addRow(new Object[] {
                produto.getIdProduto(), produto.getNome(), produto.getPreco(),
                produto.getQuantidadeEstoque(), produto.getFornecedor()
            });
        }
    }

    private record FormularioProduto(String nome, BigDecimal preco, int estoque) {
    }

    private static class RendererEstoque extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component componente = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
            int quantidade = (value instanceof Integer inteiro) ? inteiro : 0;
            if (quantidade == 0) {
                setText("0 (sem estoque)");
                setForeground(isSelected ? UiTheme.BRANCO : UiTheme.ERRO);
            } else {
                setForeground(isSelected ? UiTheme.BRANCO : UiTheme.SUCESSO);
            }
            return componente;
        }
    }
}
