package infotechg.model;

/**
 * Empresa fornecedora de pecas (RF004). Visivel apenas ao Gerente.
 */
public class Fornecedor {

    private int idFornecedor;
    private String nomeEmpresa;
    private String telefoneContato;
    private String emailContato;

    public Fornecedor() {
    }

    public Fornecedor(int idFornecedor, String nomeEmpresa, String telefoneContato, String emailContato) {
        this.idFornecedor = idFornecedor;
        this.nomeEmpresa = nomeEmpresa;
        this.telefoneContato = telefoneContato;
        this.emailContato = emailContato;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    @Override
    public String toString() {
        return idFornecedor + " - " + nomeEmpresa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Fornecedor other)) {
            return false;
        }
        return idFornecedor == other.idFornecedor;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idFornecedor);
    }
}
