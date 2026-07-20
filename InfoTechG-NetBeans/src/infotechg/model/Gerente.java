package infotechg.model;

import java.util.List;

public class Gerente extends Usuario {

    public Gerente(int idUsuario, String login, String senhaHash, String nomeCompleto) {
        super(idUsuario, login, senhaHash, nomeCompleto);
    }

    @Override
    public List<String> getPermissoes() {
        return List.of(
                Modulos.CLIENTES,
                Modulos.PRODUTOS,
                Modulos.FORNECEDORES,
                Modulos.VENDAS,
                Modulos.ORDENS_SERVICO
        );
    }

    @Override
    public String getTipoPerfil() {
        return "Gerente";
    }
}
