package hash;

/**
 * "Contrato" de uma função hash: transformar uma chave (texto) em um índice de balde.
 * Com uma interface, a tabela funciona com qualquer função — dá para trocar a função sem mexer na tabela.
 */
public interface FuncaoHash {

    /** Calcula o índice do balde. Sempre devolve um número em [0, m). */
    int calcular(String chave, int m);

    /** Nome da função, para mostrar nas tabelas comparativas. */
    String getNome();
}
