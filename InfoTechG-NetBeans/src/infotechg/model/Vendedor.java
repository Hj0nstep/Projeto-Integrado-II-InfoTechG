package infotechg.model;

import java.util.List;

/**
 * Perfil focado em vendas: cadastra clientes, consulta produtos e registra vendas.
 * Nao enxerga o cadastro de Fornecedores nem Ordens de Servico.
 */
public class Vendedor extends Usuario {

    public Vendedor(int idUsuario, String login, String senhaHash, String nomeCompleto) {
        super(idUsuario, login, senhaHash, nomeCompleto);
    }

    @Override
    public List<String> getPermissoes() {
        return List.of(
                Modulos.CLIENTES,
                Modulos.PRODUTOS,
                Modulos.VENDAS
        );
    }

    @Override
    public String getTipoPerfil() {
        return "Vendedor";
    }
}
