package infotechg;

import infotechg.repo.Repositorios;
import infotechg.view.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Classe principal do sistema InfoTechG.
 *
 * Inicia a interface grafica Swing com os repositorios JDBC conectados
 * ao MySQL (schema mydb).
 */
public class Main {

    public static void main(String[] args) {
        Repositorios repositorios = new Repositorios();
        SwingUtilities.invokeLater(() -> new LoginFrame(repositorios).setVisible(true));
    }
}
