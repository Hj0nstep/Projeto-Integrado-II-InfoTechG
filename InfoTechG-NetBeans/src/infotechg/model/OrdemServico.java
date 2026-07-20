package infotechg.model;

import java.time.LocalDateTime;

public class OrdemServico {

    private int idOS;
    private LocalDateTime dataAbertura;
    private String defeitoRelatado;
    private StatusOS status;
    private Cliente cliente;
    private Usuario tecnicoResponsavel;

    public OrdemServico() {
    }

    public OrdemServico(int idOS, LocalDateTime dataAbertura, String defeitoRelatado,
                         Cliente cliente, Usuario tecnicoResponsavel) {
        this.idOS = idOS;
        this.dataAbertura = dataAbertura;
        this.defeitoRelatado = defeitoRelatado;
        this.cliente = cliente;
        this.tecnicoResponsavel = tecnicoResponsavel;
        this.status = StatusOS.EM_ANALISE;
    }

    public void atualizarStatus(StatusOS novoStatus) {
        this.status = novoStatus;
    }

    public int getIdOS() {
        return idOS;
    }

    public void setIdOS(int idOS) {
        this.idOS = idOS;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getDefeitoRelatado() {
        return defeitoRelatado;
    }

    public void setDefeitoRelatado(String defeitoRelatado) {
        this.defeitoRelatado = defeitoRelatado;
    }

    public StatusOS getStatus() {
        return status;
    }

    public void setStatus(StatusOS status) {
        this.status = status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getTecnicoResponsavel() {
        return tecnicoResponsavel;
    }

    public void setTecnicoResponsavel(Usuario tecnicoResponsavel) {
        this.tecnicoResponsavel = tecnicoResponsavel;
    }

    @Override
    public String toString() {
        return "OS #" + idOS + " - " + cliente + " [" + status + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrdemServico other)) {
            return false;
        }
        return idOS == other.idOS;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idOS);
    }
}
