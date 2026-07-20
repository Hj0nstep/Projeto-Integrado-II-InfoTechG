package infotechg.view;

/**
 * Implementada pelos painéis que precisam recarregar dados (tabelas,
 * combos) sempre que voltam a ficar visíveis, buscando o estado atual
 * direto do banco de dados.
 */
public interface Atualizavel {

    void atualizar();
}
