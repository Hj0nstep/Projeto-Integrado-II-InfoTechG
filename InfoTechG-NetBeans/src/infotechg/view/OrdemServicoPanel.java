package infotechg.view;

import infotechg.model.Cliente;
import infotechg.model.OrdemServico;
import infotechg.model.StatusOS;
import infotechg.model.Usuario;
import infotechg.repo.Repositorios;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
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

public class OrdemServicoPanel extends JPanel implements Atualizavel {

    private final Repositorios repositorios;
    private final Usuario usuarioLogado;

    private final JComboBox<Cliente> comboCliente = new JComboBox<>();
    private final JTextField campoDefeito = new JTextField();
    private final JComboBox<StatusOS> comboStatus = new JComboBox<>(StatusOS.values());

    private final DefaultTableModel modeloTabela =
            Componentes.modeloTabelaSomenteLeitura("ID", "Cliente", "Defeito relatado", "Status");
    private final JTable tabela = new JTable(modeloTabela);

    private Integer idSelecionado;

    public OrdemServicoPanel(Repositorios repositorios, Usuario usuarioLogado) {
        this.repositorios = repositorios;
        this.usuarioLogado = usuarioLogado;
        montarTela();
    }

    private void montarTela() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UiTheme.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(Componentes.tituloPainel("Ordens de Servico"), BorderLayout.NORTH);
        add(criarFormulario(), BorderLayout.WEST);
        add(criarTabela(), BorderLayout.CENTER);
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        formulario.setPreferredSize(new java.awt.Dimension(280, 0));

        formulario.add(Componentes.campoComRotulo("Cliente", comboCliente));
        formulario.add(Componentes.campoComRotulo("Defeito relatado", campoDefeito));
        formulario.add(Componentes.campoComRotulo("Status", comboStatus));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 12));
        botoes.setOpaque(false);
        var botaoAbrir = Componentes.botaoPrimario("Abrir OS");
        var botaoAtualizarStatus = Componentes.botao("Atualizar Status");
        var botaoCancelar = Componentes.botao("Cancelar");
        botoes.add(botaoAbrir);
        botoes.add(botaoAtualizarStatus);
        botoes.add(botaoCancelar);
        formulario.add(botoes);

        botaoAbrir.addActionListener(e -> abrirOS());
        botaoAtualizarStatus.addActionListener(e -> atualizarStatus());
        botaoCancelar.addActionListener(e -> limparFormulario());

        return formulario;
    }

    private JScrollPane criarTabela() {
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new RendererStatus());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecao();
            }
        });
        return new JScrollPane(tabela);
    }

    private void abrirOS() {
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        String defeito = campoDefeito.getText().trim();
        if (cliente == null || defeito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o cliente e o defeito relatado.", "Dados invalidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        repositorios.ordensServico.abrir(defeito, cliente, usuarioLogado);
        limparFormulario();
        atualizar();
    }

    private void atualizarStatus() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma OS na tabela para atualizar o status.");
            return;
        }
        OrdemServico os = repositorios.ordensServico.findById(idSelecionado).orElse(null);
        if (os == null) {
            return;
        }
        StatusOS novoStatus = (StatusOS) comboStatus.getSelectedItem();
        os.atualizarStatus(novoStatus);
        repositorios.ordensServico.save(os);
        limparFormulario();
        atualizar();
    }

    private void carregarSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = (Integer) modeloTabela.getValueAt(linha, 0);
        OrdemServico os = repositorios.ordensServico.findById(idSelecionado).orElse(null);
        if (os == null) {
            return;
        }
        comboCliente.setSelectedItem(os.getCliente());
        campoDefeito.setText(os.getDefeitoRelatado());
        comboStatus.setSelectedItem(os.getStatus());
    }

    private void limparFormulario() {
        idSelecionado = null;
        campoDefeito.setText("");
        if (comboCliente.getItemCount() > 0) {
            comboCliente.setSelectedIndex(0);
        }
        comboStatus.setSelectedItem(StatusOS.EM_ANALISE);
        tabela.clearSelection();
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

        modeloTabela.setRowCount(0);
        for (OrdemServico os : repositorios.ordensServico.findAll()) {
            modeloTabela.addRow(new Object[] {
                os.getIdOS(), os.getCliente(), os.getDefeitoRelatado(), os.getStatus()
            });
        }
    }

    private static class RendererStatus extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component componente = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
            if (value instanceof StatusOS status && !isSelected) {
                setForeground(switch (status) {
                    case EM_ANALISE -> new java.awt.Color(0x15, 0x65, 0xC0);
                    case AGUARDANDO_PECA -> UiTheme.ALERTA;
                    case CONCLUIDO -> UiTheme.SUCESSO;
                    case ENTREGUE -> UiTheme.TEXTO_SECUNDARIO;
                });
            } else if (isSelected) {
                setForeground(UiTheme.BRANCO);
            }
            return componente;
        }
    }
}
