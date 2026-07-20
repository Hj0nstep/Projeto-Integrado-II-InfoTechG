package infotechg.model;

public class Cliente {

    private int idCliente;
    private String nome;
    private String telefone;
    private String email;

    public Cliente() {
    }

    public Cliente(int idCliente, String nome, String telefone, String email) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return idCliente + " - " + nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente other)) {
            return false;
        }
        return idCliente == other.idCliente;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idCliente);
    }
}
