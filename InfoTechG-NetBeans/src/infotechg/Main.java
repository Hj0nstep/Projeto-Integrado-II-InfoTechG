package infotechg;

import infotechg.repo.Repositorios;
import infotechg.view.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Repositorios repositorios = new Repositorios();
        SwingUtilities.invokeLater(() -> new LoginFrame(repositorios).setVisible(true));
    }
}
