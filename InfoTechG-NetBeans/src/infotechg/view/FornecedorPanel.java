package infotechg.view;

import infotechg.model.Fornecedor;
import infotechg.repo.Repositorios;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class FornecedorPanel extends JPanel implements Atualizavel {

    private final Repositorios repositorios;

    private final JTextField campoNomeEmpresa = new JTextField();
    private final JTextField campoTelefone = new JTextField();
    private final JTextField campoEmail = new JTextField();

    private final DefaultTableModel modeloTabela =
            Componentes.modeloTabelaSomenteLeitura("ID", "Empresa", "Telefone", "E-mail");
    private final JTable tabela = new JTable(modeloTabela);

    private Integer idSelecionado;

    public FornecedorPanel(Repositorios repositorios) {
        this.repositorios = repositorios;
        montarTela();
    }

    private void montarTela() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UiTheme.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(Componentes.tituloPainel("Fornecedores"), BorderLayout.NORTH);
        add(criarFormulario(), BorderLayout.WEST);
        add(criarTabela(), BorderLayout.CENTER);
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        formulario.setPreferredSize(new java.awt.Dimension(280, 0));

        formulario.add(Componentes.campoComRotulo("Nome da empresa", campoNomeEmpresa));
        formulario.add(Componentes.campoComRotulo("Telefone", campoTelefone));
        formulario.add(Componentes.campoComRotulo("E-mail", campoEmail));

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
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecao();
            }
        });
        return new JScrollPane(tabela);
    }

    private void salvar() {
        if (!validarFormulario()) {
            return;
        }
        repositorios.fornecedores.cadastrar(campoNomeEmpresa.getText().trim(), campoTelefone.getText().trim(),
                campoEmail.getText().trim());
        limparFormulario();
        atualizar();
    }

    private void editar() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para editar.");
            return;
        }
        if (!validarFormulario()) {
            return;
        }
        Fornecedor fornecedor = new Fornecedor(idSelecionado, campoNomeEmpresa.getText().trim(),
                campoTelefone.getText().trim(), campoEmail.getText().trim());
        repositorios.fornecedores.save(fornecedor);
        limparFormulario();
        atualizar();
    }

    private void excluir() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para excluir.");
            return;
        }
        try {
            repositorios.fornecedores.deleteById(idSelecionado);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    "Nao e possivel excluir: existem produtos vinculados a este fornecedor.",
                    "Exclusao bloqueada", JOptionPane.ERROR_MESSAGE);
            return;
        }
        limparFormulario();
        atualizar();
    }

    private boolean validarFormulario() {
        if (campoNomeEmpresa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da empresa.", "Dados invalidos",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void carregarSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = (Integer) modeloTabela.getValueAt(linha, 0);
        campoNomeEmpresa.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        campoTelefone.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        campoEmail.setText(String.valueOf(modeloTabela.getValueAt(linha, 3)));
    }

    private void limparFormulario() {
        idSelecionado = null;
        campoNomeEmpresa.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        tabela.clearSelection();
    }

    @Override
    public void atualizar() {
        modeloTabela.setRowCount(0);
        for (Fornecedor fornecedor : repositorios.fornecedores.findAll()) {
            modeloTabela.addRow(new Object[] {
                fornecedor.getIdFornecedor(), fornecedor.getNomeEmpresa(),
                fornecedor.getTelefoneContato(), fornecedor.getEmailContato()
            });
        }
    }
}
