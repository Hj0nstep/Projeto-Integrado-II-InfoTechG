package infotechg.model;

import java.util.List;

public class Tecnico extends Usuario {

    public Tecnico(int idUsuario, String login, String senhaHash, String nomeCompleto) {
        super(idUsuario, login, senhaHash, nomeCompleto);
    }

    @Override
    public List<String> getPermissoes() {
        return List.of(
                Modulos.CLIENTES,
                Modulos.PRODUTOS,
                Modulos.ORDENS_SERVICO
        );
    }

    @Override
    public String getTipoPerfil() {
        return "Tecnico";
    }
}
