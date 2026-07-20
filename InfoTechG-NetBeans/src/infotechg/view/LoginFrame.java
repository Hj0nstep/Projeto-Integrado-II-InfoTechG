package infotechg.view;

import infotechg.repo.Repositorios;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginFrame extends JFrame {

    private final Repositorios repositorios;
    private final JTextField campoUsuario = new JTextField(18);
    private final JPasswordField campoSenha = new JPasswordField(18);

    public LoginFrame(Repositorios repositorios) {
        super("InfoTechG - Login");
        this.repositorios = repositorios;
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(UiTheme.FUNDO);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.gridwidth = 2;
        c.gridx = 0;

        JLabel titulo = new JLabel("InfoTechG", SwingConstants.CENTER);
        titulo.setFont(UiTheme.FONTE_TITULO);
        titulo.setForeground(UiTheme.PRIMARIO_ESCURO);
        c.gridy = 0;
        painel.add(titulo, c);

        JLabel subtitulo = new JLabel("Gestao de assistencia tecnica", SwingConstants.CENTER);
        subtitulo.setFont(UiTheme.FONTE_PADRAO);
        subtitulo.setForeground(UiTheme.TEXTO_SECUNDARIO);
        c.gridy = 1;
        painel.add(subtitulo, c);

        c.gridwidth = 1;
        c.gridy = 2;
        c.gridx = 0;
        painel.add(Componentes.rotulo("Usuario:"), c);
        c.gridx = 1;
        painel.add(campoUsuario, c);

        c.gridy = 3;
        c.gridx = 0;
        painel.add(Componentes.rotulo("Senha:"), c);
        c.gridx = 1;
        painel.add(campoSenha, c);

        JCheckBox checkLembrar = new JCheckBox("Lembrar usuario");
        checkLembrar.setOpaque(false);
        c.gridy = 4;
        c.gridx = 0;
        c.gridwidth = 2;
        painel.add(checkLembrar, c);

        JLabel dica = new JLabel("<html><i>Dica: gerente/gerente123, vendedor/vendedor123, tecnico/tecnico123</i></html>");
        dica.setFont(dica.getFont().deriveFont(11f));
        dica.setForeground(UiTheme.TEXTO_SECUNDARIO);
        c.gridy = 5;
        painel.add(dica, c);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setOpaque(false);
        JButton botaoEntrar = Componentes.botaoPrimario("Entrar");
        JButton botaoCancelar = Componentes.botao("Cancelar");
        painelBotoes.add(botaoEntrar);
        painelBotoes.add(botaoCancelar);
        c.gridy = 6;
        painel.add(painelBotoes, c);

        botaoEntrar.addActionListener(e -> autenticar());
        botaoCancelar.addActionListener(e -> System.exit(0));
        campoSenha.addActionListener(e -> autenticar());

        setContentPane(painel);
        pack();
        setLocationRelativeTo(null);
    }

    private void autenticar() {
        String login = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword());

        repositorios.usuarios.autenticar(login, senha).ifPresentOrElse(
                usuario -> {
                    dispose();
                    new MainFrame(repositorios, usuario).setVisible(true);
                },
                () -> JOptionPane.showMessageDialog(this,
                        "Usuario ou senha invalidos.",
                        "Falha no login",
                        JOptionPane.ERROR_MESSAGE)
        );
    }
}
