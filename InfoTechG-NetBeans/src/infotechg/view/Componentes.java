package infotechg.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Fábrica de componentes Swing com a aparência padrão do sistema, evitando
 * duplicar a estilização (fontes/cores do {@link UiTheme}) em cada painel.
 */
final class Componentes {

    private Componentes() {
    }

    static JButton botao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(UiTheme.FONTE_PADRAO);
        botao.setFocusPainted(false);
        return botao;
    }

    static JButton botaoPrimario(String texto) {
        JButton botao = botao(texto);
        botao.setBackground(UiTheme.PRIMARIO);
        botao.setForeground(UiTheme.BRANCO);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        return botao;
    }

    static JLabel rotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(UiTheme.FONTE_ROTULO);
        rotulo.setForeground(UiTheme.TEXTO);
        return rotulo;
    }

    static JLabel tituloPainel(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(UiTheme.FONTE_TITULO);
        titulo.setForeground(UiTheme.PRIMARIO_ESCURO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return titulo;
    }

    static JPanel campoComRotulo(String rotuloTexto, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(4, 2));
        painel.setOpaque(false);
        JLabel rotulo = rotulo(rotuloTexto);
        rotulo.setHorizontalAlignment(SwingConstants.LEFT);
        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        return painel;
    }

    static DefaultTableModel modeloTabelaSomenteLeitura(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }
}
