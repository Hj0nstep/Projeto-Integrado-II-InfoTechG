package infotechg.model;

import infotechg.util.SenhaUtil;
import java.util.List;

/**
 * Representa um usuario do sistema InfoTechG.
 * Cada perfil concreto (Gerente, Vendedor, Tecnico) define suas proprias permissoes.
 */
public abstract class Usuario {

    private int idUsuario;
    private String login;
    private String senhaHash;
    private String nomeCompleto;

    protected Usuario(int idUsuario, String login, String senhaHash, String nomeCompleto) {
        this.idUsuario = idUsuario;
        this.login = login;
        this.senhaHash = senhaHash;
        this.nomeCompleto = nomeCompleto;
    }

    /**
     * Verifica a senha em texto puro informada no login contra o hash
     * (SHA-256 + salt) armazenado em {@link #senhaHash}.
     */
    public boolean autenticar(String senhaInformada) {
        return SenhaUtil.verificar(senhaInformada, this.senhaHash);
    }

    /**
     * Lista dos nomes de modulo que este perfil pode acessar.
     * Cada subclasse decide quais modulos libera.
     */
    public abstract List<String> getPermissoes();

    public boolean podeAcessar(String modulo) {
        return getPermissoes().contains(modulo);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public abstract String getTipoPerfil();

    @Override
    public String toString() {
        return nomeCompleto + " (" + getTipoPerfil() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario other)) {
            return false;
        }
        return idUsuario == other.idUsuario;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idUsuario);
    }
}
